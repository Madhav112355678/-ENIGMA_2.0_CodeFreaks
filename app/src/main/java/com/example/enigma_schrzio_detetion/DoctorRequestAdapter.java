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

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DoctorRequestAdapter extends RecyclerView.Adapter<DoctorRequestAdapter.ViewHolder> {

    private List<AppointmentRequest> requestList;
    private FirebaseFirestore db;

    public DoctorRequestAdapter(List<AppointmentRequest> requestList) {
        this.requestList = requestList;
        this.db = FirebaseFirestore.getInstance();
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

        if (request.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
            holder.tvDate.setText(sdf.format(request.getTimestamp()));
        } else {
            holder.tvDate.setText("Just now");
        }

        String status = request.getStatus();

        if ("pending".equals(status)) {
            holder.llActions.setVisibility(View.VISIBLE);
            holder.btnOpenChat.setVisibility(View.GONE);
            holder.tvDeclinedMessage.setVisibility(View.GONE);
        } else if ("accepted".equals(status)) {
            holder.llActions.setVisibility(View.GONE);
            holder.btnOpenChat.setVisibility(View.VISIBLE);
            holder.tvDeclinedMessage.setVisibility(View.GONE);
        } else if ("declined".equals(status)) {
            holder.llActions.setVisibility(View.GONE);
            holder.btnOpenChat.setVisibility(View.GONE);
            holder.tvDeclinedMessage.setVisibility(View.VISIBLE);
        }

        holder.btnAccept.setOnClickListener(v -> updateStatus(request, "accepted", holder.itemView));
        holder.btnDecline.setOnClickListener(v -> updateStatus(request, "declined", holder.itemView));

        holder.btnOpenChat.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("appointmentId", request.getId());
            intent.putExtra("otherUserName", request.getPatientName());
            v.getContext().startActivity(intent);
        });
    }

    private void updateStatus(AppointmentRequest request, String newStatus, View contextView) {
        db.collection("Appointments").document(request.getId())
                .update("status", newStatus)
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
        TextView tvPatientName, tvDate, tvDeclinedMessage;
        LinearLayout llActions;
        Button btnAccept, btnDecline, btnOpenChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDeclinedMessage = itemView.findViewById(R.id.tvDeclinedMessage);
            llActions = itemView.findViewById(R.id.llActions);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            btnOpenChat = itemView.findViewById(R.id.btnOpenChat);
        }
    }
}
