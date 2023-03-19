package com.omar.facerecognitionapps.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.adapters.DailyStudentAdapter;
import com.omar.facerecognitionapps.helpers.GetStudents;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.models.Teacher;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import com.omar.facerecognitionapps.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DailyStudentFragment extends Fragment {

    private RecyclerView recyclerView;
    private RelativeLayout hasData, empty;
    private DailyStudentAdapter adapter;
    private ProgressBar progressBar;
    private HashMap<String, Student> hashMapStudents;
    private final ArrayList<Student> finalList = new ArrayList<>();
    private GetStudents getStudents;
    private Teacher teacher;

    public void setGetStudents(GetStudents getStudents) {
        this.getStudents = getStudents;
    }
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_student, container, false);
        initViews(view);
        teacher = Utils.gsonToObjectForFragment(getArguments(), Constants.TEACHER_MODEL, Teacher.class);
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        String today = year+"-"+month+"-"+day;
        ((TextView)view.findViewById(R.id.title_daily)).setText("Today : "+today);
        hashMapStudents = new HashMap<>();
        assert getArguments() != null;
        ArrayList<Student> studentsThatTeacher;
        if(teacher.getListStudents()==null)
            studentsThatTeacher = new ArrayList<>();
        else
            studentsThatTeacher = teacher.getListStudents();
        for (Student student : studentsThatTeacher) {
            hashMapStudents.put(student.getId(), student);
        }
        adapter = new DailyStudentAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        getData();
        return view;
    }

    private void getData() {
        showLoading(true);
        FirebaseUtils.getHistory().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (Utils.isToday(Long.parseLong(dataSnapshot.getKey()))) {
                            String idKey = dataSnapshot.getValue(String.class);
                            if (hashMapStudents.get(idKey) != null) {
                                finalList.add(hashMapStudents.get(idKey));
                            }
                        }
                    }
                    if (finalList.size() <= 0) {
                        showEmpty(true);
                    } else {
                        adapter.setListStudents(finalList);
                        adapter.notifyDataSetChanged();
                        showLoading(false);
                    }
                } else {
                    showEmpty(true);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

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

    private void initViews(View view) {
        hasData = view.findViewById(R.id.has_data);
        empty = view.findViewById(R.id.empty);
        recyclerView = view.findViewById(R.id.rv_daily_students);
        progressBar = view.findViewById(R.id.progress_daily_students);
    }
}
