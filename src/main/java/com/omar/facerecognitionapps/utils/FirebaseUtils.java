package com.omar.facerecognitionapps.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class FirebaseUtils {

    public static DatabaseReference getRootDB(){
        return FirebaseDatabase.getInstance(Constants.DB_URL).getReference();
    }
    public static DatabaseReference getTeacherModel(String id) {
        return getRootDB()
                .child(Constants.TEACHERS)
                .child(id);
    }
    public static DatabaseReference getTeachers() {
        return getRootDB()
                .child(Constants.TEACHERS);
    }
    public static DatabaseReference getStudents() {
        return  getRootDB()
                .child(Constants.STUDENTS);
    }

    public static StorageReference getStorageProfilesPictures() {
        return FirebaseStorage.getInstance(Constants.STORAGE_URL).getReference().child(Constants.PI);
    }

    public static DatabaseReference getHistory() {
        return getRootDB().child(Constants.HISTORY);
    }
    public static DatabaseReference getStudentsEmails(String email) {
        return getRootDB().child(Constants.STUDENTS_EMAILS).child(Utils.toFirebase(email));
    }

    public static DatabaseReference getReports() {
        return getRootDB().child(Constants.REPORTS);
    }

    public static DatabaseReference getAbsents() {
        return getRootDB().child(Constants.ABSENTS);
    }
}
