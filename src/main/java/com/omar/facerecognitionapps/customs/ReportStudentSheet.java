package com.omar.facerecognitionapps.customs;

import android.Manifest;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.models.Report;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import com.omar.facerecognitionapps.utils.Utils;
import es.dmoral.toasty.Toasty;
import org.jetbrains.annotations.NotNull;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ReportStudentSheet extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks {
    private static final int EXTERNAL = 42;
    private MaterialButton csvBtn;
    private SweetAlertDialog loading;
    private String idStudent;

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
        View view = inflater.inflate(R.layout.report_sheet, container, false);
        initViews(view);
        idStudent = getArguments().getString(Constants.REPORT_USER);
        csvBtn.setOnClickListener(item -> {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                EasyPermissions.requestPermissions(
                        new PermissionRequest.Builder(this, EXTERNAL, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .setRationale("Accept the permissions!")
                                .setPositiveButtonText("Yes Accept")
                                .setNegativeButtonText("No")
                                .setTheme(R.style.Theme_FFA)
                                .build());
            } else {
                download(idStudent);
            }

        });
        return view;
    }

    private void download(String idStudent) {
        loading = Utils.instanceSweetLoading(getActivity());
        loading.show();
        FirebaseUtils.getReports().child(idStudent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                StringBuilder data = new StringBuilder();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        data.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
                    }
                }
                // Save the CSV file
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "firebase_data.csv");
                    FileWriter writer = new FileWriter(file);
                    writer.write(data.toString());
                    writer.close();
                    loading.dismissWithAnimation();
                    Toasty.success(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                } catch (IOException e) {
                    loading.dismissWithAnimation();
                    Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                loading.dismissWithAnimation();
                Toasty.error(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initViews(View view) {
        csvBtn = view.findViewById(R.id.download_csv);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull @NotNull List<String> perms) {
        download(idStudent);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull @NotNull List<String> perms) {
        Toasty.error(getActivity(), "You cannot download the report if you don't accept permissions!", Toast.LENGTH_SHORT).show();
    }
}
