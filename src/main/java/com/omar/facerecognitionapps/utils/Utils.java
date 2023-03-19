package com.omar.facerecognitionapps.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Utils {

    public static SweetAlertDialog instanceSweetLoading(Context context) {
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Loading");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
    public static byte[] uriToBytes(Uri imageUri, Context context) {
        // Get the content resolver
        ContentResolver contentResolver = context.getContentResolver();

// Open an input stream for the image Uri

// Read the contents of the input stream into a byte[] array
        byte[] data = new byte[0];
        try {
            InputStream inputStream = contentResolver.openInputStream(imageUri);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
// Close the input stream
    }
    public static <T> String objectToGson(Class<T> type, Object object) {
        type.cast(object);
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static <T> T gsonToObject(Intent intent, String constant, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(intent.getStringExtra(constant), type);
    }

    public static <T> T gsonToObjectForFragment(Bundle bundle, String constant, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(bundle.getString(constant), type);
    }
    public static boolean isToday(long t){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(t);
        int timestampDay = calendar.get(Calendar.DAY_OF_MONTH);
        int timestampMonth = calendar.get(Calendar.MONTH);
        int timestampYear = calendar.get(Calendar.YEAR);

        calendar = Calendar.getInstance();

        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        return timestampDay == currentDay && timestampMonth == currentMonth && timestampYear == currentYear;
    }
    public static String upperFirst(String name){
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }

    public static String timeToLetter(String date) {
        long time = Long.parseLong(date);

        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(time);
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        return month+"-"+day + " " +hour+":"+minute;
    }
    public static int H (long time){
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(time);
        return now.get(Calendar.HOUR_OF_DAY);
    }
    public static int M (long time){
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(time);
        return now.get(Calendar.MINUTE);
    }
    public static long[] getRand(String from,String to){
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date date;
        try {
            date = format.parse(from);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        assert date != null;
        calendar.setTime(date);

// Set the time to the start of the day (midnight)
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

// Get the start timestamp of the given day
        long startTimestamp = calendar.getTimeInMillis();
        try {
            date = format.parse(to);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        assert date != null;
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long endTimestamp = calendar.getTimeInMillis();
        return new long[]{startTimestamp,endTimestamp};
    }

    public static String createIdStudent(String first, String last) {
        return first.toLowerCase() + last.toLowerCase() + "_" + UUID.randomUUID().toString().substring(0, 4);
    }

    public static String toFirebase(String email) {
        return email.replace(".","_");
    }
    public static String fromFirebase(String email) {
        return email.replace("_",".");
    }
}
