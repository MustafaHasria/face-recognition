package com.omar.facerecognitionapps.camera.config;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Size;


import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import com.omar.facerecognitionapps.camera.util.LogUtils;


public class ResolutionCameraConfig extends CameraConfig {

    private Size mTargetSize;
    
    public ResolutionCameraConfig(Context context) {
        super();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        LogUtils.d(String.format("displayMetrics:%d x %d",width,height));
        if(width < height){
            int size = Math.min(width, 1080);
            float ratio =  width / (float)height;
            if(ratio > 0.7){//一般应用于平板
                mTargetSize = new Size(size, (int)(size / 3.0f * 4.0f));
            }else{
                mTargetSize = new Size(size, (int)(size / 9.0f * 16.0f));
            }
        }else{
            int size = Math.min(height, 1080);
            float ratio = height / (float)width;
            if(ratio > 0.7){//一般应用于平板
                mTargetSize = new Size((int)(size / 3.0f * 4.0f), size);
            }else{
                mTargetSize = new Size((int)(size / 9.0f * 16.0), size);
            }
        }
        LogUtils.d("targetSize:" + mTargetSize);
    }



    @NonNull
    @Override
    public Preview options(@NonNull Preview.Builder builder) {
        return super.options(builder);
    }

    @NonNull
    @Override
    public CameraSelector options(@NonNull CameraSelector.Builder builder) {
        return super.options(builder);
    }

    @NonNull
    @Override
    public ImageAnalysis options(@NonNull ImageAnalysis.Builder builder) {
        builder.setTargetResolution(mTargetSize);
        return super.options(builder);
    }
}
