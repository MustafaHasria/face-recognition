package com.omar.facerecognitionapps.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;

import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

public class FaceComparator {
    private final FaceDetector faceDetector;

    public FaceComparator(Context context) {
        faceDetector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.NO_LANDMARKS)
                .setClassificationType(FaceDetector.NO_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        android.util.Log.d(TAG, "FaceComparator: "+ faceDetector);
    }

    public float compareFaces(Bitmap face1, Bitmap face2) {
        Face faceOne = detectFaceInBitmap(face1);
        Face faceTwo = detectFaceInBitmap(face2);

        if (faceOne == null || faceTwo == null) {
            return 0.0f;
        }

        float similarityScore = 0.0f;
        similarityScore += compareLandmarks(faceOne.getLandmarks(), faceTwo.getLandmarks());


        similarityScore /= (float) faceOne.getLandmarks().size();
        return similarityScore;
    }

    public Face detectFaceInBitmap(Bitmap bitmap) {
        Log.e("CAMO", "analyze: 45" );
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        if (faces.size() == 0) {
            return null;
        }
        return faces.valueAt(0);
    }

    public float compareLandmarks(List<Landmark> landmarks1, List<Landmark> landmarks2) {
        float similarityScore = 0.0f;
        for (int i = 0; i < landmarks1.size(); i++) {
            Landmark landmark1 = landmarks1.get(i);
            Landmark landmark2 = landmarks2.get(i);
            similarityScore += comparePositions(landmark1.getPosition(), landmark2.getPosition());
        }
        return similarityScore;
    }

    public float comparePositions(PointF point1, PointF point2) {
        float xDiff = point1.x - point2.x;
        float yDiff = point1.y - point2.y;
        return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
}