package com.omar.facerecognitionapps.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.customs.AddStudentSheet;
import com.omar.facerecognitionapps.customs.EditStudentSheet;
import com.omar.facerecognitionapps.customs.ReportStudentSheet;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.models.Teacher;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import com.omar.facerecognitionapps.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StudentDetailsActivity extends AppCompatActivity {
    private Student student;
    private Teacher teacher;

    private TextView name_student_details, birth_day_student_details, residence_student_details,
            spec_student_details, join_student_details, email_student_details;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);
        initViews();
        student = Utils.gsonToObject(getIntent(), Constants.STUDENT_MODEL, Student.class);
        teacher = Utils.gsonToObject(getIntent(), Constants.TEACHER_MODEL, Teacher.class);
        Glide.with(this).load(student.getImage()).into((CircleImageView)findViewById(R.id.civ_profile_details));
        String fullName = Utils.upperFirst(student.getFirst()) + " " + Utils.upperFirst(student.getLast());
        name_student_details.setText(fullName);
        birth_day_student_details.setText(student.getBirthDay());
        residence_student_details.setText(student.getResidence());
        spec_student_details.setText(student.getSpec());
        join_student_details.setText(student.getJoin());
        email_student_details.setText(student.getEmail());
    }

    private void initViews() {
        name_student_details = findViewById(R.id.name_student_details);
        birth_day_student_details = findViewById(R.id.birth_day_student_details);
        residence_student_details = findViewById(R.id.residence_student_details);
        spec_student_details = findViewById(R.id.spec_student_details);
        join_student_details = findViewById(R.id.join_student_details);
        email_student_details = findViewById(R.id.email_student_details);
    }

    public void deleteThisUser(View view) {
        SweetAlertDialog alertDialog = new SweetAlertDialog(this);
        alertDialog.setTitleText("You want to delete this user?");
        alertDialog.setConfirmButton("Yes Delete", sweetAlertDialog -> {
            sweetAlertDialog.dismissWithAnimation();
            deleteUser();
        });
        alertDialog.setCancelButton("No", sweetAlertDialog -> alertDialog.dismissWithAnimation());
        alertDialog.show();
    }

    private void deleteUser() {
        SweetAlertDialog dialog = Utils.instanceSweetLoading(this);
        dialog.show();
        ArrayList<Student> listStudentsTeacher = teacher.getListStudents();
        ArrayList<Student> updatedList = new ArrayList<>();
        for (Student value : listStudentsTeacher) {
            if (student.getId().equals(value.getId())) {
                continue;
            }
            updatedList.add(value);
        }
        teacher.getListStudents().clear();
        teacher.setListStudents(updatedList);
        FirebaseUtils.getStudents().child(student.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUtils.getStudentsEmails(student.getEmail()).removeValue();
                FirebaseUtils.getTeachers().child(teacher.getId()).removeValue().addOnCompleteListener(task12 -> {
                    if(task12.isSuccessful()){
                        FirebaseUtils.getTeachers().child(teacher.getId()).setValue(teacher)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        dialog.dismissWithAnimation();
                                        Toasty.success(StudentDetailsActivity.this, "Deleted Successful!", Toasty.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        dialog.dismissWithAnimation();
                                        Toasty.error(StudentDetailsActivity.this, task1.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                    }
                                });
                    }else{
                        dialog.dismissWithAnimation();
                        Toasty.error(StudentDetailsActivity.this, "An error occurred!", Toasty.LENGTH_LONG).show();
                    }
                });

            } else {
                dialog.dismissWithAnimation();
                Toasty.error(StudentDetailsActivity.this, task.getException().getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    public void back(View view) {
        finish();
    }

    public void editStudent(View view) {
        BottomSheetDialogFragment studentSheet = new EditStudentSheet();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TEACHER_MODEL, Utils.objectToGson(Teacher.class, teacher));
        bundle.putString(Constants.STUDENT_MODEL, Utils.objectToGson(Teacher.class, student));
        studentSheet.setArguments(bundle);
        studentSheet.show(getSupportFragmentManager(), "");
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_down).commit();
    }

    public void reportsForThisStudent(View view) {
        BottomSheetDialogFragment fragment = new ReportStudentSheet();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.REPORT_USER, student.getId());
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(),"");
    }
}
