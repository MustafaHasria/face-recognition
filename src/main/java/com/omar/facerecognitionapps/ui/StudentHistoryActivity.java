package com.omar.facerecognitionapps.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.adapters.HistoryStudentAdapter;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import es.dmoral.toasty.Toasty;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class StudentHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RelativeLayout hasData, empty;
    private HistoryStudentAdapter adapter;
    private ProgressBar progressBar;
    private String idStudent;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_history);
        idStudent = getIntent().getStringExtra(Constants.STUDENT_ID);
        initViews();
        showLoading(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryStudentAdapter(this);
        recyclerView.setAdapter(adapter);
        getData();
    }

    private void getData() {
        HashMap<String, Student> dateLinkedStudent = new HashMap<>();
        FirebaseUtils.getStudents().child(idStudent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Student student = snapshot.getValue(Student.class);
                FirebaseUtils.getHistory().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(dataSnapshot.getValue(String.class).equals(student.getId())) {
                                dateLinkedStudent.put(dataSnapshot.getKey(),student);
                            }
                        }
                        showEmpty(false);
                        adapter.setHashMapStudents(dateLinkedStudent);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        showLoading(false);
                        Toasty.error(StudentHistoryActivity.this, "", Toasty.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                showLoading(false);
                Toasty.error(StudentHistoryActivity.this, "", Toasty.LENGTH_SHORT).show();
            }
        });

    }

    private void showLoading(boolean state) {
        showEmpty(false);
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    private void showEmpty(boolean state) {
        if (state) {
            hasData.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            hasData.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        hasData = findViewById(R.id.has_data);
        empty = findViewById(R.id.empty);
        recyclerView = findViewById(R.id.rv_student_history);
        progressBar = findViewById(R.id.progress_student_history);
    }
}
