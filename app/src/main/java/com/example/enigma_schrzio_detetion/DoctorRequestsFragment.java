package com.example.enigma_schrzio_detetion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorRequestsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvNoRequests;
    private DoctorRequestAdapter adapter;
    private List<AppointmentRequest> requestList;

    public DoctorRequestsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvDoctorRequests);
        tvNoRequests = view.findViewById(R.id.tvNoRequests);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestList = new ArrayList<>();
        adapter = new DoctorRequestAdapter(requestList);
        recyclerView.setAdapter(adapter);

        fetchRequests();
    }

    private void fetchRequests() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            return;

        FirebaseDatabase.getInstance().getReference("appointments")
                .orderByChild("doctorId").equalTo(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requestList.clear();
                        for (DataSnapshot doc : snapshot.getChildren()) {
                            AppointmentRequest request = new AppointmentRequest();
                            request.setId(doc.getKey());
                            request.setPatientName(doc.child("patientName").getValue(String.class));
                            request.setPatientId(doc.child("patientId").getValue(String.class));
                            request.setDoctorId(doc.child("doctorId").getValue(String.class));
                            request.setDoctorName(doc.child("doctorName").getValue(String.class));
                            request.setStatus(doc.child("status").getValue(String.class));
                            request.setDate(doc.child("date").getValue(String.class));
                            requestList.add(request);
                        }

                        adapter.notifyDataSetChanged();

                        if (requestList.isEmpty()) {
                            tvNoRequests.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvNoRequests.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error fetching requests", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
