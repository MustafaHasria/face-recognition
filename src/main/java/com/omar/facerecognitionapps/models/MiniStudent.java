package com.omar.facerecognitionapps.models;

public class MiniStudent {
    private String email,idStudent,urlImage;

    public MiniStudent() {
    }

    public MiniStudent(String email, String idStudent, String urlImage) {
        this.email = email;
        this.idStudent = idStudent;
        this.urlImage = urlImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}

