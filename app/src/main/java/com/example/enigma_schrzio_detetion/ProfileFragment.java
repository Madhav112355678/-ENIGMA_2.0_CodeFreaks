package com.example.enigma_schrzio_detetion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail, tvProfileAge, tvProfileMobile;
    private Button btnLogout;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfileAge = view.findViewById(R.id.tvProfileAge);
        tvProfileMobile = view.findViewById(R.id.tvProfileMobile);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Load data from SharedPreferences (bypass auth data)
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        tvProfileName.setText(prefs.getString("userName", "Guest"));
        tvProfileEmail.setText(prefs.getString("userEmail", "Not provided"));
        tvProfileAge.setText(prefs.getString("userAge", "N/A"));
        tvProfileMobile.setText(prefs.getString("userMobile", "N/A"));

        // Logout button
        btnLogout.setOnClickListener(v -> {
            // Clear local data
            prefs.edit().clear().apply();

            // Navigate back to Login
            Intent intent = new Intent(getActivity(), Authentication.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
