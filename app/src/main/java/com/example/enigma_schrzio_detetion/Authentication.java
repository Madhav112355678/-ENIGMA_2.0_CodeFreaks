package com.example.enigma_schrzio_detetion;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Authentication extends AppCompatActivity
        implements LoginFragment.OnSwitchToRegisterListener,
        RegisterFragment.OnSwitchToLoginListener {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make status bar transparent, draw behind it with green
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#1B5E20"));

        setContentView(R.layout.activity_authentication);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // Attach adapter
        AuthPagerAdapter adapter = new AuthPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Disable over-scroll glow on ViewPager2
        viewPager.setOffscreenPageLimit(2);

        // Link TabLayout ↔ ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(getString(R.string.tab_login));
            } else {
                tab.setText(getString(R.string.tab_register));
            }
        }).attach();
    }

    // ---- LoginFragment.OnSwitchToRegisterListener ----
    @Override
    public void onSwitchToRegister() {
        viewPager.setCurrentItem(1, true);
    }

    // ---- RegisterFragment.OnSwitchToLoginListener ----
    @Override
    public void onSwitchToLogin() {
        viewPager.setCurrentItem(0, true);
    }
}