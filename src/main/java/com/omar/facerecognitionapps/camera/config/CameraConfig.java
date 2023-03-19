package com.omar.facerecognitionapps.camera.config;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;

import static com.omar.facerecognitionapps.camera.CameraScan.LENS_FACING_FRONT;

public class CameraConfig {

    public CameraConfig(){

    }

    @NonNull
    public Preview options(@NonNull Preview.Builder builder){
        return builder.build();
    }

    @NonNull
    public CameraSelector options(@NonNull CameraSelector.Builder builder){
        builder.requireLensFacing(LENS_FACING_FRONT);
        return builder.build();
    }

    @NonNull
    public ImageAnalysis options(@NonNull ImageAnalysis.Builder builder){
        return builder.build();
    }

}
