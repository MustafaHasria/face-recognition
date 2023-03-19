package com.omar.facerecognitionapps.camera;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.camera.analyze.Analyzer;
import com.omar.facerecognitionapps.camera.util.LogUtils;
import com.omar.facerecognitionapps.camera.util.PermissionUtils;


public abstract class BaseCameraScanFragment<T> extends Fragment implements CameraScan.OnScanResultCallback<T> {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 0X86;

    private View mRootView;

    protected PreviewView previewView;
    protected View ivFlashlight;

    private CameraScan mCameraScan;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(isContentView()){
            mRootView = createRootView(inflater,container);
        }
        initUI();
        return mRootView;
    }

    /**
     * 初始化
     */
    public void initUI(){
        previewView = mRootView.findViewById(getPreviewViewId());
        initCameraScan();
        startCamera();
    }


    protected void onClickFlashlight(){
        toggleTorchState();
    }


    public void initCameraScan(){
        mCameraScan = createCameraScan(previewView)
                .setAnalyzer(createAnalyzer())
                .setOnScanResultCallback(this);
    }


    public void startCamera(){
        if(mCameraScan != null){
            if(PermissionUtils.checkPermission(getContext(), Manifest.permission.CAMERA)){
                mCameraScan.startCamera();
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


    protected void toggleTorchState(){
        if(mCameraScan != null){
            boolean isTorch = mCameraScan.isTorchEnabled();
            mCameraScan.enableTorch(!isTorch);
            if(ivFlashlight != null){
                ivFlashlight.setSelected(!isTorch);
            }
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
            getActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        releaseCamera();
        super.onDestroy();
    }

    /**
     * 返回true时会自动初始化{@link #createRootView(LayoutInflater, ViewGroup)}，返回为false是需自己去初始化{@link #createRootView(LayoutInflater, ViewGroup)}
     * @return 默认返回true
     */
    public boolean isContentView(){
        return true;
    }

    /**
     * 创建{@link #mRootView}
     * @param inflater
     * @param container
     * @return
     */
    @NonNull
    public View createRootView(LayoutInflater inflater, ViewGroup container){
        return inflater.inflate(getLayoutId(),container,false);
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


    //--------------------------------------------

    public View getRootView() {
        return mRootView;
    }


    public CameraScan<T> createCameraScan(PreviewView previewView){
        return new BaseCameraScan<>(this,previewView);
    }


    @Nullable
    public abstract Analyzer<T> createAnalyzer();

}
