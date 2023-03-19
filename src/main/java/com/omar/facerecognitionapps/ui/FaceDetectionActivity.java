package com.omar.facerecognitionapps.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.impl.CameraInternal;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.google.mlkit.vision.face.Face;
import com.omar.facerecognitionapps.FaceCameraScanActivity;
import com.omar.facerecognitionapps.camera.AnalyzeResult;
import com.omar.facerecognitionapps.models.Report;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FaceComparator;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import com.omar.facerecognitionapps.utils.Utils;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import org.jetbrains.annotations.NotNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import static java.lang.Thread.sleep;

public class FaceDetectionActivity extends FaceCameraScanActivity {
    private SweetAlertDialog loading;
    private FaceComparator comparator;

    public FaceDetectionActivity() {
        super();
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onScanResultCallback(@NonNull @NotNull AnalyzeResult<List<Face>> result) {
        comparator = new FaceComparator(this);
        getCameraScan()
                .setAnalyzeImage(false)
                .setPlayBeep(true)
                .setVibrate(true);
        Bitmap bitmap = result.getBitmap();
        loading = Utils.instanceSweetLoading(this);
        loading.show();
        if(bitmap!=null){
            getCameraScan().stopCamera();

            String urlImage = Paper.book().read(Constants.URL_IMAGE);
            String idStudent = Paper.book().read(Constants.STUDENT_ID);
            if(urlImage!=null&&idStudent!=null){
                getFaceWithUrl(bitmap,urlImage,idStudent);
            }else{
                loading.dismissWithAnimation();
                Toast.makeText(this, "Not Found In Our DB!", Toast.LENGTH_SHORT).show();
            }
        }else {
            loading.dismissWithAnimation();
            Toast.makeText(this, "No face detected!", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("StaticFieldLeak")
    private void getFaceWithUrl(Bitmap faceBitmap,String urlImage,String idStudent){
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    URL url = new URL(urlImage);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    return BitmapFactory.decodeStream(input);
                } catch (Exception e) {
                    Log.d("TAG", "doInBackground: 87"+e.getMessage());
                    return null;
                }
            }
            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null) {
                    float similarityScore = comparator.compareFaces(faceBitmap, result);
                    if (similarityScore >= 0.75f) {
                        String timePresence = String.valueOf(System.currentTimeMillis());
                        FirebaseUtils.getHistory().child(timePresence)
                                .setValue(idStudent)
                                .addOnFailureListener(e -> {
                                    loading.dismissWithAnimation();
                                    Toasty.error(getBaseContext(), e.getMessage(), Toasty.LENGTH_SHORT).show();
                                }).addOnSuccessListener(unused -> {
                                    loading.dismissWithAnimation();
                                    Toasty.success(getBaseContext(), "You are register with successful!", Toasty.LENGTH_SHORT).show();
                                    savePresence(idStudent, timePresence);
                                    startActivity(new Intent(getBaseContext(), StudentHistoryActivity.class)
                                            .putExtra(Constants.STUDENT_ID, idStudent));
                                    finish();
                                });
                    } else {
                        loading.dismissWithAnimation();
                        Toasty.warning(getBaseContext(), "This face is not exist in our database!", Toasty.LENGTH_SHORT).show();
                    }
                }
            }
        }.execute();

    }
    private void getTheFace(Bitmap faceBitmap, String idStudent) {
        FirebaseUtils.getStudents().child(idStudent)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        Student student = snapshot.getValue(Student.class);
                        new AsyncTask<Void, Void, Bitmap>() {
                            @Override
                            protected Bitmap doInBackground(Void... params) {
                                try {
                                    URL url = new URL(student.getId());
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setDoInput(true);
                                    connection.connect();
                                    InputStream input = connection.getInputStream();
                                    return BitmapFactory.decodeStream(input);
                                } catch (Exception e) {
                                    Log.d("TAG", "doInBackground: 87"+e.getMessage());
                                    return null;
                                }
                            }
                            @Override
                            protected void onPostExecute(Bitmap result) {
                                if (result != null) {
                                    float similarityScore = comparator.compareFaces(faceBitmap, result);
                                    if (similarityScore >= 0.75f) {
                                        String timePresence = String.valueOf(System.currentTimeMillis());
                                        FirebaseUtils.getHistory().child(timePresence)
                                                .setValue(idStudent)
                                                .addOnFailureListener(e -> {
                                                    loading.dismissWithAnimation();
                                                    Toasty.error(getBaseContext(), e.getMessage(), Toasty.LENGTH_SHORT).show();
                                                }).addOnSuccessListener(unused -> {
                                                    loading.dismissWithAnimation();
                                                    Toasty.success(getBaseContext(), "You are register with successful!", Toasty.LENGTH_SHORT).show();
                                                    savePresence(idStudent, timePresence);
                                                    startActivity(new Intent(getBaseContext(), StudentHistoryActivity.class)
                                                            .putExtra(Constants.STUDENT_ID, idStudent));
                                                    finish();
                                                });
                                    } else {
                                        loading.dismissWithAnimation();
                                        Toasty.warning(getBaseContext(), "This face is not exist in our database!", Toasty.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }.execute();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        getFacesFromDb(faceBitmap);
                    }
                });
    }

    private void savePresence(String idStudent, String timePresence) {
        long time = Long.parseLong(timePresence);
        if (Utils.H(time) >= 8 && Utils.H(time) < 9 && Utils.M(time) <= 45) {
            FirebaseUtils.getStudents().child(idStudent).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Student student = snapshot.getValue(Student.class);
                    FirebaseUtils.getReports().child(idStudent).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Report report = snapshot.getValue(Report.class);
                                updateReport(report, timePresence);
                            } else {
                                Report report = new Report();
                                report.setStudent(student);
                                createReport(report, timePresence);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        } else if (Utils.H(time) > 8 && Utils.H(time) < 9) {
            retard(idStudent, time);
        } else if (Utils.H(time) > 9) {
            absent(idStudent, time);
        }
        Paper.book().write(Constants.FOR_TODAY, timePresence);
    }

    private void retard(String idStudent, long time) {
        FirebaseUtils.getReports().child(idStudent)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Report report = snapshot.getValue(Report.class);
                            report.getInfos().get(Constants.DAYS_RETARD).add(0, time);
                            snapshot.getRef().setValue(report);
                        } else {
                            Report report = new Report();
                            FirebaseUtils.getStudents().child(idStudent).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    Student student = snapshot.getValue(Student.class);
                                    HashMap<String, ArrayList<Long>> hashMap = new HashMap<>();
                                    ArrayList<Long> daysIn = new ArrayList<>();
                                    ArrayList<Long> daysOut = new ArrayList<>();
                                    ArrayList<Long> daysRetard = new ArrayList<>();
                                    daysRetard.add(0, time);
                                    hashMap.put(Constants.DAYS_IN, daysIn);
                                    hashMap.put(Constants.DAYS_OUT, daysOut);
                                    hashMap.put(Constants.DAYS_RETARD, daysRetard);
                                    report.setStudent(student);
                                    report.setInfos(hashMap);
                                    FirebaseUtils.getReports().child(report.getStudent().getId()).setValue(report);
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void absent(String idStudent, long time) {
        FirebaseUtils.getAbsents().child(idStudent).setValue(time);
        FirebaseUtils.getReports().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Report report = snapshot.getValue(Report.class);
                    ArrayList<Long> daysIn = report.getInfos().get(Constants.DAYS_IN);
                    if (daysIn != null) {
                        daysIn.add(0, Long.valueOf(time));
                    }
                    report.getInfos().put(Constants.DAYS_IN, daysIn);
                    FirebaseUtils.getReports().child(report.getStudent().getId()).setValue(report);
                } else {
                    Report report = new Report();
                    FirebaseUtils.getStudents().child(idStudent).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Student student = snapshot.getValue(Student.class);
                            HashMap<String, ArrayList<Long>> hashMap = new HashMap<>();
                            ArrayList<Long> daysIn = new ArrayList<>();
                            daysIn.add(time);
                            ArrayList<Long> daysOut = new ArrayList<>();
                            ArrayList<Long> daysRetard = new ArrayList<>();
                            hashMap.put(Constants.DAYS_IN, daysIn);
                            hashMap.put(Constants.DAYS_OUT, daysOut);
                            hashMap.put(Constants.DAYS_RETARD, daysRetard);
                            report.setStudent(student);
                            report.setInfos(hashMap);
                            FirebaseUtils.getReports().child(report.getStudent().getId()).setValue(report);
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void createReport(Report report, String timePresence) {
        HashMap<String, ArrayList<Long>> hashMap = new HashMap<>();
        ArrayList<Long> daysIn = new ArrayList<>();
        daysIn.add(Long.valueOf(timePresence));
        ArrayList<Long> daysOut = new ArrayList<>();
        ArrayList<Long> daysRetard = new ArrayList<>();
        hashMap.put(Constants.DAYS_IN, daysIn);
        hashMap.put(Constants.DAYS_OUT, daysOut);
        hashMap.put(Constants.DAYS_RETARD, daysRetard);
        report.setInfos(hashMap);
        FirebaseUtils.getReports().child(report.getStudent().getId()).setValue(report);
    }

    private void updateReport(Report report, String timePresence) {
        ArrayList<Long> daysIn = report.getInfos().get(Constants.DAYS_IN);
        if (daysIn != null) {
            daysIn.add(0, Long.valueOf(timePresence));
        }
        report.getInfos().put(Constants.DAYS_IN, daysIn);
        FirebaseUtils.getReports().child(report.getStudent().getId()).setValue(report);
    }

    private void getFacesFromDb(Bitmap faceBitmap) {
        FirebaseUtils.getStorageProfilesPictures().listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        item.getBytes(1024 * 1024).addOnSuccessListener(bytes -> {
                            Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            String idStudent = item.getName();
                            float similarityScore = comparator.compareFaces(faceBitmap, imageBitmap);
                            if (similarityScore >= 0.65f) {
                                String timePresence = String.valueOf(System.currentTimeMillis());
                                FirebaseUtils.getHistory().child(timePresence)
                                        .setValue(idStudent)
                                        .addOnFailureListener(e -> {
                                            loading.dismissWithAnimation();
                                            Toasty.error(this, e.getMessage(), Toasty.LENGTH_SHORT).show();
                                        }).addOnSuccessListener(unused -> {
                                            loading.dismissWithAnimation();
                                            Toasty.success(this, "You are register with successful!", Toasty.LENGTH_SHORT).show();
                                            savePresence(idStudent, timePresence);
                                            Paper.book().write(Constants.STUDENT_ID, idStudent);
                                            startActivity(new Intent(this, StudentHistoryActivity.class)
                                                    .putExtra(Constants.STUDENT_ID, idStudent));
                                            finish();
                                        });
                            } else {
                                loading.dismissWithAnimation();
                                Toasty.warning(this, "This face is not exist in our database!", Toasty.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    loading.dismissWithAnimation();
                    Toasty.error(this, e.getMessage(), Toasty.LENGTH_SHORT).show();
                });
    }

}
