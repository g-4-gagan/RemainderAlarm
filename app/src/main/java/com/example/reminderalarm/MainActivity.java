package com.example.reminderalarm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    PendingIntent pendingIntent;

    public static final String MY_PREFS_FILENAME = "com.example.reminderalarm.Alarm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSet = findViewById(R.id.btnSet);
        Button btnCancel = findViewById(R.id.btnCancel);
        EditText etInput = findViewById(R.id.etInput);
        TimePicker timePicker = findViewById(R.id.timePicker);
        TextView tvAlarm = findViewById(R.id.tvAlarm);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_FILENAME,MODE_PRIVATE);
        String alarm = prefs.getString("alarm",null);
        boolean set = prefs.getBoolean("set",false);

        if (set){
            tvAlarm.setText(alarm);
            etInput.setVisibility(View.GONE);
            timePicker.setVisibility(View.GONE);
            btnSet.setVisibility(View.GONE);
        }
        else {
            btnCancel.setVisibility(View.GONE);
        }

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_FILENAME,MODE_PRIVATE).edit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("Alarm set","Alarm set", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channel1 = new NotificationChannel("Alarm cancel","Alarm cancel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            manager.createNotificationChannel(channel1);

        }

        btnSet.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = registerReceiver(null, ifilter);

                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                float batteryLevel = level * 100 / (float)scale;

                if (batteryLevel<20)
                {
                    Toast.makeText(MainActivity.this, "Please charge the battery before setting an alarm", Toast.LENGTH_LONG).show();
                }
                else {
                    if (etInput.getText().toString().isEmpty())
                    {
                        Toast.makeText(MainActivity.this, "Fill the Reminder field", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        intent.putExtra("reminder",etInput.getText().toString());

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                        calendar.set(Calendar.MINUTE, timePicker.getMinute());
                        calendar.set(Calendar.SECOND, 0);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"Alarm set");
                        builder.setContentTitle("Alarm set");
                        builder.setContentText("Your Alarm has been set to "+timePicker.getHour()+":"+timePicker.getMinute());
                        builder.setSmallIcon(R.drawable.bulb_icon);
                        builder.setAutoCancel(true);

                        NotificationManagerCompat ManagerCompact = NotificationManagerCompat.from(MainActivity.this);
                        ManagerCompact.notify(1,builder.build());

                        String Text =  "Alarm to: "+timePicker.getHour()+":"+timePicker.getMinute()+" Daily";
                        tvAlarm.setText(Text);
                        etInput.setVisibility(View.GONE);
                        timePicker.setVisibility(View.GONE);
                        btnSet.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.VISIBLE);

                        editor.putString("alarm" , Text);
                        editor.putBoolean("set",true);
                        editor.commit();

                        Toast.makeText(MainActivity.this, "Alarm has been set", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                Toast.makeText(MainActivity.this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"Alarm cancel");
                builder.setContentTitle("Alarm Cancel");
                builder.setContentText("Your Alarm has been cancelled! ");
                builder.setSmallIcon(R.drawable.bulb_icon);
                builder.setAutoCancel(true);

                editor.putBoolean("set",false);
                editor.commit();

                etInput.setVisibility(View.VISIBLE);
                timePicker.setVisibility(View.VISIBLE);
                btnSet.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                tvAlarm.setText("NO ALARM SET!");

                NotificationManagerCompat ManagerCompact = NotificationManagerCompat.from(MainActivity.this);
                ManagerCompact.notify(2,builder.build());

            }
        });
    }
}