package com.example.enigma_schrzio_detetion;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PatientAppointmentAdapter extends RecyclerView.Adapter<PatientAppointmentAdapter.ViewHolder> {

    private List<AppointmentRequest> appointmentList;

    public PatientAppointmentAdapter(List<AppointmentRequest> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentRequest request = appointmentList.get(position);
        holder.tvDoctorName.setText(request.getDoctorName());

        String status = request.getStatus();
        if (status == null)
            status = "Pending";
        holder.tvStatus.setText("Status: " + status.toUpperCase());

        if ("accepted".equals(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.btnCallDoctor.setVisibility(View.VISIBLE);

            // When patient taps Call, fetch doctor's mobile number from RTDB and open
            // dialer
            holder.btnCallDoctor.setOnClickListener(v -> {
                String doctorId = request.getDoctorId();
                if (doctorId == null || doctorId.isEmpty()) {
                    Toast.makeText(v.getContext(), "Doctor info not available", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseDatabase.getInstance().getReference("doctors").child(doctorId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String phone = snapshot.child("mobilenumber").getValue(String.class);
                                    if (phone != null && !phone.isEmpty()) {
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                        callIntent.setData(Uri.parse("tel:" + phone));
                                        v.getContext().startActivity(callIntent);
                                    } else {
                                        Toast.makeText(v.getContext(), "Doctor phone number not available",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(v.getContext(), "Doctor not found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(v.getContext(), "Error fetching doctor info", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        } else if ("declined".equals(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#F44336"));
            holder.btnCallDoctor.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
            holder.btnCallDoctor.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvStatus;
        Button btnCallDoctor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCallDoctor = itemView.findViewById(R.id.btnCallDoctor);
        }
    }
}
