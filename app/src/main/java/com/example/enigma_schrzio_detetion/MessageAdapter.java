package com.example.enigma_schrzio_detetion;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<ChatMessage> messageList;
    private String currentUserId;

    public MessageAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            this.currentUserId = "";
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
        if (currentUserId.equals(message.getSenderId())) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        String timeString = "Just now";
        if (message.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
            timeString = sdf.format(message.getTimestamp());
        }

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
            sentHolder.tvMessage.setText(message.getText());
            sentHolder.tvTime.setText(timeString);

            if (message.getFileUrl() != null && !message.getFileUrl().isEmpty()) {
                sentHolder.btnViewFile.setVisibility(View.VISIBLE);
                sentHolder.btnViewFile.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getFileUrl()));
                    v.getContext().startActivity(intent);
                });
            } else {
                sentHolder.btnViewFile.setVisibility(View.GONE);
            }
        } else {
            ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
            receivedHolder.tvMessage.setText(message.getText());
            receivedHolder.tvSender.setText(message.getSenderName());
            receivedHolder.tvTime.setText(timeString);

            if (message.getFileUrl() != null && !message.getFileUrl().isEmpty()) {
                receivedHolder.btnViewFile.setVisibility(View.VISIBLE);
                receivedHolder.btnViewFile.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getFileUrl()));
                    v.getContext().startActivity(intent);
                });
            } else {
                receivedHolder.btnViewFile.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        Button btnViewFile;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnViewFile = itemView.findViewById(R.id.btnViewFile);
        }
    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender, tvMessage, tvTime;
        Button btnViewFile;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tvSender);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnViewFile = itemView.findViewById(R.id.btnViewFile);
        }
    }
}
