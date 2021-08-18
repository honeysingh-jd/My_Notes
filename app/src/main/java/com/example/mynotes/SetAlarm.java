package com.example.mynotes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Calendar;

public class SetAlarm implements Serializable {

    Calendar c;
    Context context;
    String message;
    String id;
    int index;

    public SetAlarm(Calendar c, Context context, String id,int index,String message)
    {
        this.c=c;
        this.context=context;
        this.id=id;
        this.index=index;
        this.message=message;
    }

    public void setAlarm()
    {
        int requestCode=Integer.parseInt(id);
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context,AlertReceiver.class);
        intent.putExtra("message",message);
        intent.putExtra("id",id);
        intent.putExtra("index",index);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
    }

    public void cancelAlarm()
    {
        int requestCode=Integer.parseInt(id);
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context,AlertReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

}
