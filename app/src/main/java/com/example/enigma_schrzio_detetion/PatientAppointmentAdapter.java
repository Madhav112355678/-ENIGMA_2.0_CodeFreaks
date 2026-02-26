package com.example.enigma_schrzio_detetion;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.tvStatus.setText("Status: " + status.toUpperCase());

        if ("accepted".equals(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
            holder.btnOpenChat.setVisibility(View.VISIBLE);
            holder.btnOpenChat.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("appointmentId", request.getId());
                intent.putExtra("otherUserName", request.getDoctorName());
                v.getContext().startActivity(intent);
            });
        } else if ("declined".equals(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#F44336")); // Red
            holder.btnOpenChat.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800")); // Orange (Pending)
            holder.btnOpenChat.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvStatus;
        Button btnOpenChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnOpenChat = itemView.findViewById(R.id.btnOpenChat);
        }
    }
}
