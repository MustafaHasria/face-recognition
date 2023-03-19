package com.omar.facerecognitionapps.models;

public class Student {
    private String id;
    private String image,first,last,residence,spec,birthDay,email,join,phoneStudent,phoneGuardian;

    public Student() {
    }

    public Student(String id, String image, String first, String last, String residence, String spec, String birthDay, String email, String join, String phoneStudent, String phoneGuardian) {
        this.id = id;
        this.image = image;
        this.first = first;
        this.last = last;
        this.residence = residence;
        this.spec = spec;
        this.birthDay = birthDay;
        this.email = email;
        this.join = join;
        this.phoneStudent = phoneStudent;
        this.phoneGuardian = phoneGuardian;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJoin() {
        return join;
    }

    public void setJoin(String join) {
        this.join = join;
    }

    public String getPhoneStudent() {
        return phoneStudent;
    }

    public void setPhoneStudent(String phoneStudent) {
        this.phoneStudent = phoneStudent;
    }

    public String getPhoneGuardian() {
        return phoneGuardian;
    }

    public void setPhoneGuardian(String phoneGuardian) {
        this.phoneGuardian = phoneGuardian;
    }
}
