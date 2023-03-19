package com.omar.facerecognitionapps.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Report {
    private Student student;
    private HashMap<String, ArrayList<Long>> infos;

    public Report() {
    }

    public Report(Student student, HashMap<String, ArrayList<Long>> infos) {
        this.student = student;
        this.infos = infos;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public HashMap<String, ArrayList<Long>> getInfos() {
        return infos;
    }

    public void setInfos(HashMap<String, ArrayList<Long>> infos) {
        this.infos = infos;
    }
}
