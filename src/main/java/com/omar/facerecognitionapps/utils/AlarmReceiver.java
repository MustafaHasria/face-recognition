package com.omar.facerecognitionapps.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.models.Report;
import io.paperdb.Paper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;
import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Paper.init(context);
        int notificationId = intent.getIntExtra("notification_id", 0);
        String name = intent.getStringExtra("name");
        switch (notificationId) {
            case 1:
                createNotification(context, "First Alarm", "It's 8:00. Time for class.", 1,PRIORITY_DEFAULT);
                break;
            case 2:
                createNotification(context, "Last Warning", "It's 8:45. Last chance to get to class.", 2,PRIORITY_HIGH);

                break;
            case 3:
                createNotification(context, "Notification for Supervisor", "It's 9:00. "+name+" is absent!", 3,PRIORITY_HIGH);
                break;
            default:
                break;
        }
    }

    private void createNotification(Context context, String t, String d, int i,int pr) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Attendance Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("attendance_channel", name, importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "attendance_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(t)
                .setContentText(d)
                .setPriority(pr)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(i, builder.build());
    }
}