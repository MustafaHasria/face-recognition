package com.omar.facerecognitionapps;


import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.models.Teacher;
import com.omar.facerecognitionapps.utils.Constants;
import com.omar.facerecognitionapps.utils.FirebaseUtils;
import com.omar.facerecognitionapps.utils.TwilioService;
import com.omar.facerecognitionapps.utils.Utils;

import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;

import static java.lang.Thread.sleep;


public class BaseApplication extends Application {
    //private VonageClient client;
    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                String deviceBsid = getDeviceBsid(); // retrieve bsid from device
                FirebaseDatabase.getInstance(Constants.DB_URL).getReference()
                        .child(Constants.BSSID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                                String firebaseBsid = dataSnapshot.getValue(String.class);
                                if (!deviceBsid.equals(firebaseBsid)) {
                                    Toasty.error(context, "The app is not authorized for you!", Toast.LENGTH_SHORT).show();
                                    try {
                                        sleep(1500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    System.exit(0);
                                }
                            }

                            @Override
                            public void onCancelled(@NotNull DatabaseError databaseError) {
                                System.out.println("Error checking  values: " + databaseError.getMessage());
                            }
                        });
            }
        }
    };

    private String getDeviceBsid() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getBSSID();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //  client = VonageClient.builder().apiKey("6054b1b2").apiSecret("3ccuFmkcSc2kIWiT").build();
        createNotificationChannel();
        setNotifications(this);
        Paper.init(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, filter);
        String idStudent = Paper.book().read(Constants.STUDENT_ID);
        long timeMillis = System.currentTimeMillis();
        if (Paper.book().contains(Constants.FOR_TODAY)) {
            long absent = Long.parseLong(Paper.book().read(Constants.FOR_TODAY));
            if (Utils.H(timeMillis) >= 21) {
                if (idStudent != null) {
                    if (!Utils.isToday(absent)) {
                        FirebaseUtils.getAbsents().child(idStudent).setValue(absent);
                        FirebaseUtils.getStudents().child(idStudent).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                Student student = snapshot.getValue(Student.class);
                                if (student.getPhoneGuardian() != null) {
                                    sendToGuardian(student.getPhoneGuardian(),
                                            Utils.upperFirst(student.getFirst()) + " " + Utils.upperFirst(student.getLast())
                                    );
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }
                }
            }
        }

        if (Utils.H(timeMillis) == 21 && Utils.M(timeMillis) == 0) {
            if (Paper.book().contains(Constants.TOPIC_REGISTERED)) {
                String topic = Paper.book().read(Constants.TOPIC_REGISTERED);
                if (Constants.TOPIC_TEACHER.equals(topic)) {
                    checkStudents();
                } else if (Constants.TOPIC_STUDENT.equals(topic)) {
                    if (Utils.H(timeMillis) == 20) {
                        RemoteMessage message = new RemoteMessage.Builder(topic)
                                .addData("type", Constants.STUDENT_UP)
                                .build();
                        FirebaseMessaging.getInstance().send(message);
                    } else if (Utils.H(timeMillis) == 20 && Utils.M(timeMillis) == 45) {
                        if (Paper.book().contains(Constants.FOR_TODAY)) {
                            long me = Long.parseLong(Paper.book().read(Constants.FOR_TODAY));
                            if (!Utils.isToday(me)) {
                                RemoteMessage message = new RemoteMessage.Builder(topic)
                                        .addData("type", Constants.DAYS_RETARD)
                                        .addData("idStudent", idStudent)
                                        .build();
                                FirebaseMessaging.getInstance().send(message);
                            }
                        }

                    }
                }
            }

        }
    }

    public void setNotifications(Context context) {
        // Set up notification for 7:55pm
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, 19);
        cal1.set(Calendar.MINUTE, 55);
        cal1.set(Calendar.SECOND, 0);

        Intent intent1 = new Intent(context, NotificationReceiver.class);
        intent1.putExtra("title", "Reminder");
        intent1.putExtra("message", "Please login to the app.");
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, 0);

        AlarmManager alarmManager1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP, cal1.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent1);

        // Set up notification for 8:00pm
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY, 20);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);

        Intent intent2 = new Intent(context, NotificationReceiver.class);
        intent2.putExtra("title", "Lesson Reminder");
        intent2.putExtra("message", "Your lesson is starting now.");
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 1, intent2, 0);

        AlarmManager alarmManager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent2);

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntente = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntente);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Notification Channel";
            String description = "This is my notification channel.";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendToGuardian(String phoneGuardian, String fullName) {
        TwilioService.sendSms(Constants.PHONE_ADDRESS+phoneGuardian.replace("+",""), "Dear sir,"+fullName+" is registered in Face Recognition Apps!");
    }

    private void checkStudents() {
        FirebaseUtils.getTeachers().child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        Teacher teacher = snapshot.getValue(Teacher.class);
                        for (Student id : teacher.getListStudents()) {
                            FirebaseUtils.getAbsents().child(id.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        long time = dataSnapshot.getValue(Long.class);
                                        if (Utils.isToday(time)) {
                                            FirebaseUtils.getStudents().child(id.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                    Student student = snapshot.getValue(Student.class);
                                                    String topic = Constants.TOPIC_TEACHER;
                                                    RemoteMessage message = new RemoteMessage.Builder(topic)
                                                            .addData("first", Utils.upperFirst(student.getFirst()))
                                                            .addData("last", Utils.upperFirst(student.getLast()))
                                                            .addData("type", Constants.TEACHER_NOTIFICATION)
                                                            .build();
                                                    FirebaseMessaging.getInstance().send(message);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(wifiReceiver);
    }
}
