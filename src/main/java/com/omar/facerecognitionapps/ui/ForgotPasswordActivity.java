package com.omar.facerecognitionapps.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.utils.Utils;


public class ForgotPasswordActivity extends AppCompatActivity {
    private ImageView image_forgot,image_send_email;

    private TextInputLayout til_email,til_password;
    private TextInputEditText tie_email,tie_password;
    private FirebaseAuth auth;
    private String linkEmail;
    private String email;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_forgot_password);
        initViews();
    }

    private void initViews() {
        image_forgot = findViewById(R.id.frame_forgot_password_image);
        til_email = findViewById(R.id.til_email_input_forgot_password);
        tie_email = findViewById(R.id.tie_email_input_forgot_password);
    }

    public void sendEmailBtn(View view) {
        email  = tie_email.getText().toString();
        if(email.isEmpty()){
            til_email.setError("You have to put your email!");
            til_email.requestFocus();
        }else{
            SweetAlertDialog dialog = Utils.instanceSweetLoading(ForgotPasswordActivity.this);
            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                dialog.dismissWithAnimation();
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Check your mail box (Spam maybe) and click in the link for reset your password!", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }




    public void exit(View view) {
        finish();
    }

}
