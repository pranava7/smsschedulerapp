package com.example.smsschedulerapp;

import java.util.Calendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smsschedulerapp.AlarmService;
import com.example.smsschedulerapp.R;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    EditText editTextphone;
    EditText message;
    Button sendnow, sendlater;
    String sms;
    String telno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextphone = findViewById(R.id.editTextPhone);
        message = findViewById(R.id.message);
        sendnow = findViewById(R.id.sendnowbtn);
        sendlater = findViewById(R.id.sendlaterbtn);

        sendnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sms = message.getText().toString();
                telno = editTextphone.getText().toString();

                checkpermission();



            }
        });

        sendlater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sms = message.getText().toString();
                telno = editTextphone.getText().toString();
                Intent intent = new Intent(MainActivity.this, EnterScheduleActivity.class);
                intent.putExtra("message",sms);
                intent.putExtra("telno", telno);

                startActivity(intent);
            }
        });


    }

    private void checkpermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        } else {
            checkinput();
        }

    }

    private void checkinput() {

        if (telno.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Enter a phone number.", Toast.LENGTH_SHORT).show();

        } else if (sms.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Enter a message.", Toast.LENGTH_SHORT).show();

        }
        else{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telno, null, sms, null, null);
        }
    }
}



