package com.omar.facerecognitionapps.customs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.models.MiniStudent;
import com.omar.facerecognitionapps.ui.SplashActivity;
import com.omar.facerecognitionapps.ui.StudentLoginActivity;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import com.omar.facerecognitionapps.utils.Utils;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import org.jetbrains.annotations.NotNull;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailStudentFragment extends BottomSheetDialogFragment  {
    private SweetAlertDialog loading;
    private TextInputLayout til_email;
    private TextInputEditText tie_email;

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
            view.measure(0, 0);
            behavior.setPeekHeight(view.getMeasuredHeight());
        }
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_student_sheet, container, false);
        Paper.init(getActivity());
        initViews(view);
        view.findViewById(R.id.continueForCamera).setOnClickListener(item->{
            continueForCamera();
        });
        view.findViewById(R.id.cancel_download_report).setOnClickListener(item->{
            getDialog().dismiss();
        });
        return view;
    }

    private void continueForCamera() {
        loading = Utils.instanceSweetLoading(getActivity());
        String email = tie_email.getText().toString();
        if(email.isEmpty()){
            til_email.setError("Please Write Your Email!");
            til_email.requestFocus();
        }else{
            loading.show();
            FirebaseUtils.getStudentsEmails(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    loading.dismissWithAnimation();
                   if(snapshot.exists()){
                       MiniStudent miniStudent = snapshot.getValue(MiniStudent.class);
                       String urlImage = miniStudent.getUrlImage();
                       String idStudent = miniStudent.getIdStudent();
                       assert urlImage != null;
                       Paper.book().write(Constants.URL_IMAGE,urlImage);
                       Paper.book().write(Constants.STUDENT_ID,idStudent);
                       startActivity(new Intent(getActivity(), StudentLoginActivity.class));
                   }else{
                       Toasty.error(getActivity(), "This email is not exists!", Toast.LENGTH_SHORT).show();
                   }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toasty.error(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void initViews(View view) {
        til_email = view.findViewById(R.id.til_login_email);
        tie_email = view.findViewById(R.id.tie_login_email);
    }
}
