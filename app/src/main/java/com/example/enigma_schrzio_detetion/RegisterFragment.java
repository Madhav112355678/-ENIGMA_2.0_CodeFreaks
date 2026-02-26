package com.example.enigma_schrzio_detetion;

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

public class RegisterFragment extends Fragment {

    private TextInputLayout tilFullName, tilAge, tilEmail, tilMobile, tilPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etAge, etEmail, etMobile, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    /** Callback interface so the activity can switch back to the Login tab */
    public interface OnSwitchToLoginListener {
        void onSwitchToLogin();
    }

    private OnSwitchToLoginListener switchListener;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        tilFullName = view.findViewById(R.id.tilFullName);
        tilAge = view.findViewById(R.id.tilAge);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilMobile = view.findViewById(R.id.tilMobile);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPassword);

        etFullName = view.findViewById(R.id.etFullName);
        etAge = view.findViewById(R.id.etAge);
        etEmail = view.findViewById(R.id.etEmail);
        etMobile = view.findViewById(R.id.etMobile);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        btnRegister = view.findViewById(R.id.btnRegister);
        tvGoToLogin = view.findViewById(R.id.tvGoToLogin);

        // Resolve parent activity listener
        if (getActivity() instanceof OnSwitchToLoginListener) {
            switchListener = (OnSwitchToLoginListener) getActivity();
        }

        // Register button click
        btnRegister.setOnClickListener(v -> validateAndRegister());

        // Switch to Login
        tvGoToLogin.setOnClickListener(v -> {
            if (switchListener != null) {
                switchListener.onSwitchToLogin();
            }
        });
    }

    private void validateAndRegister() {
        boolean valid = true;

        String fullName = getText(etFullName);
        String ageStr = getText(etAge);
        String email = getText(etEmail);
        String mobile = getText(etMobile);
        String password = getText(etPassword);
        String confirmPassword = getText(etConfirmPassword);

        // -- Full Name --
        tilFullName.setError(null);
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError(getString(R.string.err_empty_name));
            valid = false;
        }

        // -- Age --
        tilAge.setError(null);
        if (TextUtils.isEmpty(ageStr)) {
            tilAge.setError(getString(R.string.err_empty_age));
            valid = false;
        } else {
            try {
                int age = Integer.parseInt(ageStr);
                if (age < 1 || age > 120) {
                    tilAge.setError(getString(R.string.err_invalid_age));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                tilAge.setError(getString(R.string.err_invalid_age));
                valid = false;
            }
        }

        // -- Email --
        tilEmail.setError(null);
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.err_empty_email));
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.err_invalid_email));
            valid = false;
        }

        // -- Mobile --
        tilMobile.setError(null);
        if (TextUtils.isEmpty(mobile)) {
            tilMobile.setError(getString(R.string.err_empty_mobile));
            valid = false;
        } else if (mobile.length() != 10 || !mobile.matches("\\d{10}")) {
            tilMobile.setError(getString(R.string.err_invalid_mobile));
            valid = false;
        }

        // -- Password --
        tilPassword.setError(null);
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.err_empty_password));
            valid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.err_short_password));
            valid = false;
        }

        // -- Confirm Password --
        tilConfirmPassword.setError(null);
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.err_empty_confirm));
            valid = false;
        } else if (!confirmPassword.equals(password)) {
            tilConfirmPassword.setError(getString(R.string.err_password_mismatch));
            valid = false;
        }

        if (valid) {
            // Bypass Firebase Auth for now - Save to SharedPreferences
            android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs",
                    android.content.Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            editor.putString("userName", fullName);
            editor.putString("userEmail", email);
            editor.putString("userAge", ageStr);
            editor.putString("userMobile", mobile);
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

            Toast.makeText(getContext(), getString(R.string.register_success), Toast.LENGTH_SHORT).show();

            // Navigate directly to MainActivity (Home)
            android.content.Intent intent = new android.content.Intent(getActivity(), MainActivity.class);
            intent.putExtra("userName", fullName);
            startActivity(intent);
            requireActivity().finish(); // Close auth activity
        }
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
