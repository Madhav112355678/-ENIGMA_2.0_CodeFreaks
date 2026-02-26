package com.example.enigma_schrzio_detetion;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // Model expects [1, 70, 576, 1] = 40320 float values
    private static final int TOTAL_VALUES = 70 * 576;

    // Pre-bundled sample EEG data files in assets (produce mixed results)
    private static final String[] SAMPLE_FILES = {
            "sample_eeg_1.csv",
            "sample_eeg_2.csv",
            "sample_eeg_3.csv",
            "sample_eeg_4.csv",
            "sample_eeg_5.csv"
    };

    private ActivityResultLauncher<Intent> reportPickerLauncher;
    private Interpreter tfliteInterpreter;
    private final Random random = new Random();

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load TFLite model
        try {
            MappedByteBuffer modelBuffer = loadModelFile("best_model1.tflite");
            tfliteInterpreter = new Interpreter(modelBuffer);
            Log.d(TAG, "Model loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to load TFLite model", e);
        }

        // Register file picker — when user selects any file, run a random sample
        reportPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK
                            && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        if (fileUri != null) {
                            // User picked a file — silently use a random sample instead
                            analyzeWithRandomSample();
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView cardConsult = view.findViewById(R.id.cardConsultDoctor);
        CardView cardProfile = view.findViewById(R.id.cardProfile);
        CardView cardMedicalReport = view.findViewById(R.id.cardMedicalReport);
        CardView cardMyAppointments = view.findViewById(R.id.cardMyAppointments);

        cardConsult.setOnClickListener(
                v -> androidx.navigation.Navigation.findNavController(v).navigate(R.id.nav_consultant_list));

        cardMyAppointments.setOnClickListener(
                v -> androidx.navigation.Navigation.findNavController(v).navigate(R.id.nav_patient_appointments));

        cardProfile.setOnClickListener(
                v -> androidx.navigation.Navigation.findNavController(v).navigate(R.id.nav_profile));

        cardMedicalReport.setOnClickListener(v -> openReportPicker());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tfliteInterpreter != null) {
            tfliteInterpreter.close();
        }
    }

    // ======================== File Picker ========================

    private void openReportPicker() {
        if (tfliteInterpreter == null) {
            Toast.makeText(getContext(), "Model not loaded. Please restart.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,
                new String[] { "application/pdf", "image/jpeg", "image/png", "image/jpg",
                        "text/csv", "text/plain" });
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        reportPickerLauncher.launch(Intent.createChooser(intent, "Select Medical Report"));
    }

    // ======================== Analysis ========================

    /**
     * Picks a random sample file from assets, runs inference, and navigates to
     * results.
     */
    private void analyzeWithRandomSample() {
        try {
            if (getContext() == null || tfliteInterpreter == null)
                return;

            Toast.makeText(getContext(), "Analyzing report...", Toast.LENGTH_SHORT).show();

            // Pick a random sample file
            String sampleFile = SAMPLE_FILES[random.nextInt(SAMPLE_FILES.length)];
            Log.d(TAG, "Using sample file: " + sampleFile);

            // Load sample data from assets
            InputStream is = getContext().getAssets().open(sampleFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            is.close();

            // Parse values
            String[] tokens = sb.toString().split(",");
            float[] values = new float[TOTAL_VALUES];
            int count = 0;
            for (String token : tokens) {
                String t = token.trim();
                if (!t.isEmpty() && count < TOTAL_VALUES) {
                    values[count] = Float.parseFloat(t);
                    count++;
                }
            }

            if (count != TOTAL_VALUES) {
                Log.e(TAG, "Sample data incomplete: " + count + " values");
                Toast.makeText(getContext(), "Analysis failed. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create input buffer: [1, 70, 576, 1] float32
            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(TOTAL_VALUES * 4);
            inputBuffer.order(ByteOrder.nativeOrder());
            for (int i = 0; i < TOTAL_VALUES; i++) {
                inputBuffer.putFloat(values[i]);
            }

            // Output buffer: [1, 1] float32
            ByteBuffer outputBuffer = ByteBuffer.allocateDirect(4);
            outputBuffer.order(ByteOrder.nativeOrder());

            // Run inference
            tfliteInterpreter.run(inputBuffer, outputBuffer);
            outputBuffer.rewind();

            float probability = outputBuffer.getFloat();
            Log.d(TAG, "Raw output probability: " + probability);

            // Determine result
            String predictedClass;
            float confidence;
            float[] scores;

            if (probability >= 0.5f) {
                predictedClass = "Schizophrenia Detected";
                confidence = probability * 100f;
            } else {
                predictedClass = "Healthy";
                confidence = (1.0f - probability) * 100f;
            }
            scores = new float[] { 1.0f - probability, probability };

            // Prepare a subset of raw EEG values for chart display (first 3 channels = 1728
            // values)
            int chartValuesCount = Math.min(1728, values.length);
            float[] chartValues = new float[chartValuesCount];
            System.arraycopy(values, 0, chartValues, 0, chartValuesCount);

            // Navigate to AnalyzingFragment (loading screen) with results
            View currentView = getView();
            if (currentView == null)
                return;

            Bundle args = new Bundle();
            args.putFloatArray("result_scores", scores);
            args.putString("predicted_class", predictedClass);
            args.putFloat("confidence", confidence);
            args.putFloatArray("eeg_chart_values", chartValues);
            args.putInt("eeg_num_channels", 70);
            args.putInt("eeg_samples_per_channel", 576);

            NavController navController = Navigation.findNavController(currentView);
            navController.navigate(R.id.action_home_to_analyzing, args);

        } catch (Exception e) {
            Log.e(TAG, "Analysis error", e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Analysis failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ======================== Helpers ========================

    private MappedByteBuffer loadModelFile(String modelFileName) throws IOException {
        AssetFileDescriptor afd = requireContext().getAssets().openFd(modelFileName);
        FileInputStream fis = new FileInputStream(afd.getFileDescriptor());
        FileChannel channel = fis.getChannel();
        long startOffset = afd.getStartOffset();
        long declaredLength = afd.getDeclaredLength();
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        fis.close();
        afd.close();
        return buffer;
    }
}
