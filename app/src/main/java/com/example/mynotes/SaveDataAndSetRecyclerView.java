package com.example.mynotes;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SaveDataAndSetRecyclerView {

    Context context;
    Intent received;
    ArrayList<Notes> listNotes;
    RecyclerView.Adapter myAdapter;
    String activity;

    public SaveDataAndSetRecyclerView(Context context, Intent received, ArrayList<Notes> listNotes,
                                      RecyclerView.Adapter myAdapter,String activity)
    {
        this.context=context;
        this.received=received;
        this.listNotes=listNotes;
        this.myAdapter=myAdapter;
        this.activity=activity;
    }

    public void saveDataAndRecyclerView()
    {
        String s=received.getStringExtra("notes");
        String str=received.getStringExtra("heading");
        int flag=received.getIntExtra("flag",0);
        String category=received.getStringExtra("category");
        ArrayList<TodoListClass> todoList=(ArrayList<TodoListClass>)received.getSerializableExtra("todoList");
        try
        {
            NotesDB notesDB = new NotesDB(context);
            notesDB.open();
            if(flag==0)
            {
                if(!s.isEmpty() || !str.isEmpty() || !todoList.isEmpty())
                {
                    boolean isAlarmSet=received.getBooleanExtra("isAlarmSet",false);
                    Calendar calendar=(Calendar)received.getSerializableExtra("calendar");
                    String alarmDate=received.getStringExtra("alarmDate");
                    String alarmTime=received.getStringExtra("alarmTime");


                    String alarm="set";
                    if(!isAlarmSet) alarm="not set";
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy",
                            Locale.getDefault()).format(new Date());

                    String currentTime = new SimpleDateFormat("HH:mm:ss",
                            Locale.getDefault()).format(new Date());

                    String todoString=new Gson().toJson(todoList);
                    notesDB.createEntry(s,str,currentDate,currentTime,alarm,alarmDate,alarmTime,category,todoString);
                    Notes notes = notesDB.getLastData();
                    s=notes.getNotes();
                    str=notes.getHeading();
                    if(str.isEmpty() && todoList.size()>0)
                    {
                        str=todoList.get(0).getTodo();
                        if(s.isEmpty())
                        {
                            for(int i=1;i<Math.min(5,todoList.size());i++)
                            {
                                s+=todoList.get(i).getTodo();
                                s+="\n";
                            }
                        }
                    }
                    else
                    {
                        if(s.isEmpty() && todoList.size()>0)
                        {
                            for(int i=0;i<Math.min(5,todoList.size());i++)
                            {
                                s+=todoList.get(i).getTodo();
                                s+="\n";
                            }
                        }
                    }
                    s=trimTheString(s,0);
                    str=trimTheString(str,1);
                    notes.setNotes(s);
                    notes.setHeading(str);
                    if(activity.equals(category))
                    {
                        listNotes.add(0,notes);
                        NotesAdapter.f=0;
                        myAdapter.notifyDataSetChanged();
                    }
                    if(isAlarmSet)
                    {
                        String message="";
                        if(category.equals("hidden"))
                        {
                            message="Hidden Notes";
                        }
                        else
                        {
                            if(!str.isEmpty())
                            {
                                message=str;
                            }
                            else
                            {
                                message=s;
                            }
                        }
                        SetAlarm alarmInstance=new SetAlarm(calendar,context,
                                listNotes.get(0).getId(),0,message);
                        alarmInstance.setAlarm();
//                        Toast.makeText(context,"Alarm is set!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                String id=received.getStringExtra("id");
                int index=received.getIntExtra("index",0);
                boolean isAlarmSet=received.getBooleanExtra("isAlarmSet",false);
                String alarm="set";
                if(!isAlarmSet) alarm="not set";
                boolean iskeyPressed=received.getBooleanExtra("isKeyPressed",false);
                Calendar calendar=(Calendar)received.getSerializableExtra("calendar");
                String alarmDate=received.getStringExtra("alarmDate");
                String alarmTime=received.getStringExtra("alarmTime");
                if(iskeyPressed)
                {
                    listNotes.remove(index);
                    NotesAdapter.f=0;
                    myAdapter.notifyDataSetChanged();
                    if(!(s.isEmpty() && str.isEmpty()) || todoList.size()>0)
                    {
                        String currentDate = new SimpleDateFormat("dd-MM-yyyy",
                                Locale.getDefault()).format(new Date());
                        String currentTime = new SimpleDateFormat("HH:mm:ss",
                                Locale.getDefault()).format(new Date());

//                        Toast.makeText(context,alarm,Toast.LENGTH_SHORT).show();
                        String todoString=new Gson().toJson(todoList);
                        notesDB.updateEntry(id,s,str,currentDate,currentTime,alarm,alarmDate,alarmTime,category,todoString);
                        Notes notes = notesDB.getDataAt(id);
                        s=notes.getNotes();
                        str=notes.getHeading();
                        if(str.isEmpty() && todoList.size()>0)
                        {
                            str=todoList.get(0).getTodo();
                            if(s.isEmpty())
                            {
                                for(int i=1;i<Math.min(5,todoList.size());i++)
                                {
                                    s+=todoList.get(i).getTodo();
                                    s+="\n";
                                }
                            }
                        }
                        else
                        {
                            if(s.isEmpty() && todoList.size()>0)
                            {
                                for(int i=0;i<Math.min(5,todoList.size());i++)
                                {
                                    s+=todoList.get(i).getTodo();
                                    s+="\n";
                                }
                            }
                        }
                        s=trimTheString(s,0);
                        str=trimTheString(str,1);
                        notes.setNotes(s);
                        notes.setHeading(str);
                        if(activity.equals(category))
                        {
                            listNotes.add(0,notes);
                            NotesAdapter.f=0;
                            myAdapter.notifyDataSetChanged();
                        }

                        if(isAlarmSet && calendar!=null)
                        {
                            String message="";
                            if(category.equals("hidden"))
                            {
                                message="Hidden Notes";
                            }
                            else
                            {
                                if(!str.isEmpty())
                                {
                                    message=str;
                                }
                                else
                                {
                                    message=s;
                                }
                            }
                            SetAlarm alarmInstance=new SetAlarm(calendar,context,
                                    listNotes.get(0).getId(),0,message);
                            alarmInstance.setAlarm();
                        }
                        else if(isAlarmSet && calendar==null)
                        {

                        }
                    }
                    else
                    {
                        if(isAlarmSet)
                        {
                            SetAlarm instance=new SetAlarm(calendar,context,id,index,"");
                            instance.cancelAlarm();
                        }
                        notesDB.deleteEntry(id);
                    }
                }
            }
        }
        catch (SQLiteException e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public static String trimTheString(String s,int type)
    {
        if(type==0)
        {
            int f=0;
            if(s.length()>200)
            {
                s=s.substring(0,200);
                f=1;
            }
            int count=0;
            int in=-1;
            for(int i=0;i<s.length();i++)
            {
                if(s.charAt(i)=='\n')
                {
                    count++;
                }
                if(count==10)
                {
                    in=i;
                    break;
                }
            }
            if(in!=-1)
            {
                s=s.substring(0,in);
                f=1;
            }
            if(f==1)
            {
                s+="...";
            }
        }
        else
        {
            if(s.length()>40)
            {
                s=s.substring(0,40);
            }
            int count=0;
            int in=-1;
            for(int i=0;i<s.length();i++)
            {
                if(s.charAt(i)=='\n')
                {
                    count++;
                }
                if(count==2)
                {
                    in=i;
                    break;
                }
            }
            if(in!=-1)
            {
                s=s.substring(0,in);
            }
        }
        return s;
    }

}
