package com.omar.facerecognitionapps.camera;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.view.PreviewView;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.camera.analyze.Analyzer;
import com.omar.facerecognitionapps.camera.config.CameraConfig;
import com.omar.facerecognitionapps.camera.util.LogUtils;
import com.omar.facerecognitionapps.camera.util.PermissionUtils;
import org.jetbrains.annotations.NotNull;

import static com.omar.facerecognitionapps.camera.CameraScan.LENS_FACING_FRONT;
import static java.lang.Thread.sleep;


public abstract class BaseCameraScanActivity<T> extends AppCompatActivity implements CameraScan.OnScanResultCallback<T>{

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 0X86;

    protected PreviewView previewView;
    private CameraScan<T> mCameraScan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isContentView()){
            setContentView(getLayoutId());
        }
        initUI();
    }


    public void initUI(){
        previewView = findViewById(getPreviewViewId());
        initCameraScan();
        startCamera();
    }

    public void initCameraScan(){
        mCameraScan = createCameraScan(previewView)
                .setAnalyzer(createAnalyzer())
                .setOnScanResultCallback(this);
    }


    public void startCamera(){
        if(mCameraScan != null){
            if(PermissionUtils.checkPermission(this,Manifest.permission.CAMERA)){
                mCameraScan.startCamera();
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }else{
                LogUtils.d("checkPermissionResult != PERMISSION_GRANTED");
                PermissionUtils.requestPermission(this,Manifest.permission.CAMERA,CAMERA_PERMISSION_REQUEST_CODE);
            }
        }
    }



    private void releaseCamera(){
        if(mCameraScan != null){
            mCameraScan.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_REQUEST_CODE){
            requestCameraPermissionResult(permissions,grantResults);
        }
    }

    public void requestCameraPermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults){
        if(PermissionUtils.requestPermissionsResult(Manifest.permission.CAMERA,permissions,grantResults)){
            startCamera();
        }else{
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        releaseCamera();
        super.onDestroy();
    }


    public boolean isContentView(){
        return true;
    }


    public int getLayoutId(){
        return R.layout.activity_student_login;
    }



    public int getPreviewViewId(){
        return R.id.previewView;
    }

    public CameraScan<T> getCameraScan(){
        return mCameraScan;
    }

    public CameraScan<T> createCameraScan(PreviewView previewView){
        return new BaseCameraScan<>(this,previewView);
    }


    @Nullable
    public abstract Analyzer<T> createAnalyzer();
}