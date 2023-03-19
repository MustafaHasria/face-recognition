package com.omar.facerecognitionapps.models;

import java.util.ArrayList;

public class Teacher {
    private String id;
    private String email;
    private String fullName;
    private String urlImage;
    private ArrayList<Student> listStudents;

    public Teacher() {
    }

    public Teacher(String id, String email, String fullName, String urlImage, ArrayList<Student> listStudents) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.urlImage = urlImage;
        this.listStudents = listStudents;
    }

    public Teacher(String id, String email, String fullName, String urlImage) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.urlImage = urlImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public ArrayList<Student> getListStudents() {
        return listStudents;
    }

    public void setListStudents(ArrayList<Student> listStudents) {
        this.listStudents = listStudents;
    }
}
