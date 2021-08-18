package com.example.mynotes;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.Serializable;

public class AlertReceiver extends BroadcastReceiver implements Serializable {

    public static final String CHANNEL_ID="channel_id";
    NotificationManagerCompat managerCompat;
    String cagetory;



    @Override
    public void onReceive(Context context, Intent intent) {


        String message=intent.getStringExtra("message");
        String id=intent.getStringExtra("id");
        int index=intent.getIntExtra("index",0);
        try
        {
            NotesDB notesDB=new NotesDB(context);
            notesDB.open();
            notesDB.updateEntry(id,"not set");
            cagetory=notesDB.getDataAt(id).getCategory();
            notesDB.close();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,"Channel 1",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is Channel 1");

            NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        managerCompat= NotificationManagerCompat.from(context);
        Intent intent1=new Intent(context,ShowNotesActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("id",id);
        intent1.putExtra("flag",2);
        intent1.putExtra("index",index);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification=new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.notes)
                .setContentTitle("Note Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        int id1=Integer.parseInt(id);
        managerCompat.notify(id1,notification);

    }
}
