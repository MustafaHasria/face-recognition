package com.omar.facerecognitionapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the service to send the notification
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent serviceIntent = new Intent(context, NotificationService.class);
        context.startService(serviceIntent);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"channel_id")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setSound(soundUri)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(0,builder.build());
    }
}
