package com.example.mynotes;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class NotesDB {

    public static final String DATABASE_TABLE="notes_table";
    public static final String DATABASE_NAME="notesDB";
    public static final int DATABASE_VERSION=1;
    public static final String KEY_ID="_id";
    public static final String KEY_NOTES="_notes";
    public static final String KEY_HEADING="_heading";
    public static final String KEY_DATE="_date";
    public static final String KEY_TIME="_time";
    public static final String KEY_ALARM="_alarm";
    public static final String KEY_ALARM_DATE="alarm_date";
    public static final String KEY_ALARM_TIME="alarm_time";
    public static final String KEY_CATEGORY="_category";
    public static final String KEY_TODO_STRING="todo_string";

    public Context ourContext;
    public SQLiteDatabase ourDataBase;
    public DBHelper ourHelper;

    public NotesDB(Context context)
    {
        this.ourContext=context;
    }

    private class DBHelper extends SQLiteOpenHelper
    {

        public DBHelper(@Nullable Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sqlCode="CREATE TABLE "+DATABASE_TABLE+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+KEY_NOTES+" TEXT NOT NULL, "+
                    KEY_HEADING+" TEXT NOT NULL, "+KEY_DATE+" TEXT NOT NULL, "+KEY_TIME+" TEXT NOT NULL, "+
                    KEY_ALARM+" TEXT NOT NULL, "+KEY_ALARM_DATE+" TEXT NOT NULL, "+KEY_ALARM_TIME+" TEXT NOT NULL, "
                    +KEY_CATEGORY+" TEXT NOT NULL, "+KEY_TODO_STRING+" TEXT NOT NULL);";

            db.execSQL(sqlCode);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String sqlCode="DROP TABLE IF EXISTS "+DATABASE_TABLE;
            db.execSQL(sqlCode);
        }
    }

    public NotesDB open() throws SQLiteException
    {
        ourHelper=new DBHelper(ourContext);
        ourDataBase=ourHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        ourHelper.close();
    }

    public long createEntry(String notes,String heading,String date,String time,String alarm,String alarmDate,String alarmTime
    ,String category,String todoString)
    {
        ContentValues cv=new ContentValues();
        cv.put(KEY_NOTES,notes);
        cv.put(KEY_HEADING,heading);
        cv.put(KEY_DATE,date);
        cv.put(KEY_TIME,time);
        cv.put(KEY_ALARM,alarm);
        cv.put(KEY_ALARM_DATE,alarmDate);
        cv.put(KEY_ALARM_TIME,alarmTime);
        cv.put(KEY_CATEGORY,category);
        cv.put(KEY_TODO_STRING,todoString);
        return ourDataBase.insert(DATABASE_TABLE,null,cv);
    }


    public ArrayList<Notes> getData()
    {
        String columns[]=new String[]{KEY_ID,KEY_NOTES,KEY_HEADING,KEY_DATE,KEY_TIME,KEY_ALARM,KEY_ALARM_DATE,KEY_ALARM_TIME
        ,KEY_CATEGORY,KEY_TODO_STRING};
        Cursor c=ourDataBase.query(DATABASE_TABLE,columns,null,null,null,null,null);
        ArrayList<Notes> list=new ArrayList<>();
        int iRowId=c.getColumnIndex(KEY_ID);
        int iNotes=c.getColumnIndex(KEY_NOTES);
        int iHeading=c.getColumnIndex(KEY_HEADING);
        int iDate=c.getColumnIndex(KEY_DATE);
        int iTime=c.getColumnIndex(KEY_TIME);
        int iAlarm=c.getColumnIndex(KEY_ALARM);
        int iAlarmDate=c.getColumnIndex(KEY_ALARM_DATE);
        int iAlarmTime=c.getColumnIndex(KEY_ALARM_TIME);
        int iCategory=c.getColumnIndex(KEY_CATEGORY);
        int iTodoString=c.getColumnIndex(KEY_TODO_STRING);
        for(c.moveToLast();!c.isBeforeFirst();c.moveToPrevious())
        {
            String notes=c.getString(iNotes);
            String id=c.getString(iRowId);
            String heading=c.getString(iHeading);
            String date=c.getString(iDate);
            String time=c.getString(iTime);
            String alarm=c.getString(iAlarm);
            String alarmDate=c.getString(iAlarmDate);
            String alarmTime=c.getString(iAlarmTime);
            String category=c.getString(iCategory);
            String todoString=c.getString(iTodoString);
            boolean isAlarm=false;
            if(alarm.equals("set")) isAlarm=true;
            ArrayList<TodoListClass> todoList=new Gson().fromJson(todoString,
                    new TypeToken<ArrayList<TodoListClass>>() {}.getType());
            list.add(new Notes(notes,heading,id,date,time,isAlarm,alarmDate,alarmTime,category,todoList));
        }
        c.close();
        return list;
    }


    public Notes getLastData()
    {
        String columns[]=new String[]{KEY_ID,KEY_NOTES,KEY_HEADING,KEY_DATE,KEY_TIME,KEY_ALARM,KEY_ALARM_DATE,KEY_ALARM_TIME
        ,KEY_CATEGORY,KEY_TODO_STRING};
        Cursor c=ourDataBase.query(DATABASE_TABLE,columns,null,null,null,null,null);
        int iRowId=c.getColumnIndex(KEY_ID);
        int iNotes=c.getColumnIndex(KEY_NOTES);
        int iHeading=c.getColumnIndex(KEY_HEADING);
        int iDate=c.getColumnIndex(KEY_DATE);
        int iTime=c.getColumnIndex(KEY_TIME);
        int iAlarm=c.getColumnIndex(KEY_ALARM);
        int iAlarmDate=c.getColumnIndex(KEY_ALARM_DATE);
        int iAlarmTime=c.getColumnIndex(KEY_ALARM_TIME);
        int iCategory=c.getColumnIndex(KEY_CATEGORY);
        int iTodoString=c.getColumnIndex(KEY_TODO_STRING);
        c.moveToLast();
        String notes=c.getString(iNotes);
        String id=c.getString(iRowId);
        String heading=c.getString(iHeading);
        String date=c.getString(iDate);
        String time=c.getString(iTime);
        String alarm=c.getString(iAlarm);
        String alarmDate=c.getString(iAlarmDate);
        String alarmTime=c.getString(iAlarmTime);
        String category=c.getString(iCategory);
        String todoString=c.getString(iTodoString);
        boolean isAlarm=false;
        if(alarm.equals("set")) isAlarm=true;
        ArrayList<TodoListClass> todoList=new Gson().fromJson(todoString,
                new TypeToken<ArrayList<TodoListClass>>() {}.getType());
        c.close();
        return new Notes(notes,heading,id,date,time,isAlarm,alarmDate,alarmTime,category,todoList);
    }


    public Notes getDataAt(String id)
    {
        String columns[]=new String[]{KEY_ID,KEY_NOTES,KEY_HEADING,KEY_DATE,KEY_TIME,KEY_ALARM,KEY_ALARM_DATE,KEY_ALARM_TIME
        ,KEY_CATEGORY,KEY_TODO_STRING};
        Cursor c=ourDataBase.query(DATABASE_TABLE,columns,null,null,null,null,null);
        int iRowId=c.getColumnIndex(KEY_ID);
        int iNotes=c.getColumnIndex(KEY_NOTES);
        int iHeading=c.getColumnIndex(KEY_HEADING);
        int iDate=c.getColumnIndex(KEY_DATE);
        int iTime=c.getColumnIndex(KEY_TIME);
        int iAlarm=c.getColumnIndex(KEY_ALARM);
        int iAlarmDate=c.getColumnIndex(KEY_ALARM_DATE);
        int iAlarmTime=c.getColumnIndex(KEY_ALARM_TIME);
        int iCategory=c.getColumnIndex(KEY_CATEGORY);
        int iTodoString=c.getColumnIndex(KEY_TODO_STRING);
        Notes notes=new Notes("","","","","",false,"","","",null);
        for(c.moveToLast();!c.isBeforeFirst();c.moveToPrevious())
        {
            String id1=c.getString(iRowId);
            if(id1.equals(id))
            {
                String s=c.getString(iNotes);
                String str=c.getString(iHeading);
                String date=c.getString(iDate);
                String time=c.getString(iTime);
                String alarm=c.getString(iAlarm);
                String alarmDate=c.getString(iAlarmDate);
                String alarmTime=c.getString(iAlarmTime);
                String category=c.getString(iCategory);
                String todoString=c.getString(iTodoString);
                boolean isAlarm=false;
                if(alarm.equals("set")) isAlarm=true;
                ArrayList<TodoListClass> todoList=new Gson().fromJson(todoString,
                        new TypeToken<ArrayList<TodoListClass>>() {}.getType());
                notes=new Notes(s,str,id,date,time,isAlarm,alarmDate,alarmTime,category,todoList);
                break;
            }
        }
        return notes;
    }

    public long deleteEntry(String rowId)
    {
        return ourDataBase.delete(DATABASE_TABLE,KEY_ID+"=?",new String[]{rowId});
    }


    public long updateEntry(String rowId,String alarm)
    {
        ContentValues cv=new ContentValues();
        cv.put(KEY_ALARM,alarm);
        return ourDataBase.update(DATABASE_TABLE,cv,KEY_ID+"=?",new String[]{rowId});
    }

    public long updateEntry(String rowId,String notes,String heading,String date,String time,String alarm,String alarmDate,
                            String alarmTime,String cagtegory,String todoString)
    {
        ContentValues cv=new ContentValues();
        cv.put(KEY_NOTES,notes);
        cv.put(KEY_HEADING,heading);
        cv.put(KEY_DATE,date);
        cv.put(KEY_TIME,time);
        cv.put(KEY_ALARM,alarm);
        cv.put(KEY_ALARM_DATE,alarmDate);
        cv.put(KEY_ALARM_TIME,alarmTime);
        cv.put(KEY_CATEGORY,cagtegory);
        cv.put(KEY_TODO_STRING,todoString);
        return ourDataBase.update(DATABASE_TABLE,cv,KEY_ID+"=?",new String[]{rowId});
    }

}
