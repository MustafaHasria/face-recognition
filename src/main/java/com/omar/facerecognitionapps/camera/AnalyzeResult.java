package com.omar.facerecognitionapps.camera;

import android.graphics.Bitmap;

public class AnalyzeResult<T> {

    private Bitmap bitmap;

    private T result;

    public AnalyzeResult() {
    }

    public AnalyzeResult(Bitmap bitmap, T result) {
        this.bitmap = bitmap;
        this.result = result;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

}
