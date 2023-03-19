package com.omar.facerecognitionapps.camera.manager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AmbientLightManager implements SensorEventListener {

    private static final int INTERVAL_TIME = 200;

    protected static final float DARK_LUX = 45.0f;
    protected static final float BRIGHT_LUX = 100.0f;


    private float darkLightLux = DARK_LUX;

    private float brightLightLux = BRIGHT_LUX;

    private SensorManager sensorManager;
    private Sensor lightSensor;

    private long lastTime;

    private boolean isLightSensorEnabled;

    private OnLightSensorEventListener mOnLightSensorEventListener;

    public AmbientLightManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        isLightSensorEnabled = true;
    }

    public void register() {
        if (sensorManager != null && lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void unregister() {
        if (sensorManager != null && lightSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(isLightSensorEnabled){
            long currentTime = System.currentTimeMillis();
            if(currentTime - lastTime < INTERVAL_TIME){//降低频率
                return;
            }
            lastTime = currentTime;

            if (mOnLightSensorEventListener != null) {
                float lightLux = sensorEvent.values[0];
                mOnLightSensorEventListener.onSensorChanged(lightLux);
                if (lightLux <= darkLightLux) {
                    mOnLightSensorEventListener.onSensorChanged(true,lightLux);
                } else if (lightLux >= brightLightLux) {
                    mOnLightSensorEventListener.onSensorChanged(false,lightLux);
                }
            }
        }
    }

    public void setDarkLightLux(float lightLux){
        this.darkLightLux = lightLux;
    }


    public void setBrightLightLux(float lightLux){
        this.brightLightLux = lightLux;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    public boolean isLightSensorEnabled() {
        return isLightSensorEnabled;
    }

    public void setLightSensorEnabled(boolean lightSensorEnabled) {
        isLightSensorEnabled = lightSensorEnabled;
    }


    public void setOnLightSensorEventListener(OnLightSensorEventListener listener){
        mOnLightSensorEventListener = listener;
    }

    public interface OnLightSensorEventListener{

        default void onSensorChanged(float lightLux){

        }

        void onSensorChanged(boolean dark,float lightLux);
    }
}