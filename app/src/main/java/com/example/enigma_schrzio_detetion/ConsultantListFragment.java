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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("role", "doctor")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Failed to fetch doctors", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    if (value != null) {
                        doctorList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            String id = doc.getId();
                            String name = doc.getString("username");
                            if (name == null)
                                name = "Unknown Doctor";

                            // Using defaults for now since Registration doesn't ask for these
                            String spec = doc.contains("specialization") ? doc.getString("specialization")
                                    : "General Physician";
                            String exp = doc.contains("experience") ? doc.getString("experience") : "5 Years";
                            float rating = doc.contains("rating") && doc.getDouble("rating") != null
                                    ? doc.getDouble("rating").floatValue()
                                    : 4.5f;

                            doctorList.add(new Doctor(id, name, spec, exp, rating));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
