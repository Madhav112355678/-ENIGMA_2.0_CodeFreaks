package com.example.enigma_schrzio_detetion;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Random;

public class AnalyzingFragment extends Fragment {

    private Handler handler;
    private final Random random = new Random();
    private boolean navigated = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analyzing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handler = new Handler(Looper.getMainLooper());

        // UI references
        View pulseRing = view.findViewById(R.id.pulseRing);
        ProgressBar progressBar = view.findViewById(R.id.progressAnalyzing);
        TextView tvPercent = view.findViewById(R.id.tvProgressPercent);
        TextView tvStep1 = view.findViewById(R.id.tvStep1);
        TextView tvStep2 = view.findViewById(R.id.tvStep2);
        TextView tvStep3 = view.findViewById(R.id.tvStep3);
        TextView tvStep4 = view.findViewById(R.id.tvStep4);

        // Random total duration between 10-16 seconds
        int totalDurationMs = 10000 + random.nextInt(6001);

        // Pulse animation on the ring
        ObjectAnimator pulseAnim = ObjectAnimator.ofFloat(pulseRing, "scaleX", 1f, 1.3f, 1f);
        pulseAnim.setDuration(1500);
        pulseAnim.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseAnim.start();

        ObjectAnimator pulseAnimY = ObjectAnimator.ofFloat(pulseRing, "scaleY", 1f, 1.3f, 1f);
        pulseAnimY.setDuration(1500);
        pulseAnimY.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimY.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseAnimY.start();

        // Animate progress bar from 0 to 100 over totalDuration
        ValueAnimator progressAnimator = ValueAnimator.ofInt(0, 100);
        progressAnimator.setDuration(totalDurationMs);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.addUpdateListener(animation -> {
            int val = (int) animation.getAnimatedValue();
            progressBar.setProgress(val);
            tvPercent.setText(val + "%");
        });
        progressAnimator.start();

        // Step timings: each step activates at ~25% intervals
        int step1Delay = totalDurationMs / 10; // ~10%
        int step2Delay = totalDurationMs * 3 / 10; // ~30%
        int step3Delay = totalDurationMs * 55 / 100; // ~55%
        int step4Delay = totalDurationMs * 8 / 10; // ~80%

        // Activate steps sequentially
        handler.postDelayed(() -> activateStep(tvStep1, "✅  Preprocessing EEG signals..."), step1Delay);
        handler.postDelayed(() -> activateStep(tvStep2, "✅  Extracting spectral features..."), step2Delay);
        handler.postDelayed(() -> activateStep(tvStep3, "✅  Running neural network analysis..."), step3Delay);
        handler.postDelayed(() -> activateStep(tvStep4, "✅  Generating diagnostic report..."), step4Delay);

        // Navigate to StatisticsFragment after totalDuration
        handler.postDelayed(() -> {
            if (!navigated && isAdded()) {
                navigated = true;
                Bundle args = getArguments();
                if (args == null)
                    args = new Bundle();

                View v = getView();
                if (v != null) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_analyzing_to_stats, args);
                }
            }
        }, totalDurationMs + 500); // small buffer after 100%
    }

    private void activateStep(TextView tv, String completedText) {
        if (!isAdded())
            return;
        tv.animate().alpha(1f).setDuration(400).start();
        tv.setText(completedText);
        tv.setTextColor(getResources().getColor(R.color.green_primary, null));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
