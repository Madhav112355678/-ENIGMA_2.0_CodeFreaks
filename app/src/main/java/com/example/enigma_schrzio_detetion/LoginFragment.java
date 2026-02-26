package com.example.enigma_schrzio_detetion;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvGoToRegister;

    /** Callback interface so the activity can switch to the Register tab */
    public interface OnSwitchToRegisterListener {
        void onSwitchToRegister();
    }

    private OnSwitchToRegisterListener switchListener;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        tvGoToRegister = view.findViewById(R.id.tvGoToRegister);

        // Resolve parent activity listener
        if (getActivity() instanceof OnSwitchToRegisterListener) {
            switchListener = (OnSwitchToRegisterListener) getActivity();
        }

        // Login button click
        btnLogin.setOnClickListener(v -> validateAndLogin());

        // Forgot password
        tvForgotPassword.setOnClickListener(
                v -> Toast.makeText(getContext(), "Password reset coming soon!", Toast.LENGTH_SHORT).show());

        // Switch to Register
        tvGoToRegister.setOnClickListener(v -> {
            if (switchListener != null) {
                switchListener.onSwitchToRegister();
            }
        });
    }

    private void validateAndLogin() {
        boolean valid = true;

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // Email
        tilEmail.setError(null);
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.err_empty_email));
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.err_invalid_email));
            valid = false;
        }

        // Password
        tilPassword.setError(null);
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.err_empty_password));
            valid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.err_short_password));
            valid = false;
        }

        if (valid) {
            // Bypass Firebase Auth for now
            android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs",
                    android.content.Context.MODE_PRIVATE);
            String existingName = prefs.getString("userName", null);

            // If they haven't registered, create some dummy data so the profile works
            if (existingName == null) {
                android.content.SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userName", "Guest User");
                editor.putString("userEmail", email);
                editor.putString("userAge", "25");
                editor.putString("userMobile", "0000000000");
                editor.putBoolean("isLoggedIn", true);
                editor.apply();
                existingName = "Guest User";
            } else {
                // Just mark logged in
                prefs.edit().putBoolean("isLoggedIn", true).apply();
            }

            Toast.makeText(getContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show();

            // Navigate directly to MainActivity (Home)
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("userName", existingName);
            startActivity(intent);
            requireActivity().finish(); // Close auth activity
        }
    }
}
