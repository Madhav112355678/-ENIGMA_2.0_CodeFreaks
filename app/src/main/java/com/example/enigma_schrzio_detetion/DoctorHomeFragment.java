package com.example.enigma_schrzio_detetion;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorHomeFragment extends Fragment {

    private TextView tvWelcomeName, tvPendingCountBadge;

    public DoctorHomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvWelcomeName = view.findViewById(R.id.tvWelcomeName);
        tvPendingCountBadge = view.findViewById(R.id.tvPendingCountBadge);

        CardView cardNavRequests = view.findViewById(R.id.cardNavRequests);
        CardView cardNavProfile = view.findViewById(R.id.cardNavProfile);

        cardNavRequests.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_doctor_requests));
        cardNavProfile.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_doctor_profile));

        loadDoctorInfo();
        countPendingRequests();
    }

    private void loadDoctorInfo() {
        if (getContext() == null)
            return;
        String name = getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getString("userName", "Doctor");
        tvWelcomeName.setText("Welcome, Dr. " + name);
    }

    private void countPendingRequests() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            return;

        FirebaseDatabase.getInstance().getReference("appointments")
                .orderByChild("doctorId").equalTo(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for (DataSnapshot doc : snapshot.getChildren()) {
                            String status = doc.child("status").getValue(String.class);
                            if ("Pending".equals(status)) {
                                count++;
                            }
                        }
                        tvPendingCountBadge.setText(String.valueOf(count));
                        if (count > 0) {
                            tvPendingCountBadge.setVisibility(View.VISIBLE);
                        } else {
                            tvPendingCountBadge.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Ignore
                    }
                });
    }
}
