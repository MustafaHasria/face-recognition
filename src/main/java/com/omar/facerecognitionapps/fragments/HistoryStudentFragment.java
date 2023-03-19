package com.omar.facerecognitionapps.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.adapters.HistoryStudentAdapter;
import com.omar.facerecognitionapps.customs.FilterByDateSheet;
import com.omar.facerecognitionapps.helpers.GetStudents;
import com.omar.facerecognitionapps.helpers.OnSaveDate;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.models.Teacher;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import com.omar.facerecognitionapps.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryStudentFragment extends Fragment {
    private RecyclerView recyclerView;
    private RelativeLayout hasData, empty;
    private HistoryStudentAdapter adapter;
    private ProgressBar progressBar;
    private HashMap<String, Student> hashMapStudents;
    private Teacher teacher;
    private String from,to;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_student, container, false);
        initViews(view);
        assert getArguments() != null;
        teacher = Utils.gsonToObjectForFragment(getArguments(), Constants.TEACHER_MODEL, Teacher.class);
        ArrayList<Student> studentsThatTeacher;
        if(teacher.getListStudents()==null)
            studentsThatTeacher = new ArrayList<>();
        else
            studentsThatTeacher = teacher.getListStudents();
        hashMapStudents = new HashMap<>();
        for (Student student : studentsThatTeacher) {
            hashMapStudents.put(student.getId(), student);
        }
        adapter = new HistoryStudentAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadHistory();

        view.findViewById(R.id.filter_by_date).setOnClickListener(item->{
            BottomSheetDialogFragment filterByDateSheet = new FilterByDateSheet(new OnSaveDate() {
                @Override
                public void from(int year, int month, int day) {
                    from = year+"/"+month+"/"+day;
                }

                @Override
                public void to(int year, int month, int day) {
                    to = year+"/"+month+"/"+day;
                }

                @Override
                public void complete() {
                    long[] rand = Utils.getRand(from,to);
                    updateWithDate(rand);
                }
            });
            filterByDateSheet.show(getChildFragmentManager(),"");
            getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_down).commit();
        });
        return view;
    }

    private void updateWithDate(long[] rand) {
        HashMap<String,Student> filtered = new HashMap<>();
        for(String id : hashMapStudents.keySet()){
            long time = Long.parseLong(id);
            if(time>=rand[0]&&time<=rand[1]){
                filtered.put(id,hashMapStudents.get(id));
            }
        }
        adapter.setHashMapStudents(filtered);
        adapter.notifyDataSetChanged();
    }
    private void loadHistory(){
        HashMap<String,Student> dateLinkedWithStudent = new HashMap<>();
        FirebaseUtils.getHistory().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String date = dataSnapshot.getKey();
                        String idStudent = dataSnapshot.getValue(String.class);
                        if (hashMapStudents.containsKey(idStudent)) {
                            dateLinkedWithStudent.put(date, hashMapStudents.get(idStudent));
                        }
                    }
                    if (dateLinkedWithStudent.size() <= 0) {
                        showEmpty(true);
                    } else {
                        adapter.setHashMapStudents(dateLinkedWithStudent);
                        adapter.notifyDataSetChanged();
                        showLoading(false);
                    }
                }else{
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
        recyclerView = view.findViewById(R.id.rv_history_students);
        progressBar = view.findViewById(R.id.progress_history_students);
    }

    private GetStudents getStudents;

    public void setGetStudents(GetStudents getStudents) {
        this.getStudents = getStudents;
    }
}
