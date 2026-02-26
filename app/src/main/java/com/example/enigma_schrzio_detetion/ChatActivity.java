package com.example.enigma_schrzio_detetion;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend, btnAttach;
    private TextView tvChatTitle;

    private MessageAdapter adapter;
    private List<ChatMessage> messageList;

    private FirebaseFirestore db;
    private String currentUserId, currentUserName, appointmentId, otherUserName;

    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";
        currentUserName = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("userName", "User");

        appointmentId = getIntent().getStringExtra("appointmentId");
        otherUserName = getIntent().getStringExtra("otherUserName");

        tvChatTitle = findViewById(R.id.tvChatTitle);
        tvChatTitle.setText(otherUserName != null ? "Chat with " + otherUserName : "Chat");

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnAttach = findViewById(R.id.btnAttach);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        rvMessages.setAdapter(adapter);

        if (appointmentId == null) {
            Toast.makeText(this, "Error loading chat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listenForMessages();

        btnSend.setOnClickListener(v -> sendMessage(etMessage.getText().toString().trim(), null));

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        uploadFileAndSendMessage(fileUri);
                    }
                });

        btnAttach.setOnClickListener(v -> openFilePicker());
    }

    private void listenForMessages() {
        db.collection("Chats").document(appointmentId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    messageList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            ChatMessage msg = doc.toObject(ChatMessage.class);
                            messageList.add(msg);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (messageList.size() > 0) {
                        rvMessages.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void sendMessage(String text, String fileUrl) {
        if ((text == null || text.isEmpty()) && (fileUrl == null || fileUrl.isEmpty()))
            return;

        etMessage.setText("");

        String msgId = db.collection("Chats").document(appointmentId).collection("messages").document().getId();
        ChatMessage message = new ChatMessage(msgId, currentUserId, currentUserName, text, fileUrl);

        db.collection("Chats").document(appointmentId).collection("messages").document(msgId)
                .set(message)
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = { "application/pdf", "image/jpeg", "image/png" };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        filePickerLauncher.launch(Intent.createChooser(intent, "Select Medical Report"));
    }

    private void uploadFileAndSendMessage(Uri fileUri) {
        Toast.makeText(this, "Uploading file...", Toast.LENGTH_SHORT).show();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("medical_reports/" + UUID.randomUUID().toString());

        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            sendMessage("Attached Medical Report", uri.toString());
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show());
    }
}
