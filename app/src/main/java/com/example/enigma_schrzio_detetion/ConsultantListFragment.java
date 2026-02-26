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

        // Add Dummy Data
        doctorList = new ArrayList<>();
        doctorList.add(new Doctor("1", "Dr. Sarah Jenkins", "Psychiatrist", "12 Years", 4.9f));
        doctorList.add(new Doctor("2", "Dr. Michael Chen", "Clinical Psychologist", "8 Years", 4.7f));
        doctorList.add(new Doctor("3", "Dr. Emily Davis", "Therapist", "5 Years", 4.5f));
        doctorList.add(new Doctor("4", "Dr. Robert Wilson", "Neurologist", "15 Years", 4.8f));
        doctorList.add(new Doctor("5", "Dr. Lisa Wong", "Child Psychologist", "10 Years", 4.6f));

        adapter = new DoctorAdapter(doctorList);
        recyclerView.setAdapter(adapter);
    }
}
