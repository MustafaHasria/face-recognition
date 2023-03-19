package com.omar.facerecognitionapps.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import android.widget.RemoteViews;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.omar.facerecognitionapps.NotificationReceiver;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.models.Teacher;
import com.omar.facerecognitionapps.ui.SplashActivity;
import io.paperdb.Paper;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "CustomFCMService";
    private static final String CHANNEL_ID = "notification_channel";

    @Override
    public void onNewToken(@NonNull @NotNull String token) {
        super.onNewToken(token);

    }
    void generateNotification(String title,String message){
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this,SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "channel_id")
                        .setSmallIcon(R.drawable.ic_notification).setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        notificationBuilder = notificationBuilder.setContent(getRemoteView(title,message));
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel_id",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private RemoteViews getRemoteView(String title, String message) {
        RemoteViews remote = new RemoteViews("com.omar.facerecognitionapps", R.layout.notification);
        remote.setTextViewText(R.id.title_not,title);
        remote.setTextViewText(R.id.desc_not,message);

        return remote;
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        String topic = data.get("topic");
        String type = data.get("type");
        String first = data.get("first");
        String last = data.get("last");
        String theId = data.get("idStudent");
        generateNotification("The Student :" + first + " " + last,"Is absent today!");
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "channel_id")
                        .setSmallIcon(R.drawable.ic_notification).setSound(defaultSoundUri);
        if (data.size() > 0) {
            if (topic != null) {
                if(topic.equals(Constants.TOPIC_TEACHER) && type.equals(Constants.TEACHER_NOTIFICATION)) {
                    notificationBuilder.setContentTitle("The Student :" + first + " " + last)
                            .setContentText("Is absent today!");
                    sendNotification(notificationBuilder,"The Student :" + first + " " + last,"Is absent today!");
                }
                else if(topic.equals(Constants.TOPIC_STUDENT)){
                    if(type.equals(Constants.STUDENT_UP)){
                        notificationBuilder.setContentTitle("Time!!")
                                .setContentText("Is 8:00, you have to login!");
                        sendNotification(notificationBuilder,"Time!!","Is 8:00, you have to login!");
                    }else if(type.equals(Constants.STUDENT_RETARD)){
                        String idStudent = Paper.book().read(Constants.STUDENT_ID);
                        if(theId.equals(idStudent)) {
                            notificationBuilder.setContentTitle("Time!!")
                                    .setContentText("Is 8:45, you have to login!");
                            sendNotification(notificationBuilder,"Time!!","Is 8:45, you have to login!");
                        }
                    }
                }
            }
        }
    }

    private void sendNotification(NotificationCompat.Builder notificationBuilder,String title,String message) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(pendingIntent);
        String channelName = getString(R.string.default_notification_channel_name);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


        Intent intent1 = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent1.putExtra("title",title );
        intent1.putExtra("message", message);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, 0);
        AlarmManager alarmManager1 = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent1);
    }
}
