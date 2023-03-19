package com.omar.facerecognitionapps.camera.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;

import androidx.camera.core.ImageProxy;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ImageUtils {

    private ImageUtils(){
        throw new AssertionError();
    }

    public static Bitmap imageProxyToBitmap(ImageProxy imageProxy) throws Exception{
        return imageProxyToBitmap(imageProxy, imageProxy.getImageInfo().getRotationDegrees());
    }

    public static Bitmap imageProxyToBitmap(ImageProxy imageProxy,int rotationDegrees) throws Exception{
        ImageProxy.PlaneProxy[] plane = imageProxy.getPlanes();
        ByteBuffer yBuffer = plane[0].getBuffer();  // Y
        ByteBuffer uBuffer = plane[1].getBuffer();  // U
        ByteBuffer vBuffer = plane[2].getBuffer();  // V

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, imageProxy.getWidth(), imageProxy.getHeight(), null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream(nv21.length);
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 90, stream);

        Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
        if(rotationDegrees != 0){
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegrees);
            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }
}
