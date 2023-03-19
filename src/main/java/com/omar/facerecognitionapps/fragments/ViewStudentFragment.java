package com.omar.facerecognitionapps.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.adapters.ViewStudentsAdapter;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.models.Teacher;
import com.omar.facerecognitionapps.ui.ProfileTeacherActivity;
import com.omar.facerecognitionapps.ui.StudentDetailsActivity;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import com.omar.facerecognitionapps.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class ViewStudentFragment extends Fragment {
    private  ArrayList<Student> theList;
    private Teacher teacher;
    private RecyclerView recyclerView;
    private RelativeLayout hasData, empty;
    private CircleImageView imgProfile;
    private ViewStudentsAdapter adapter;
    private ProgressBar progressBar;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_student, container, false);
        initViews(view);
        teacher = Utils.gsonToObjectForFragment(getArguments(), Constants.TEACHER_MODEL, Teacher.class);
        if(!teacher.getUrlImage().isEmpty())
            Glide.with(Objects.requireNonNull(getContext())).load(teacher.getUrlImage()).into(imgProfile);
        imgProfile.setOnClickListener(item->{
            startActivity(new Intent(getActivity(), ProfileTeacherActivity.class)
                    .putExtra(Constants.TEACHER_MODEL,Utils.objectToGson(Teacher.class,teacher)));
        });
        showLoading(true);
        adapter = new ViewStudentsAdapter(getContext());
        adapter.setOnClickItem(pos -> {
            Student student = theList.get(pos);
            startActivity(new Intent(getActivity(), StudentDetailsActivity.class).putExtra(Constants.TEACHER_MODEL, Utils.objectToGson(Teacher.class, teacher)).putExtra(Constants.STUDENT_MODEL, Utils.objectToGson(Student.class, student)));
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        FirebaseUtils
                .getTeachers().child(teacher.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        teacher = snapshot.getValue(Teacher.class);
                        theList = teacher.getListStudents();
                        if(theList == null)
                            showEmpty(true);
                        else{
                            if(theList.size()<=0)
                                showEmpty(true);
                            else
                                loadStudents();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        showEmpty(true);
                        Toasty.error(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews(View view) {
        imgProfile = view.findViewById(R.id.img_profile_teacher);
        hasData = view.findViewById(R.id.has_data);
        empty = view.findViewById(R.id.empty);
        recyclerView = view.findViewById(R.id.rv_students);
        progressBar = view.findViewById(R.id.progress_students);
    }
    private void loadStudents() {
        showLoading(false);
        adapter.setListStudents(theList);
        adapter.notifyDataSetChanged();
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

}
