package com.omar.facerecognitionapps.customs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.models.MiniStudent;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.models.Teacher;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import com.omar.facerecognitionapps.utils.TwilioService;
import com.omar.facerecognitionapps.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import org.jetbrains.annotations.NotNull;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AddStudentSheet extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks {
    private static final int EXTERNAL = 42;
    private TextInputLayout til_first, til_last, til_resid, til_spec, til_email, til_phone_number, til_phone_number_guardian;
    private TextInputEditText tie_first, tie_last, tie_resid, tie_spec, tie_email, tie_phone_number, tie_phone_number_guardian;
    private String image, first, last, residence, spec, birthDay, join;
    private CircleImageView civ;
    private Uri uriToUpload;
    private SweetAlertDialog loadingFace;
    private String idStudent;
    private Teacher teacher;
    private DatePicker picker_birth_day, picker_joined_date;
    private String email;
    private String phoneNumber;
    private String phoneGuardian;
    //private VonageClient client;

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
        View view = inflater.inflate(R.layout.add_student_sheet, container, false);
        //  client = VonageClient.builder().apiKey("6054b1b2").apiSecret("3ccuFmkcSc2kIWiT").build();
        teacher = Utils.gsonToObjectForFragment(getArguments(), Constants.TEACHER_MODEL, Teacher.class);
        initViews(view);
        installImeOptions();
        initPhoneNumber();
        view.findViewById(R.id.cancel_add_student).setOnClickListener(item -> {
            getDialog().dismiss();
        });

        view.findViewById(R.id.img_add_profile_photo).setOnClickListener(item -> {
            if (tie_first.getText().toString().isEmpty()) {
                til_first.setError("You have to put first name!");
                til_first.requestFocus();
            } else if (tie_last.getText().toString().isEmpty()) {
                til_last.setError("You have to put last name!");
                til_last.requestFocus();
            } else {
                first = tie_first.getText().toString();
                last = tie_last.getText().toString();
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, EXTERNAL, Manifest.permission.READ_EXTERNAL_STORAGE).setRationale("Accept the permissions!").setPositiveButtonText("Yes Accept").setNegativeButtonText("No").setTheme(R.style.Theme_FFA).build());
                } else {
                    startSelectImage();
                }
            }
        });
        view.findViewById(R.id.mb_add_student).setOnClickListener(item -> {
            first = tie_first.getText().toString();
            last = tie_last.getText().toString();
            residence = tie_resid.getText().toString();
            spec = tie_spec.getText().toString();
            email = tie_email.getText().toString();
            birthDay = picker_birth_day.getYear() + "-" + picker_birth_day.getMonth() + "-" + picker_birth_day.getDayOfMonth();
            join = picker_joined_date.getYear() + "-" + picker_joined_date.getMonth() + "-" + picker_joined_date.getDayOfMonth();
            phoneNumber = tie_phone_number.getText().toString();
            phoneGuardian = tie_phone_number_guardian.getText().toString();
            SweetAlertDialog load = Utils.instanceSweetLoading(getActivity());
            if (checkInputs()) {
                load.show();
                Student student = new Student(idStudent, image, first, last, residence, spec, birthDay, email, join, phoneNumber, phoneGuardian);
                MiniStudent miniStudent = new MiniStudent(email, idStudent, image);
                FirebaseUtils.getStudents().child(idStudent).setValue(student).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUtils.getStudentsEmails(email).setValue(miniStudent);
                        ArrayList<Student> students;
                        if (teacher.getListStudents() != null) {
                            students = teacher.getListStudents();
                            students.add(0, student);
                        } else {
                            students = new ArrayList<>();
                            students.add(student);
                        }
                        teacher.setListStudents(students);
                        FirebaseUtils.getTeachers().child(teacher.getId()).setValue(teacher).addOnSuccessListener(unused -> {
                            String fullName = student.getFirst()+" " + student.getLast();
                            sendToStudent(phoneNumber, fullName);
                            sendToGuardian(phoneGuardian, fullName);
                            Toasty.success(getActivity(), "Added!", Toast.LENGTH_SHORT).show();
                            load.dismissWithAnimation();
                            getDialog().dismiss();


                        }).addOnFailureListener(e -> {
                            Log.d("TAG165", "onCreateView: 165");
                            Toasty.error(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            load.dismissWithAnimation();

                        });
                    } else {
                        Log.d("TAG165", "onCreateView: 171");
                        Toasty.error(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        load.dismissWithAnimation();
                    }
                });
            }
        });
        return view;
    }

    private void sendToGuardian(String phone, String fullName) {
        TwilioService.sendSms(Constants.PHONE_ADDRESS + phone, "Dear sir," + fullName + " is registered in Face Recognition Apps!");
    }

    private void sendToStudent(String phoneNumber, String fullName) {
        TwilioService.sendSms(Constants.PHONE_ADDRESS + phoneNumber, "Dear " + fullName + " You are registered in Face Recognition Apps!");
    }

    private void initPhoneNumber() {
        tie_phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String phoneNumber = editable.toString();
                til_phone_number.setHelperText("Write your number directly without +968");
                if (phoneNumber.isEmpty()) {
                    til_phone_number.setErrorEnabled(true);
                    til_phone_number.setError("Required!");
                } else {
                    til_phone_number.setErrorEnabled(false);
                }
            }
        });

        tie_phone_number_guardian.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phoneNumber = editable.toString();
                til_phone_number_guardian.setHelperText("Write your number directly without +968");
                if (phoneNumber.isEmpty()) {
                    til_phone_number_guardian.setErrorEnabled(true);
                    til_phone_number_guardian.setError("Required!");
                } else {
                    til_phone_number_guardian.setErrorEnabled(false);
                }
            }
        });
    }

    private void startSelectImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(getContext(), this);
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
        Toast.makeText(getActivity(), "You can not complete adding student!", Toast.LENGTH_SHORT).show();
    }


    private boolean checkInputs() {
        if (image == null || image.isEmpty()) {
            Toasty.error(getActivity(), "You have to select image for student with his face!", Toasty.LENGTH_SHORT).show();
            return false;
        } else if (first.isEmpty()) {
            til_first.setError("Required!");
            til_first.requestFocus();
            return false;
        } else if (last.isEmpty()) {
            til_last.setError("Required!");
            til_last.requestFocus();
            return false;
        } else if (residence.isEmpty()) {
            til_resid.setError("Required!");
            til_resid.requestFocus();
            return false;
        } else if (spec.isEmpty()) {
            til_spec.setError("Required!");
            til_spec.requestFocus();
            return false;
        } else if (email.isEmpty()) {
            til_email.setError("Required!");
            til_email.requestFocus();
            return false;
        }
        return true;
    }

    private void runFaceContourDetection(Uri b) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), b);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        FaceDetectorOptions options = new FaceDetectorOptions.Builder().setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST).setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL).build();

        //mFaceButton.setEnabled(false);
        FaceDetector detector = FaceDetection.getClient(options);
        //mFaceButton.setEnabled(true);
        detector.process(image).addOnSuccessListener(this::processFaceContourDetectionResult).addOnFailureListener(e -> {
            // Task failed with an exception
            //mFaceButton.setEnabled(true);
            loadingFace.dismissWithAnimation();
            Toasty.error(getActivity(), e.getMessage(), Toasty.LENGTH_SHORT).show();
            Toasty.error(getActivity(), "You have to select real face!", Toasty.LENGTH_SHORT).show();
            e.printStackTrace();
        });
    }

    private void processFaceContourDetectionResult(List<Face> faces) {
        if (faces.size() == 0) {
            loadingFace.dismissWithAnimation();
            Toasty.error(getActivity(), "No Face Found", Toasty.LENGTH_SHORT).show();
            return;
        }
        if (faces.size() > 1) {
            loadingFace.dismissWithAnimation();
            Toasty.error(getActivity(), "Only One Face Required", Toasty.LENGTH_SHORT).show();
            return;
        }
        StorageReference storageReference = FirebaseUtils.getStorageProfilesPictures();
        first = tie_first.getText().toString();
        last = tie_last.getText().toString();
        idStudent = Utils.createIdStudent(first, last);
        byte[] uri = Utils.uriToBytes(uriToUpload, getActivity());
        storageReference.child(idStudent).putBytes(uri).addOnFailureListener(e -> {
            Log.d("TAG165", "onCreateView: 356");
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            loadingFace.dismissWithAnimation();
        }).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri1 -> {
            image = uri1.toString();
            Toasty.success(getActivity(), "Success!", Toasty.LENGTH_SHORT).show();
            loadingFace.dismissWithAnimation();
        }));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri selectedImage = result.getUri();
                Glide.with(getContext()).load(selectedImage).into(civ);

                uriToUpload = selectedImage;
                try {
                    loadingFace = Utils.instanceSweetLoading(getActivity());
                    loadingFace.show();
                    runFaceContourDetection(uriToUpload);
                } catch (IOException e) {
                    Toasty.error(getActivity(), e.getMessage(), Toasty.LENGTH_LONG).show();

                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toasty.error(getActivity(), "Task Cancelled" + error.getMessage(), Toasty.LENGTH_LONG).show();

            }
        }
    }


    private void installImeOptions() {
        til_first.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == EditorInfo.IME_ACTION_NEXT) {
                til_last.requestFocus();
                return true;
            }
            return false;
        });

        til_last.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == EditorInfo.IME_ACTION_NEXT) {
                til_resid.requestFocus();
                return true;
            }
            return false;
        });
        til_resid.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == EditorInfo.IME_ACTION_NEXT) {
                til_spec.requestFocus();
                return true;
            }
            return false;
        });
        til_spec.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == EditorInfo.IME_ACTION_NEXT) {
                til_email.requestFocus();
                return true;
            }
            return false;
        });
    }

    private void initViews(View view) {
        civ = view.findViewById(R.id.iv_profile_image);
        picker_birth_day = view.findViewById(R.id.picker_birth_day);
        picker_joined_date = view.findViewById(R.id.picker_joined_date);
        til_first = view.findViewById(R.id.til_first_name);
        til_last = view.findViewById(R.id.til_last_name);
        til_resid = view.findViewById(R.id.til_resid);
        til_spec = view.findViewById(R.id.til_spec);
        til_email = view.findViewById(R.id.til_email);
        til_phone_number = view.findViewById(R.id.til_phone_number);
        til_phone_number_guardian = view.findViewById(R.id.til_phone_number_guardian);


        tie_first = view.findViewById(R.id.tie_first_name);
        tie_last = view.findViewById(R.id.tie_last_name);
        tie_resid = view.findViewById(R.id.tie_resid);
        tie_spec = view.findViewById(R.id.tie_spec);
        tie_email = view.findViewById(R.id.tie_email);
        tie_phone_number = view.findViewById(R.id.tie_phone_number);
        tie_phone_number_guardian = view.findViewById(R.id.tie_phone_number_guardian);
    }
}
