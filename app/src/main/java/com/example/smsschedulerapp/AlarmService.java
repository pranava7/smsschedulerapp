package com.example.smsschedulerapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.telephony.SmsManager;

import androidx.annotation.Nullable;

public class AlarmService extends Service {
    String msg,telno;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        msg= intent.getStringExtra("message");
        telno= intent.getStringExtra("telno");
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(telno, null, msg, null, null);

        return START_STICKY;

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent restartintent = new Intent(getApplicationContext(),this.getClass());
        restartintent.setPackage(getPackageName());

        startService(restartintent);
        super.onTaskRemoved(rootIntent);
    }
}


