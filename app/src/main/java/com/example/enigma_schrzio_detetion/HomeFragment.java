package com.example.enigma_schrzio_detetion;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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

public class HomeFragment extends Fragment {

    private ActivityResultLauncher<Intent> reportPickerLauncher;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register file picker result handler
        reportPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK
                            && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        String fileName = getFileNameFromUri(fileUri);
                        Toast.makeText(getContext(),
                                "Report selected: " + fileName,
                                Toast.LENGTH_LONG).show();
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

    /** Launches the system file picker for PDFs and images. */
    private void openReportPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Accept PDFs and common image types
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,
                new String[] { "application/pdf", "image/jpeg", "image/png", "image/jpg" });
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        reportPickerLauncher.launch(Intent.createChooser(intent, "Select Medical Report"));
    }

    /** Extracts a human-readable file name from a content URI. */
    private String getFileNameFromUri(Uri uri) {
        if (uri == null)
            return "Unknown file";
        String name = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = requireContext().getContentResolver()
                    .query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx >= 0)
                        name = cursor.getString(idx);
                }
            } catch (Exception ignored) {
            }
        }
        if (name == null) {
            name = uri.getLastPathSegment();
        }
        return name != null ? name : "Unknown file";
    }
}
