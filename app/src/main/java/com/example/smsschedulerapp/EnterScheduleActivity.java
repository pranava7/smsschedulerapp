package com.example.smsschedulerapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class EnterScheduleActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    TextView time;
    Calendar cal;
    TextView displaydate;
    DatePickerDialog.OnDateSetListener dateSetListener;
    Button schedulebtn;
    int yearnow=-1,monthnow=-1,daynow=-1;
    int hournow=-1,minnow=-1;
    String msg, telno;

    int yearselected=-1,monthselected=-1,dayselected=-1,hourselected=-1,minselected=-1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterschedule);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        msg= extras.getString("message");
        telno= extras.getString("telno");



        time =findViewById(R.id.time);
        schedulebtn = findViewById(R.id.schedulesmsg);
        displaydate = findViewById(R.id.date);


        displaydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal = Calendar.getInstance();
                yearnow = cal.get(Calendar.YEAR);
                monthnow = cal.get(Calendar.MONTH);
                daynow = cal.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialog= new DatePickerDialog(
                        EnterScheduleActivity.this, android.R.style.Theme_Holo_Dialog, dateSetListener,yearnow,monthnow, daynow);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                yearselected=year;
                monthselected=month;
                dayselected=dayOfMonth;

                if((yearselected==yearnow&& monthselected>monthnow)||yearselected>yearnow||(yearselected==yearnow&&monthselected==monthnow&&dayselected>=daynow)){
                    String date= dayOfMonth+ "/" + month+ "/"+ year;
                    displaydate.setText(date);
                }
                else
                    Toast.makeText(getApplicationContext(), "Enter a valid Date", Toast.LENGTH_SHORT).show();


            }
        };


        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timepicker = new Timepickerfragment();
                timepicker.show(getSupportFragmentManager(),"time picker");
                Calendar c = Calendar.getInstance();
                hournow = c.get(Calendar.HOUR_OF_DAY);
                minnow = c.get(Calendar.MINUTE);

            }
        });


        schedulebtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                checkpermission();
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourselected=hourOfDay;
        minselected=minute;
        if((dayselected==daynow&&monthselected==monthnow&& yearnow==yearselected)&&(hourselected==hournow&&minselected<minnow))
        {
            Toast.makeText(getApplicationContext(), "Enter Valid Time", Toast.LENGTH_SHORT).show();
        }
        else
            time.setText(String.format("%02d:%02d", hourOfDay, minute));
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkpermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        } else {
            checkinput();

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkinput() {

        if (dayselected==-1)
        {
            Toast.makeText(getApplicationContext(), "Select Date", Toast.LENGTH_SHORT).show();
        } else if (hourselected==-1)
        {
            Toast.makeText(getApplicationContext(), "Select time", Toast.LENGTH_SHORT).show();
        }
        else
        {
            sendsms();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sendsms() {


        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, yearselected);
        c.set(Calendar.MONTH, monthselected);
        c.set(Calendar.DAY_OF_MONTH, dayselected);
        c.set(Calendar.HOUR_OF_DAY, hourselected);
        c.set(Calendar.MINUTE, minselected);
        c.set(Calendar.SECOND, 0);

        Intent intent = new Intent(EnterScheduleActivity.this, AlarmService.class);
        intent.putExtra("message",msg);
        intent.putExtra("telno",telno);


        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

        Toast.makeText(this, "SMS is scheduled", Toast.LENGTH_SHORT).show();

    }


}
