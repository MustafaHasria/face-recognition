package com.omar.facerecognitionapps.ui;

import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.customs.AddStudentSheet;
import com.omar.facerecognitionapps.fragments.DailyStudentFragment;
import com.omar.facerecognitionapps.fragments.HistoryStudentFragment;
import com.omar.facerecognitionapps.fragments.ViewStudentFragment;
import com.omar.facerecognitionapps.models.Teacher;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.Utils;

public class TeacherHomeActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;
    private Teacher teacher;
    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = new ViewStudentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TEACHER_MODEL, Utils.objectToGson(Teacher.class, teacher));
        int itemId = item.getItemId();
        if (itemId == R.id.view_students) {
            selectedFragment = new ViewStudentFragment();
        } else if (itemId == R.id.view_daily_history) {
            selectedFragment = new DailyStudentFragment();
        } else if (itemId == R.id.view_students_history) {
            selectedFragment = new HistoryStudentFragment();
        }
        selectedFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_teacher_home, selectedFragment)
                .setCustomAnimations(
                        R.anim.fragment_slide_in,  // enter
                        R.anim.fragment_fade_out,  // exit
                        R.anim.fragment_fade_in,   // popEnter
                        R.anim.fragment_slide_out
                ).commit();
        return true;
    };
    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1500);
        getWindow().setEnterTransition(fade);
        Slide slide = new Slide();
        slide.setDuration(1500);
        getWindow().setReturnTransition(slide);
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
        teacher = Utils.gsonToObject(getIntent(), Constants.TEACHER_MODEL, Teacher.class);
        initViews();
        setupWindowAnimations();
        bottomNavigationView.setOnItemSelectedListener(navListener);
        loadDefault();
    }

    private void loadDefault() {
        Fragment selectedFragment = new ViewStudentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TEACHER_MODEL, Utils.objectToGson(Teacher.class, teacher));
        selectedFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_teacher_home, selectedFragment)
                .setCustomAnimations(
                        R.anim.fragment_slide_in,  // enter
                        R.anim.fragment_fade_out,  // exit
                        R.anim.fragment_fade_in,   // popEnter
                        R.anim.fragment_slide_out
                ).commit();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bnv_teacher_menu);
    }
    public void addStudent(View view) {
        BottomSheetDialogFragment studentSheet = new AddStudentSheet();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TEACHER_MODEL, Utils.objectToGson(Teacher.class, teacher));
        studentSheet.setArguments(bundle);
        studentSheet.show(getSupportFragmentManager(), "");
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_down).commit();
    }
}
