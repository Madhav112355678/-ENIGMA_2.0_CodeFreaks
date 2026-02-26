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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PatientAppointmentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvNoAppointments;
    private PatientAppointmentAdapter adapter;
    private List<AppointmentRequest> appointmentList;

    public PatientAppointmentsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvPatientAppointments);
        tvNoAppointments = view.findViewById(R.id.tvNoAppointments);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        appointmentList = new ArrayList<>();
        adapter = new PatientAppointmentAdapter(appointmentList);
        recyclerView.setAdapter(adapter);

        fetchAppointments();
    }

    private void fetchAppointments() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            return;

        FirebaseFirestore.getInstance().collection("Appointments")
                .whereEqualTo("patientId", user.getUid())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error fetching appointments", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    appointmentList.clear();
                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {
                            AppointmentRequest request = doc.toObject(AppointmentRequest.class);
                            appointmentList.add(request);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (appointmentList.isEmpty()) {
                        tvNoAppointments.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvNoAppointments.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
