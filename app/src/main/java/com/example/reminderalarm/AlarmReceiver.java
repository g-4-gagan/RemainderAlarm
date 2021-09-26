package com.example.reminderalarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

//        Toast.makeText(context, "Hello World!", Toast.LENGTH_SHORT).show();

        String reminder = intent.getStringExtra("reminder");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel2 = new NotificationChannel("Reminder","Reminder", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel2);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"Reminder");
        builder.setContentTitle("Alarm Reminder");
        builder.setContentText(reminder);
        builder.setSmallIcon(R.drawable.bulb_icon);
        builder.setAutoCancel(true);

        NotificationManagerCompat ManagerCompact = NotificationManagerCompat.from(context);
        ManagerCompact.notify(3,builder.build());

        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.start();

    }
}
