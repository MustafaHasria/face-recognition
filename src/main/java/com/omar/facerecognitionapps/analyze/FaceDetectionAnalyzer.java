package com.omar.facerecognitionapps.analyze;

import android.graphics.Bitmap;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.omar.facerecognitionapps.camera.AnalyzeResult;
import com.omar.facerecognitionapps.camera.analyze.Analyzer;
import com.omar.facerecognitionapps.camera.util.BitmapUtils;
import com.omar.facerecognitionapps.camera.util.LogUtils;


import java.util.List;

import static java.lang.Thread.sleep;


public class FaceDetectionAnalyzer implements Analyzer<List<Face>> {

    private FaceDetector mDetector;
    public FaceDetectionAnalyzer(){
        this(null);
    }

    public FaceDetectionAnalyzer(FaceDetectorOptions options){

        if(options != null){
            mDetector = FaceDetection.getClient(options);
        }else{
            mDetector = FaceDetection.getClient(new FaceDetectorOptions.Builder()
                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                    .build());
        }
    }


    @Override
    public void analyze(@NonNull ImageProxy imageProxy, @NonNull OnAnalyzeListener<AnalyzeResult<List<Face>>> listener) {
        try{

            final Bitmap bitmap = BitmapUtils.getBitmap(imageProxy);
            InputImage inputImage = InputImage.fromBitmap(bitmap,0);

            mDetector.process(inputImage)
                    .addOnSuccessListener(result -> {
                        if(result == null || result.isEmpty()){
                            listener.onFailure();
                        }else{
                            listener.onSuccess(new AnalyzeResult<>(bitmap,result));
                        }
                    }).addOnFailureListener(e -> {
                listener.onFailure();
            });
        }catch (Exception e){
            LogUtils.w(e);
        }
    }
}
