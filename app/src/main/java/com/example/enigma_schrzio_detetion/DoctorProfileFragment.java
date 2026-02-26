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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorProfileFragment extends Fragment {

    public DoctorProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvDoctorName = view.findViewById(R.id.tvDoctorName);
        TextView tvDoctorEmail = view.findViewById(R.id.tvDoctorEmail);
        TextView tvDoctorMobile = view.findViewById(R.id.tvDoctorMobile);
        TextView tvDoctorAge = view.findViewById(R.id.tvDoctorAge);
        TextView tvAvatarInitials = view.findViewById(R.id.tvAvatarInitials);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String name = prefs.getString("userName", "Doctor");
            String email = prefs.getString("userEmail", "Not Provided");
            String mobile = prefs.getString("userMobile", "Not Provided");
            String age = prefs.getString("userAge", "Not Provided");

            tvDoctorName.setText("Dr. " + name);
            tvDoctorEmail.setText(email);
            tvDoctorMobile.setText(mobile);
            tvDoctorAge.setText(age);

            if (!name.isEmpty()) {
                tvAvatarInitials.setText(String.valueOf(name.charAt(0)).toUpperCase());
            } else {
                tvAvatarInitials.setText("DR");
            }
        }

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            if (getContext() != null) {
                SharedPreferences prefs = getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                prefs.edit().clear().apply();
            }

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}
