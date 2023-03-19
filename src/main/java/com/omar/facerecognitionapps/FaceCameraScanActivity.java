package com.omar.facerecognitionapps;

import androidx.annotation.Nullable;

import com.google.mlkit.vision.face.Face;
import com.omar.facerecognitionapps.analyze.FaceDetectionAnalyzer;
import com.omar.facerecognitionapps.camera.BaseCameraScanActivity;
import com.omar.facerecognitionapps.camera.analyze.Analyzer;

import java.util.List;


public abstract class FaceCameraScanActivity extends BaseCameraScanActivity<List<Face>> {

    @Nullable
    @Override
    public Analyzer<List<Face>> createAnalyzer() {
        return new FaceDetectionAnalyzer();
    }

}
