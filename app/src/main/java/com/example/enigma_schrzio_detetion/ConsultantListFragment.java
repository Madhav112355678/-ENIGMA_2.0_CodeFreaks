package com.example.enigma_schrzio_detetion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConsultantListFragment extends Fragment {

    private RecyclerView recyclerView;
    private DoctorAdapter adapter;
    private List<Doctor> doctorList;

    public ConsultantListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consultant_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewDoctors);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        doctorList = new ArrayList<>();
        adapter = new DoctorAdapter(doctorList);
        recyclerView.setAdapter(adapter);

        fetchDoctorsRealtime();
    }

    private void fetchDoctorsRealtime() {
        FirebaseDatabase.getInstance().getReference("doctors")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        doctorList.clear();
                        for (DataSnapshot doc : snapshot.getChildren()) {
                            String id = doc.getKey();
                            String name = doc.child("name").getValue(String.class);
                            if (name == null)
                                name = "Unknown Doctor";

                            String spec = doc.child("specialization").exists()
                                    ? doc.child("specialization").getValue(String.class)
                                    : "General Physician";
                            String exp = doc.child("experience").exists()
                                    ? doc.child("experience").getValue(String.class)
                                    : "5 Years";
                            float rating = doc.child("rating").exists() && doc.child("rating").getValue() != null
                                    ? doc.child("rating").getValue(Float.class)
                                    : 4.5f;

                            doctorList.add(new Doctor(id, name, spec, exp, rating));
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Failed to fetch doctors", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
