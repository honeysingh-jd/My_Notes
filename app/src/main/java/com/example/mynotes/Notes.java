package com.example.mynotes;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class Notes implements Serializable {
    private String notes;
    private String id;
    private String heading;
    private String date;
    private String time;
    private boolean isAlarmSet;
    private String alarmDate;
    private String alarmTime;
    private String category;
    private ArrayList<TodoListClass> todoList;

    public Notes(String notes,String heading,String id,String date,String time, boolean isAlarmSet, String alarmDate,
                 String alarmTime,String category,ArrayList<TodoListClass> todoList) {
        this.notes = notes;
        this.heading=heading;
        this.id=id;
        this.date=date;
        this.time=time;
        this.isAlarmSet=isAlarmSet;
        this.alarmDate=alarmDate;
        this.alarmTime=alarmTime;
        this.category=category;
        this.todoList=todoList;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAlarmSet() {
        return isAlarmSet;
    }

    public void setAlarmSet(boolean alarmSet) {
        isAlarmSet = alarmSet;
    }

    public String getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(String alarmDate) {
        this.alarmDate = alarmDate;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<TodoListClass> getTodoList() {
        return todoList;
    }

    public void setTodoList(ArrayList<TodoListClass> todoList) {
        this.todoList = todoList;
    }
}
