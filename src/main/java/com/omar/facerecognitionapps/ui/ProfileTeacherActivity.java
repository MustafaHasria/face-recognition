package com.omar.facerecognitionapps.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.models.Teacher;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import com.omar.facerecognitionapps.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import org.jetbrains.annotations.NotNull;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import java.util.List;

public class ProfileTeacherActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks {

    private static final int EXTERNAL = 24;
    private TextInputLayout til_first, til_last, til_email, til_password;
    private TextInputEditText tie_first, tie_last, tie_email, tie_password;
    private CircleImageView civ;
    private Teacher teacher;
    private Uri uriToUpload;
    private SweetAlertDialog loading;
    private String first;
    private String last;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_teacher);
        teacher = Utils.gsonToObject(getIntent(), Constants.TEACHER_MODEL, Teacher.class);
        initViews();
        til_first.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == EditorInfo.IME_ACTION_NEXT) {
                til_last.requestFocus();
                return true;
            }
            return false;
        });
        String[] fullName = teacher.getFullName().split(" ");
        first = Utils.upperFirst(fullName[0].substring(0, 1)) + fullName[0].substring(1);
        last = Utils.upperFirst(fullName[1].substring(0, 1)) + fullName[1].substring(1);
        tie_first.setText(first);
        tie_last.setText(last);
        tie_email.setText(teacher.getEmail());
        tie_email.setEnabled(false);
        til_email.setEnabled(false);
        if (teacher.getUrlImage() != null && !teacher.getUrlImage().isEmpty())
            Glide.with(this).load(teacher.getUrlImage()).into(civ);
        civ.setOnClickListener(item -> {
            if (ActivityCompat.checkSelfPermission(ProfileTeacherActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                EasyPermissions.requestPermissions(
                        new PermissionRequest.Builder(this, EXTERNAL, Manifest.permission.READ_EXTERNAL_STORAGE)
                                .setRationale("Accept the permissions!")
                                .setPositiveButtonText("Yes Accept")
                                .setNegativeButtonText("No")
                                .setTheme(R.style.Theme_FFA)
                                .build());
            } else {
                startSelectImage();
            }
        });
    }

    private void startSelectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    public void initViews() {
        civ = findViewById(R.id.iv_profile_image);
        til_first = findViewById(R.id.til_first_name);
        til_last = findViewById(R.id.til_last_name);
        til_email = findViewById(R.id.til_email_profile);
        til_password = findViewById(R.id.til_new_password);
        tie_first = findViewById(R.id.tie_first_name);
        tie_last = findViewById(R.id.tie_last_name);
        tie_email = findViewById(R.id.tie_email_profile);
        tie_password = findViewById(R.id.tie_new_password);
    }

    public void back(View view) {
        finish();
    }

    public void updateInfo(View view) {
        String first = tie_first.getText().toString();
        String last = tie_first.getText().toString();
        if (uriToUpload != null) {
            if (!first.isEmpty() && !last.isEmpty()) {
                teacher.setFullName(first + " " + last);
            }
            saveData();

        } else {
            if (!first.isEmpty() && !last.isEmpty()) {
                teacher.setFullName(first + " " + last);
                saveData();
            } else {
                Toasty.error(this, "You have to update info for save!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveData() {
        loading = Utils.instanceSweetLoading(this);
        loading.show();
        String password = tie_password.getText().toString();
        FirebaseUtils.getTeachers().child(teacher.getId()).setValue(teacher).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (password.isEmpty()) {
                    loading.dismissWithAnimation();
                    Toasty.success(ProfileTeacherActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(password).addOnCompleteListener(task1 -> {
                        loading.dismissWithAnimation();
                        if (task1.isSuccessful()) {
                            Toasty.success(ProfileTeacherActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toasty.error(ProfileTeacherActivity.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                loading.dismissWithAnimation();
                Toasty.error(ProfileTeacherActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri selectedImage = result.getUri();
                Glide.with(ProfileTeacherActivity.this)
                        .load(selectedImage)
                        .into(civ);

                uriToUpload = selectedImage;
                loading = Utils.instanceSweetLoading(ProfileTeacherActivity.this);
                loading.show();
                uploadImage(uriToUpload);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toasty.error(ProfileTeacherActivity.this, "Task Cancelled" + error.getMessage(), Toasty.LENGTH_LONG).show();

            }
        }
    }

    private void uploadImage(Uri uriToUpload) {
        StorageReference storageReference = FirebaseUtils.getStorageProfilesPictures();
        String idStudent = teacher.getId();
        byte[] uri = Utils.uriToBytes(uriToUpload, ProfileTeacherActivity.this);
        storageReference.child(idStudent).
                putBytes(uri).addOnFailureListener(e -> {
                    Toast.makeText(ProfileTeacherActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismissWithAnimation();
                }).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri1 -> {
                    teacher.setUrlImage(uri1.toString());
                    Toasty.success(ProfileTeacherActivity.this, "Success!", Toasty.LENGTH_SHORT).show();
                    loading.dismissWithAnimation();
                }));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NotNull List<String> list) {
        startSelectImage();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NotNull List<String> list) {
        Toasty.error(ProfileTeacherActivity.this, "You can not complete upload a profile image!", Toasty.LENGTH_SHORT).show();
    }

    public void logOut(View view) {
        SweetAlertDialog alertDialog = new SweetAlertDialog(this);
        alertDialog.setTitleText("You want to log out?");
        alertDialog.setConfirmButton("Yes LogOut", sweetAlertDialog -> {
            FirebaseAuth.getInstance().signOut();
            sweetAlertDialog.dismissWithAnimation();
            startActivity(new Intent(ProfileTeacherActivity.this,SplashActivity.class));
            finish();
        });
        alertDialog.setCancelButton("Cancel", sweetAlertDialog -> alertDialog.dismissWithAnimation());
        alertDialog.show();
    }
}
