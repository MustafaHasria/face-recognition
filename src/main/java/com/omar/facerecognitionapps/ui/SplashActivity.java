package com.omar.facerecognitionapps.ui;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.customs.EmailStudentFragment;
import com.omar.facerecognitionapps.models.Teacher;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import com.omar.facerecognitionapps.utils.Utils;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import org.jetbrains.annotations.NotNull;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.twilio.Twilio;
import com.twilio.converter.Promoter;

import com.twilio.type.PhoneNumber;

import java.net.URI;
import java.math.BigDecimal;
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int EXTERNAL = 24;
    private AnimationDrawable anim = null;
    private LottieAnimationView anim_lottie;
    private boolean isFirst = true;
    //private VonageClient client;
    //private SmsSubmissionResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Two Code For Hide Status Bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getDecorView().getWindowInsetsController().hide(WindowInsets.Type.statusBars());
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_splash);
        Paper.init(SplashActivity.this);
        isFirst = Boolean.TRUE.equals(Paper.book().read(Constants.IS_FIRST));
        subscribeTokens();
        anim_lottie = findViewById(R.id.anim_view_splash);
        anim_lottie.animate().setDuration(Constants.SLEEP_TIME_SPLASH).start();
        anim_lottie.playAnimation();
        anim_lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {

                loadSelectType();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
    }

    private void subscribeTokens() {
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_REGISTERED).addOnCompleteListener(task -> {
            isFirst = false;
            Paper.book().write(Constants.IS_FIRST, isFirst);
            Paper.book().write(Constants.TOPIC_REGISTERED, Constants.TOPIC_STUDENT);
            Log.d("SERVICE", "onComplete: successful");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning()) anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning()) anim.stop();
    }

    private void loadSelectType() {
        // Animation Lottie Out
        Animation animOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        anim_lottie.startAnimation(animOut);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                anim_lottie.setVisibility(View.GONE);
                findViewById(R.id.select_type_user).setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        RelativeLayout container = findViewById(R.id.bg_anim);
        // Animation Gradient Background
        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);
        // Animation Slide Out For Child Views
        Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        findViewById(R.id.linear_select_type).startAnimation(animSlideDown);
    }

    public void loginAsTeacher(View view) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(SplashActivity.this, TeacherActivityLogin.class));
        } else {
            SweetAlertDialog dialog = Utils.instanceSweetLoading(SplashActivity.this);
            dialog.show();
            FirebaseUtils.getTeacherModel(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        dialog.dismissWithAnimation();
                        Teacher teacher = snapshot.getValue(Teacher.class);
                        startActivity(new Intent(SplashActivity.this, TeacherHomeActivity.class).putExtra(Constants.TEACHER_MODEL, Utils.objectToGson(Teacher.class, teacher)));
                        finishAffinity();
                    } else {
                        dialog.dismissWithAnimation();
                        Toasty.error(SplashActivity.this, "We don't have any account with these information!", Toasty.LENGTH_LONG).show();
                        finishAndRemoveTask();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismissWithAnimation();
                    Toasty.error(SplashActivity.this, error.getMessage(), Toasty.LENGTH_LONG).show();
                    finishAndRemoveTask();
                }
            });
        }
    }
    public static final String ACCOUNT_SID = "AC699f11ebb10398a4bc6a94a5d21bb150";
    public static final String AUTH_TOKEN = "8b6f94baea35586b4a8601a4be2ea988";
    public void loginAsStudent(View view) {

        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, EXTERNAL, Manifest.permission.READ_EXTERNAL_STORAGE).setRationale("Accept the permissions!").setPositiveButtonText("Yes Accept").setNegativeButtonText("No").setTheme(R.style.Theme_FFA).build());
        } else {
            BottomSheetDialogFragment fragment = new EmailStudentFragment();
            fragment.show(getSupportFragmentManager(), "");
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull @NotNull List<String> perms) {
        BottomSheetDialogFragment fragment = new EmailStudentFragment();
        fragment.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull @NotNull List<String> perms) {
        Toasty.error(SplashActivity.this, "You cannot sign in as a user if you don't accept permissions!", Toast.LENGTH_SHORT).show();
    }
}