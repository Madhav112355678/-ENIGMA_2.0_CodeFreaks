package com.example.enigma_schrzio_detetion;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Green status bar to match theme
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#1B5E20"));

        setContentView(R.layout.activity_main);

        // ---- NavController ----
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);
        NavController navController = navHostFragment.getNavController();

        // ---- Bottom Navigation ----
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // ---- Top Bar Username (placeholder, swap for real user data) ----
        TextView tvTopUsername = findViewById(R.id.tvTopUsername);
        tvTopUsername.setText("User"); // TODO: replace with real username from auth/prefs
    }
}
