package com.omar.facerecognitionapps.camera;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;

import com.omar.facerecognitionapps.camera.analyze.Analyzer;
import com.omar.facerecognitionapps.camera.config.CameraConfig;


public abstract class CameraScan<T> implements ICamera, ICameraControl {

    public static String SCAN_RESULT = "SCAN_RESULT";

    /** A camera on the device facing the same direction as the device's screen. */
    public static int LENS_FACING_FRONT = CameraSelector.LENS_FACING_FRONT;
    /** A camera on the device facing the opposite direction as the device's screen. */
    public static int LENS_FACING_BACK = CameraSelector.LENS_FACING_BACK;


    private boolean isNeedTouchZoom = true;


    protected boolean isNeedTouchZoom() {
        return isNeedTouchZoom;
    }



    public CameraScan setNeedTouchZoom(boolean needTouchZoom) {
        isNeedTouchZoom = needTouchZoom;
        return this;
    }

    public abstract CameraScan setCameraConfig(CameraConfig cameraConfig);

    //public abstract CameraScan setCameraConfig(CameraConfig cameraConfig);


    public abstract CameraScan setAnalyzeImage(boolean analyze);


    //public abstract CameraScan setAnalyzer(Analyzer<T> analyzer);

    public abstract CameraScan setAnalyzer(Analyzer<T> analyzer);


    public abstract CameraScan setVibrate(boolean vibrate);


    public abstract CameraScan setPlayBeep(boolean playBeep);


    public abstract CameraScan setOnScanResultCallback(OnScanResultCallback<T> callback);


    public abstract CameraScan bindFlashlightView(@Nullable View v);


    public abstract CameraScan setDarkLightLux(float lightLux);

    public abstract CameraScan setBrightLightLux(float lightLux);

    public interface OnScanResultCallback<T>{

        void onScanResultCallback(@NonNull AnalyzeResult<T> result);

        default void onScanResultFailure(){

        }

    }


    @Nullable
    public static String parseScanResult(Intent data){
        if(data != null){
            return data.getStringExtra(SCAN_RESULT);
        }
        return null;
    }

}
