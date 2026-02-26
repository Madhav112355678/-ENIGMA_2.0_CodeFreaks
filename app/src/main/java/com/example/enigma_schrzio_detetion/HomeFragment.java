package com.example.enigma_schrzio_detetion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView cardConsult = view.findViewById(R.id.cardConsultDoctor);
        CardView cardProfile = view.findViewById(R.id.cardProfile);

        cardConsult.setOnClickListener(
                v -> Toast.makeText(getContext(), "Consult a Doctor — coming soon!", Toast.LENGTH_SHORT).show());

        cardProfile.setOnClickListener(
                v -> Toast.makeText(getContext(), "Profile — coming soon!", Toast.LENGTH_SHORT).show());
    }
}
