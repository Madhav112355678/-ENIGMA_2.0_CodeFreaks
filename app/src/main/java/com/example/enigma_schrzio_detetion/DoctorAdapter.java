package com.example.enigma_schrzio_detetion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private List<Doctor> doctorList;

    public DoctorAdapter(List<Doctor> doctorList) {
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.tvDoctorName.setText(doctor.getName());
        holder.tvDoctorSpecialty.setText(doctor.getSpecialization());
        holder.tvDoctorExperience.setText(doctor.getExperience() + " Exp");
        holder.tvDoctorRating.setText(String.valueOf(doctor.getRating()));

        holder.btnBookAppointment.setOnClickListener(v -> {
            holder.btnBookAppointment.setEnabled(false);
            holder.btnBookAppointment.setText("Requesting...");

            com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
            if (auth.getCurrentUser() == null) {
                Toast.makeText(v.getContext(), "Please login first", Toast.LENGTH_SHORT).show();
                holder.btnBookAppointment.setEnabled(true);
                holder.btnBookAppointment.setText("Book Appointment");
                return;
            }

            String patientId = auth.getCurrentUser().getUid();

            // Get patient name from SharedPreferences
            android.content.SharedPreferences prefs = v.getContext().getSharedPreferences("AppPrefs",
                    android.content.Context.MODE_PRIVATE);
            String patientName = prefs.getString("userName", "Patient");

            com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore
                    .getInstance();

            String reqId = db.collection("Appointments").document().getId();
            AppointmentRequest request = new AppointmentRequest(
                    reqId, patientId, patientName, doctor.getId(), doctor.getName(), "pending");

            db.collection("Appointments").document(reqId).set(request)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(v.getContext(), "Appointment Requested", Toast.LENGTH_SHORT).show();
                        holder.btnBookAppointment.setText("Requested");
                        // keep disabled
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        holder.btnBookAppointment.setEnabled(true);
                        holder.btnBookAppointment.setText("Book Appointment");
                    });
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvDoctorSpecialty, tvDoctorExperience, tvDoctorRating;
        Button btnBookAppointment;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDoctorSpecialty = itemView.findViewById(R.id.tvDoctorSpecialty);
            tvDoctorExperience = itemView.findViewById(R.id.tvDoctorExperience);
            tvDoctorRating = itemView.findViewById(R.id.tvDoctorRating);
            btnBookAppointment = itemView.findViewById(R.id.btnBookAppointment);
        }
    }
}
