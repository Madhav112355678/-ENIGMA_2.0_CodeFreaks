package com.example.enigma_schrzio_detetion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DoctorRequestAdapter extends RecyclerView.Adapter<DoctorRequestAdapter.ViewHolder> {

    private List<AppointmentRequest> requestList;

    public DoctorRequestAdapter(List<AppointmentRequest> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentRequest request = requestList.get(position);
        holder.tvPatientName.setText(request.getPatientName());
        holder.tvDate.setText(request.getDate() != null ? request.getDate() : "Date not specified");

        String status = request.getStatus();

        if ("Pending".equals(status)) {
            holder.llActions.setVisibility(View.VISIBLE);
            holder.tvAcceptedLabel.setVisibility(View.GONE);
            holder.tvDeclinedMessage.setVisibility(View.GONE);
        } else if ("accepted".equals(status)) {
            holder.llActions.setVisibility(View.GONE);
            holder.tvAcceptedLabel.setVisibility(View.VISIBLE);
            holder.tvDeclinedMessage.setVisibility(View.GONE);
        } else if ("declined".equals(status)) {
            holder.llActions.setVisibility(View.GONE);
            holder.tvAcceptedLabel.setVisibility(View.GONE);
            holder.tvDeclinedMessage.setVisibility(View.VISIBLE);
        }

        holder.btnAccept.setOnClickListener(v -> updateStatus(request, "accepted", holder.itemView));
        holder.btnDecline.setOnClickListener(v -> updateStatus(request, "declined", holder.itemView));
    }

    private void updateStatus(AppointmentRequest request, String newStatus, View contextView) {
        FirebaseDatabase.getInstance().getReference("appointments")
                .child(request.getId())
                .child("status")
                .setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(contextView.getContext(), "Request " + newStatus, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(contextView.getContext(), "Error updating request", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvDate, tvDeclinedMessage, tvAcceptedLabel;
        LinearLayout llActions;
        Button btnAccept, btnDecline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDeclinedMessage = itemView.findViewById(R.id.tvDeclinedMessage);
            tvAcceptedLabel = itemView.findViewById(R.id.tvAcceptedLabel);
            llActions = itemView.findViewById(R.id.llActions);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
        }
    }
}
