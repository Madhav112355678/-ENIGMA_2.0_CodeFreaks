package com.example.enigma_schrzio_detetion;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class AppointmentRequest {
    private String id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String status; // "pending", "accepted", "declined"
    @ServerTimestamp
    private Date timestamp;

    public AppointmentRequest() {
        // Required for Firestore
    }

    public AppointmentRequest(String id, String patientId, String patientName, String doctorId, String doctorName,
            String status) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getStatus() {
        return status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
