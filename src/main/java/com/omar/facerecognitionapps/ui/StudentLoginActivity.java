package com.omar.facerecognitionapps.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.omar.facerecognitionapps.analyze.FaceDetectionAnalyzer;
import com.omar.facerecognitionapps.camera.analyze.Analyzer;

import java.util.List;

public class StudentLoginActivity extends FaceDetectionActivity{
    @SuppressLint("RestrictedApi")
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Analyzer<List<Face>> createAnalyzer() {
        return new FaceDetectionAnalyzer(new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build());
    }
}
