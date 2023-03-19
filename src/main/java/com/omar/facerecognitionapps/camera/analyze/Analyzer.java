package com.omar.facerecognitionapps.camera.analyze;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;
import com.omar.facerecognitionapps.camera.AnalyzeResult;


public interface Analyzer<T> {

    void analyze(@NonNull ImageProxy imageProxy, @NonNull OnAnalyzeListener<AnalyzeResult<T>> listener);

    public interface OnAnalyzeListener<T>{
        void onSuccess(@NonNull T result);
        void onFailure();
    }
}
