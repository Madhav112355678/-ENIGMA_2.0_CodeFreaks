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
            Toast.makeText(v.getContext(), "Booking appointment with " + doctor.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to appointment booking flow
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
