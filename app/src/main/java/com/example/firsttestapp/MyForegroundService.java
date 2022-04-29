package com.example.firsttestapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Date;

public class MyForegroundService extends Service {
    private Servicecallback main;
    private final IBinder binder = new LocalBinder();



//    private String res = "";

    public class LocalBinder extends Binder {
        MyForegroundService getService() {
            // Return this instance of MyService so clients can call public methods
            return MyForegroundService.this;
        }
    }

    public void setCallbacks(Servicecallback callbacks) {
        main = callbacks;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int old_time = 0;
                        String old_location = "0";

                        while (true) {
                            try {
                                Thread.sleep(8000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.e("Service", "Service is running...");

                            if (main != null) {
                                Date currentTime = Calendar.getInstance().getTime();

                                int current_time = currentTime.getHours(); //Integer between 0 and 23
                                String current_location = main.getLocation();
                                System.out.println("current location: "+ current_location);
                                System.out.println("current time: "+ current_time);
                                System.out.println("old location: "+ old_location);
                                System.out.println("old time: "+ old_time);

                                if ((old_time != current_time) || !(old_location.equals(current_location))){


                                    main.RL_Decision();

                                    System.out.println("Time or Location change detected");
                                }
                                old_time=current_time;
                                old_location=current_location;

                            }

//                            try {
//                                Thread.sleep(30000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }
                }
        ).start();

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );


        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Service is running")
                .setContentTitle("Service enabled")
                .setSmallIcon(R.drawable.ic_launcher_background);


        startForeground(1001, notification.build());





        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
