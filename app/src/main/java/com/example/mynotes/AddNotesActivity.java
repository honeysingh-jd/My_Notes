package com.example.mynotes;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class AddNotesActivity extends AppCompatActivity implements Serializable, TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, TodoAdapter.ItemClicked {
    EditText etNotes;
    int flag;
    EditText etHeading;
    TextView tvDate1;
    TextView tvTime,tvEditTime;
    ImageView ivAdd;
    InputMethodManager inputMethodManager;
    static int f=0;
    Menu menu;
    int hourOfDay,minute,day,month,year;
    boolean isALarmSet;
    Calendar calendar;
    String category;
    RecyclerView rvTodoList;
    TodoAdapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<TodoListClass> todoList;
    MaterialButton btnAddItem;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        TodoAdapter.f=0;

        ivAdd=findViewById(R.id.ivAdd);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        tvEditTime=findViewById(R.id.tvEditTime);
        Calendar calendar1=Calendar.getInstance();
        String time1=DateFormat.getTimeInstance().format(calendar1.getTimeInMillis());
        time1=getTime(time1);
        tvEditTime.setText("Edited  "+time1);



        etNotes=findViewById(R.id.etNotes);


        etHeading=findViewById(R.id.etHeading);

        flag=getIntent().getIntExtra("flag",0);
        category=getIntent().getStringExtra("category");

        // to open automatic keyboard
        etNotes.requestFocus();
        softInputMethod();

        // keyboard open and close listener
        keyboardOpenAndCloseListener();


        setRecyclerView();

        // add todoList..
        if(todoList.size()>0)
        {
            ivAdd.animate().alpha(0.5f).start();
            ivAdd.setEnabled(false);
        }
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todoList.add(new TodoListClass(null,false));
                TodoAdapter.f=1;
                myAdapter.notifyDataSetChanged();
                btnAddItem.setVisibility(View.VISIBLE);
                ivAdd.animate().alpha(0.5f).start();
                ivAdd.setEnabled(false);
            }
        });

        btnAddItem=findViewById(R.id.btnAddItem);
        if(todoList.isEmpty())
        {
            btnAddItem.setVisibility(View.GONE);
        }
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todoList.add(new TodoListClass(null,false));
                TodoAdapter.f=1;
                myAdapter.notifyDataSetChanged();
                ivAdd.animate().alpha(0.5f).start();
                ivAdd.setEnabled(false);
            }
        });

        setFont();

        setFontSize();
    }

    public void setFontSize()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("PREFS",0);
        String fontSize=sharedPreferences.getString("fontSize","medium");
        if(fontSize.equals("small"))
        {
            etNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
            etHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        }
        else if(fontSize.equals("medium"))
        {
            etNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            etHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        }
        else
        {
            etNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
            etHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP,26);
        }
    }

    public void setFont()
    {
        SharedPreferences sharedPreferences1=getSharedPreferences("PREFS",0);
        String bodyFont=sharedPreferences1.getString("bodyFont","convergence");
        if(bodyFont.equals("convergence"))
        {
            Typeface typeface = ResourcesCompat.getFont(this,R.font.convergence);
            etNotes.setTypeface(typeface);
            etHeading.setTypeface(typeface);
            tvEditTime.setTypeface(typeface);
        }
        else if(bodyFont.equals("monospace"))
        {
            etNotes.setTypeface(Typeface.MONOSPACE);
            etHeading.setTypeface(Typeface.MONOSPACE);
            tvEditTime.setTypeface(Typeface.MONOSPACE);
        }
        else
        {
            Typeface typeface = ResourcesCompat.getFont(this,R.font.andada);
            etNotes.setTypeface(typeface);
            etHeading.setTypeface(typeface);
            tvEditTime.setTypeface(typeface);
        }
    }

    public void setRecyclerView()
    {
        rvTodoList=findViewById(R.id.rvTodoList);
        rvTodoList.setHasFixedSize(true);
        rvTodoList.setNestedScrollingEnabled(false);
        layoutManager=new LinearLayoutManager(this);
        rvTodoList.setLayoutManager(layoutManager);
        todoList=new ArrayList<>();
        myAdapter=new TodoAdapter(this,todoList);
        rvTodoList.setAdapter(myAdapter);
    }


    public void keyboardOpenAndCloseListener()
    {
        View view=findViewById(android.R.id.content).getRootView();
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r=new Rect();
                view.getWindowVisibleDisplayFrame(r);
                int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);
                if(f==0)
                {

                }
                else if(f==1)
                {
                    f++;
                }
                else if(f==2)
                {
                    f++;
                }
                else if(f==3)
                {
                    if (heightDiff > 100)
                    {
                        //keyboard is open
                        for(int i=0;i<menu.size();i++)
                        {
                            if(i==0)
                            {
                                menu.getItem(i).setVisible(true);
                            }
                            else
                            {
                                menu.getItem(i).setVisible(false);
                            }
                        }
                    }
                    else
                    {
                        //keyboard is closed
                        for(int i=0;i<menu.size();i++)
                        {
                            if(i==0)
                            {
                                menu.getItem(i).setVisible(false);
                            }
                            else
                            {
                                menu.getItem(i).setVisible(true);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        inputMethodManager=(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        this.menu=menu;
        if(category.equals("hidden"))
        {
            getMenuInflater().inflate(R.menu.addnotesmenu1,menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.anotheractionbar,menu);
        }
        if(f==0)
        {
            f=1;
        }
        return super.onCreateOptionsMenu(menu);
    }

    // to popup keyboard automatically
    public void softInputMethod()
    {
        inputMethodManager=(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager!=null)
        {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.tick)
        {
            View view = this.getCurrentFocus();
            if (view == null) {
                view = new View(this);
            }
            inputMethodManager=(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager.isActive())
            {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        }
        else if(id==R.id.reminder)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(AddNotesActivity.this);
            View view=getLayoutInflater().inflate(R.layout.reminderdialog,null);
            ImageView ivDate=(ImageView)view.findViewById(R.id.ivDate);
            ImageView ivTime=(ImageView)view.findViewById(R.id.ivTime);
            Button btnCancel1=view.findViewById(R.id.btnCancel1);
            Button btnOk1=view.findViewById(R.id.btnOk1);
            tvDate1=(TextView)view.findViewById(R.id.tvDate1);
            tvTime=(TextView)view.findViewById(R.id.tvTime);

            Calendar c1=Calendar.getInstance();
            String date=DateFormat.getDateInstance().format(c1.getTimeInMillis());
            String time=DateFormat.getTimeInstance().format(c1.getTimeInMillis());
            time=getTime(time);
            tvDate1.setText(date);
            tvTime.setText(time);
            hourOfDay=c1.get(Calendar.HOUR_OF_DAY);
            minute=c1.get(Calendar.MINUTE);
            day=c1.get(Calendar.DAY_OF_MONTH);
            month=c1.get(Calendar.MONTH);
            year=c1.get(Calendar.YEAR);

            builder.setView(view);

            AlertDialog dialog=builder.create();
            dialog.setCanceledOnTouchOutside(true);

            Calendar c=Calendar.getInstance();
            String currentTime=DateFormat.getTimeInstance().format(c.getTime());
            currentTime=getTime(currentTime);
            String currentDate=DateFormat.getDateInstance().format(c.getTime());
            tvTime.setText(currentTime);
            tvDate1.setText(currentDate);

            ivDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment datePicker=new DatePickerFragment();
                    datePicker.show(getSupportFragmentManager(),"date picker");
                }
            });

            ivTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment timePicker=new TimePickerFragment();
                    timePicker.show(getSupportFragmentManager(),"time picker");
                }
            });

            btnOk1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar=Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_MONTH,day);
                    calendar.set(Calendar.MONTH,month);
                    calendar.set(Calendar.YEAR,year);
                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    calendar.set(Calendar.MINUTE,minute);
                    calendar.set(Calendar.SECOND,0);
                    isALarmSet=true;
//                    Toast.makeText(AddNotesActivity.this,day+"",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    dialog.dismiss();
                }
            });

            btnCancel1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
        else if(id==R.id.hide)
        {
            category="hidden";
            Toast.makeText(AddNotesActivity.this,"This notes is hidden Successfully",Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.unhide)
        {
            category="public";
            Toast.makeText(AddNotesActivity.this,"Unhidden Successfully",Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.delete)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(AddNotesActivity.this,R.style.my_dialog_theme);
            View view=getLayoutInflater().inflate(R.layout.custom_dialog1,null);
            Button btnCancel=(Button)view.findViewById(R.id.btnCancel);
            Button btnDelete=(Button)view.findViewById(R.id.btnDelete);
            builder.setView(view);

            AlertDialog dialog=builder.create();
            dialog.setCanceledOnTouchOutside(true);
            Window window=dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etHeading.setText(null);
                    etNotes.setText(null);
                    dialog.dismiss();
                    AddNotesActivity.this.finish();
                    Toast.makeText(AddNotesActivity.this,"Deleted successfully",Toast.LENGTH_SHORT).show();
                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
        else
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        String str=etHeading.getText().toString();
        String s=etNotes.getText().toString()+"";
        String alarmDate=" ",alarmTime=" ";
        if(calendar!=null)
        {
            alarmDate=DateFormat.getDateInstance().format(calendar.getTimeInMillis());
            alarmTime=DateFormat.getTimeInstance().format(calendar.getTimeInMillis());
        }
        Intent intent=new Intent();
        intent.putExtra("notes",s);
        intent.putExtra("heading",str);
        intent.putExtra("flag",flag);
        intent.putExtra("isAlarmSet",isALarmSet);
        intent.putExtra("calendar",calendar);
        intent.putExtra("alarmDate",alarmDate);
        intent.putExtra("alarmTime",alarmTime);
        intent.putExtra("category",category);
        intent.putExtra("todoList",todoList);
        setResult(RESULT_OK,intent);

        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager=(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isActive())
        {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
        AddNotesActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        f=0;
        super.onDestroy();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        this.hourOfDay=hourOfDay;
        this.minute=minute;
        Calendar c=Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
        c.set(Calendar.MINUTE,minute);

        String time=DateFormat.getTimeInstance().format(c.getTime());
        time=getTime(time);
        tvTime.setText(time);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        this.year=year;
        this.month=month;
        this.day=dayOfMonth;
        Calendar c=Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        String date=DateFormat.getDateInstance().format(c.getTime());
        tvDate1.setText(date);
    }

    public String getTime(String currentTime)
    {
        int c=0;
        int in=0;
        for(int i=0;i<currentTime.length();i++)
        {
            if(currentTime.charAt(i)==':')
            {
                c++;
            }
            if(c==2)
            {
                in=i;
                break;
            }
        }
        String s1=currentTime.substring(0,in);
        String s2=currentTime.substring(in+3);
        return s1+s2;
    }

    @Override
    public void onCheckedClick(int index) {
        Boolean pre=todoList.get(index).isCancelled;
        if(pre)
        {
            pre=false;
        }
        else
        {
            pre=true;
        }
        todoList.get(index).setCancelled(pre);
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void listText(int index, String text) {
        todoList.get(index).setTodo(text);
    }

    @Override
    public void clearItem(int index) {
        todoList.remove(index);
        TodoAdapter.f=1;
        myAdapter.notifyDataSetChanged();
        if(todoList.isEmpty())
        {
            btnAddItem.setVisibility(View.GONE);
            ivAdd.animate().alpha(1).start();
            ivAdd.setEnabled(true);
        }
        else
        {
            btnAddItem.setVisibility(View.VISIBLE);
            ivAdd.animate().alpha(0.5f).start();
            ivAdd.setEnabled(false);
        }
    }

}