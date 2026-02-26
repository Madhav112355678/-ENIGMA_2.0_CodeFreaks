package com.example.enigma_schrzio_detetion;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AuthPagerAdapter extends FragmentStateAdapter {

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }
    public AuthPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return RegisterFragment.newInstance();
        }
        return LoginFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return 2; // Login (0) + Register (1)
    }
}
