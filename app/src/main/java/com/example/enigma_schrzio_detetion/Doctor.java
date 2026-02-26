package com.example.enigma_schrzio_detetion;

public class Doctor {
    private String id;
    private String name;
    private String specialization;
    private String experience;
    private float rating;

    public Doctor(String id, String name, String specialization, String experience, float rating) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.experience = experience;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getExperience() {
        return experience;
    }

    public float getRating() {
        return rating;
    }
}
