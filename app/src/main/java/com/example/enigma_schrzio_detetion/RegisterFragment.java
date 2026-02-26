package com.example.enigma_schrzio_detetion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    private TextInputEditText etFullName, etAge,
            etEmail, etMobile, etPassword, etConfirmPassword;

    private TextInputLayout tilEmail, tilPassword, tilConfirmPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    public interface OnSwitchToLoginListener {
        void onSwitchToLogin();
    }

    private OnSwitchToLoginListener listener;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
            Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etFullName = view.findViewById(R.id.etFullName);
        etAge = view.findViewById(R.id.etAge);
        etEmail = view.findViewById(R.id.etEmail);
        etMobile = view.findViewById(R.id.etMobile);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPassword);

        btnRegister = view.findViewById(R.id.btnRegister);
        tvGoToLogin = view.findViewById(R.id.tvGoToLogin);

        listener = (OnSwitchToLoginListener) getActivity();

        btnRegister.setOnClickListener(v -> registerUser());

        tvGoToLogin.setOnClickListener(v -> {
            if (listener != null)
                listener.onSwitchToLogin();
        });
    }

    private void registerUser() {

        String name = get(etFullName);
        String age = get(etAge);
        String email = get(etEmail);
        String mobile = get(etMobile);
        String pass = get(etPassword);
        String confirm = get(etConfirmPassword);

        if (TextUtils.isEmpty(email)
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid Email");
            return;
        }

        if (pass.length() < 6) {
            tilPassword.setError("Min 6 chars");
            return;
        }

        if (!pass.equals(confirm)) {
            tilConfirmPassword.setError("Password mismatch");
            return;
        }

        RadioGroup rgRole = getView().findViewById(R.id.rgRole);
        String role = "patient";
        if (rgRole.getCheckedRadioButtonId() == R.id.rbDoctor) {
            role = "doctor";
        }

        btnRegister.setEnabled(false);

        String finalRole = role; // Need final for lambda
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("username", name);
                        userMap.put("email", email);
                        userMap.put("mobilenumber", mobile);
                        userMap.put("age", age);
                        userMap.put("password", pass); // Saving password as requested
                        userMap.put("role", finalRole);

                        db.collection("Users").document(uid).set(userMap)
                                .addOnSuccessListener(aVoid -> {
                                    // Also save to SharedPreferences for ProfileFragment to use
                                    SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs",
                                            Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("userName", name);
                                    editor.putString("userEmail", email);
                                    editor.putString("userAge", age);
                                    editor.putString("userMobile", mobile);
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.apply();

                                    Toast.makeText(getContext(), "Account Created", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                    requireActivity().finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to save user data: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                    btnRegister.setEnabled(true);
                                });
                    } else {
                        Toast.makeText(getContext(), "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        btnRegister.setEnabled(true);
                    }
                });
    }

    private String get(TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }
}