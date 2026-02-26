package com.example.enigma_schrzio_detetion;

import android.content.*;
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

public class LoginFragment extends Fragment {

    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    public interface OnSwitchToRegisterListener {
        void onSwitchToRegister();
    }

    private OnSwitchToRegisterListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_login,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
            Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvGoToRegister = view.findViewById(R.id.tvGoToRegister);

        listener = (OnSwitchToRegisterListener) getActivity();

        btnLogin.setOnClickListener(v -> loginUser());

        tvGoToRegister.setOnClickListener(v -> {
            if (listener != null)
                listener.onSwitchToRegister();
        });
    }

    private void loginUser() {

        String email = get(etEmail);
        String pass = get(etPassword);

        if (TextUtils.isEmpty(email)
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid Email");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            tilPassword.setError("Enter Password");
            return;
        }

        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        // Fetch user data to populate SharedPreferences for ProfileFragment
                        db.collection("Users").document(uid).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs",
                                                Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("userName", documentSnapshot.getString("username"));
                                        editor.putString("userEmail", documentSnapshot.getString("email"));
                                        editor.putString("userAge", documentSnapshot.getString("age"));
                                        editor.putString("userMobile", documentSnapshot.getString("mobilenumber"));
                                        String role = documentSnapshot.getString("role");
                                        if (role == null)
                                            role = "patient"; // default
                                        editor.putString("userRole", role);
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.apply();

                                        Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();

                                        if ("doctor".equals(role)) {
                                            startActivity(new Intent(getActivity(), DoctorDashboardActivity.class));
                                        } else {
                                            startActivity(new Intent(getActivity(), MainActivity.class));
                                        }
                                        requireActivity().finish();
                                    } else {
                                        Toast.makeText(getContext(), "User document does not exist", Toast.LENGTH_SHORT)
                                                .show();
                                        btnLogin.setEnabled(true);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Could not fetch profile details", Toast.LENGTH_SHORT)
                                            .show();
                                    btnLogin.setEnabled(true);
                                });
                    } else {
                        Toast.makeText(getContext(), "Login Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        btnLogin.setEnabled(true);
                    }
                });
    }

    private String get(TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }
}