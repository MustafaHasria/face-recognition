package com.omar.facerecognitionapps.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.models.Teacher;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import com.omar.facerecognitionapps.utils.Utils;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TeacherActivityLogin extends AppCompatActivity {
    private TextInputLayout til_email, til_password;
    private TextInputEditText tie_email, tie_password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        Paper.init(TeacherActivityLogin.this);
        setContentView(R.layout.activity_teacher_login);
        initViews();
        Animation animSlideDown =
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        findViewById(R.id.text_create_account).setAnimation(animSlideDown);
        til_email.setAnimation(animSlideDown);
        til_password.setAnimation(animSlideDown);
        findViewById(R.id.btn_create_account).setAnimation(animSlideDown);
        findViewById(R.id.forgot_password_login).setAnimation(animSlideDown);

        tie_email.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                if (!tie_email.getText().toString().isEmpty()) {
                    til_email.clearFocus();
                    til_password.requestFocus();
                } else {
                    til_email.setError("Put your email!");
                    til_email.requestFocus();
                }
                return true;
            }
            return false;
        });
        tie_password.setImeOptions(EditorInfo
                .IME_ACTION_DONE);
        tie_password.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                til_password.clearFocus();
                doSignIn();
                return true;
            }
            return false;
        });
    }

    private void doSignIn() {
        String email = tie_email.getText().toString();
        String password = tie_password.getText().toString();
        if (email.isEmpty()) {
            til_email.setError("You have to put your email!");
            til_email.requestFocus();
        } else if (password.isEmpty()) {
            til_password.setError("You have to put your password!");
            til_password.requestFocus();
        } else {
            loginAccount(email, password);
        }
    }


    private void loginAccount(String email, String password) {
        SweetAlertDialog dialog = Utils.instanceSweetLoading(TeacherActivityLogin.this);
        dialog.show();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String idUser = task.getResult().getUser().getUid();
                FirebaseUtils.getTeacherModel(idUser)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    dialog.dismissWithAnimation();
                                    Teacher teacher = snapshot.getValue(Teacher.class);
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.TOPIC_STUDENT).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            Paper.book().delete(Constants.TOPIC_REGISTERED);
                                            FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_TEACHER).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    Paper.book().write(Constants.TOPIC_REGISTERED,Constants.TOPIC_TEACHER);
                                                    startActivity(new Intent(TeacherActivityLogin.this, TeacherHomeActivity.class)
                                                            .putExtra(Constants.TEACHER_MODEL, Utils.objectToGson(Teacher.class, teacher)));
                                                    finishAffinity();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Teacher teacher = new Teacher(idUser, email, "", "", new ArrayList<Student>());
                                    dialog.dismissWithAnimation();
                                    Toasty.error(TeacherActivityLogin.this, "We don't have any account with these information!", Toasty.LENGTH_LONG).show();
                                    finishAndRemoveTask();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                dialog.dismissWithAnimation();
                                Toasty.error(TeacherActivityLogin.this, error.getMessage(), Toasty.LENGTH_LONG).show();
                                finishAndRemoveTask();
                            }
                        });
            } else {
                dialog.dismissWithAnimation();
                Toasty.error(TeacherActivityLogin.this, task.getException().getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    private void initViews() {
        til_email = findViewById(R.id.til_login_email);
        til_password = findViewById(R.id.til_login_password);
        tie_email = findViewById(R.id.tie_login_email);
        tie_password = findViewById(R.id.tie_login_password);
    }

    public void back(View view) {
        finish();
    }

    public void toForgotPassword(View view) {
        startActivity(new Intent(TeacherActivityLogin.this, ForgotPasswordActivity.class));
    }

    public void signIn(View view) {
        doSignIn();
    }
}
