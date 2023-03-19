package com.omar.facerecognitionapps.camera;

import androidx.annotation.FloatRange;


public interface ICameraControl {

    void zoomIn();


    void zoomOut();


    void zoomTo(float ratio);

    void lineZoomIn();

    /**
     * 线性缩小
     */
    void lineZoomOut();

    void lineZoomTo(@FloatRange(from = 0.0,to = 1.0) float linearZoom);


    void enableTorch(boolean torch);


    boolean isTorchEnabled();

    boolean hasFlashUnit();
}
