package com.example.mynotes;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ShowNotesActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener ,
        DatePickerDialog.OnDateSetListener , TodoAdapter.ItemClicked {
    EditText etNotes1;
    int flag;
    EditText etHeading1;
    InputMethodManager inputMethodManager;
    static int f=0;
    Menu menu;
    static String id;
    int index;
    static boolean isKeyPressed=false;
    TextView tvDate1;
    TextView tvTime,tvEditTime1;
    int hourOfDay,minute,day,month,year;
    boolean isALarmSet;
    Calendar calendar;
    MaterialButton btnCancelAlarm,btnAddItem;
    String category;
    String alarmDate,alarmTime;
    ImageView ivAdd1;
    ArrayList<TodoListClass> todoList;
    RecyclerView rvTodoList;
    TodoAdapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    int recyclerview_set=0;
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notes);

        TodoAdapter.f=0;

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        tvEditTime1=findViewById(R.id.tvEditTime1);

        etNotes1=findViewById(R.id.etNotes1);

        etHeading1=findViewById(R.id.etHeading1);

        btnCancelAlarm=findViewById(R.id.btnCancelAlarm);

        isKeyPressed=false;

        TextWatcher textWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                isKeyPressed=true;
                Calendar c2=Calendar.getInstance();
                String time=DateFormat.getTimeInstance().format(c2.getTimeInMillis());
                time=getTime(time);
                tvEditTime1.setText("Edited  "+time);
            }
        };


        // getting data through intent
        id=getIntent().getStringExtra("id");
        index=getIntent().getIntExtra("index",0);
        flag=getIntent().getIntExtra("flag",0);

        btnCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ShowNotesActivity.this);
                View view=getLayoutInflater().inflate(R.layout.custom_dialog1,null);
                Button btnCancel=(Button)view.findViewById(R.id.btnCancel);
                Button btnDelete=(Button)view.findViewById(R.id.btnDelete);
                TextView tvHeading1=view.findViewById(R.id.tvHeading1);
                TextView tvTitle=view.findViewById(R.id.tvTitle);
                tvHeading1.setText("Delete Alarm");
                tvTitle.setText("Delete this alarm?");
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

                        btnCancelAlarm.setVisibility(View.GONE);
                        isALarmSet=false;
                        isKeyPressed=true;
                        SetAlarm instance=new SetAlarm(calendar,ShowNotesActivity.this,id,index,"");
                        instance.cancelAlarm();
                        Toast.makeText(ShowNotesActivity.this,"Deleted successfully",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        try
        {
            NotesDB notesDB=new NotesDB(this);
            notesDB.open();
            Notes notes=notesDB.getDataAt(id);
            alarmDate=notes.getAlarmDate();
            alarmTime=notes.getAlarmTime();
            isALarmSet=notes.isAlarmSet();
            category=notes.getCategory();
            todoList=notes.getTodoList();
            recyclerview_set=todoList.size();

            // edit time
            String editDate=notes.getDate();
            editDate=getDateStamp(editDate,notes.getTime());
            tvEditTime1.setText("Edited  "+editDate);
            
            
            if(category.equals("hidden") && flag==2)
            {
                SharedPreferences sharedPreferences=getSharedPreferences("PREFS",0);
                String password=sharedPreferences.getString("password","0");
                ActivityResultLauncher<Intent> launcher=registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {

                            }
                        });
                if(password.equals("0"))
                {
                    Intent intent=new Intent(ShowNotesActivity.this,CreatePassword.class);
                    intent.putExtra("flag",flag);
                    launcher.launch(intent);
                }
                else
                {
                    Intent intent=new Intent(ShowNotesActivity.this,InputPassword.class);
                    intent.putExtra("flag",flag);
                    launcher.launch(intent);
                }
            }

            if(isALarmSet)
            {
                String alarmDate1=getDate(alarmDate);
                String alarmTime1=getTime(alarmTime);
                String dateTime=alarmDate1+", "+alarmTime1;
                btnCancelAlarm.setText(dateTime);
                btnCancelAlarm.setVisibility(View.VISIBLE);
            }
            else
            {
                btnCancelAlarm.setVisibility(View.GONE);
            }
            String s=notes.getNotes();
            String str=notes.getHeading();
            etNotes1.setText(s);
            etHeading1.setText(str);
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }


        etNotes1.addTextChangedListener(textWatcher);
        etHeading1.addTextChangedListener(textWatcher);

        // keyboard open and close listener
        keyboardOpenAndCloseListener();

        // add properties button
        ivAdd1=findViewById(R.id.ivAdd1);
        if(todoList.size()>0)
        {
            ivAdd1.animate().alpha(0.5f).start();
            ivAdd1.setEnabled(false);
        }
        ivAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todoList.add(new TodoListClass(null,false));
                TodoAdapter.f=1;
                myAdapter.notifyDataSetChanged();
                btnAddItem.setVisibility(View.VISIBLE);
                ivAdd1.animate().alpha(0.5f).start();
                ivAdd1.setEnabled(false);
                isKeyPressed=true;
            }
        });

        rvTodoList=findViewById(R.id.rvTodoList);
        rvTodoList.setHasFixedSize(true);
        rvTodoList.setNestedScrollingEnabled(false);
        layoutManager=new LinearLayoutManager(this);
        rvTodoList.setLayoutManager(layoutManager);
        myAdapter=new TodoAdapter(this,todoList);
        rvTodoList.setAdapter(myAdapter);

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
                ivAdd1.animate().alpha(0.5f).start();
                ivAdd1.setEnabled(false);
                isKeyPressed=true;
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
            etNotes1.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
            etHeading1.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        }
        else if(fontSize.equals("medium"))
        {
            etNotes1.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            etHeading1.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        }
        else
        {
            etNotes1.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
            etHeading1.setTextSize(TypedValue.COMPLEX_UNIT_SP,26);
        }
    }

    public void setFont()
    {
        SharedPreferences sharedPreferences1=getSharedPreferences("PREFS",0);
        String bodyFont=sharedPreferences1.getString("bodyFont","convergence");
        if(bodyFont.equals("convergence"))
        {
            Typeface typeface = ResourcesCompat.getFont(this,R.font.convergence);
            etNotes1.setTypeface(typeface);
            etHeading1.setTypeface(typeface);
            tvEditTime1.setTypeface(typeface);
        }
        else if(bodyFont.equals("monospace"))
        {
            etNotes1.setTypeface(Typeface.MONOSPACE);
            etHeading1.setTypeface(Typeface.MONOSPACE);
            tvEditTime1.setTypeface(Typeface.MONOSPACE);
        }
        else
        {
            Typeface typeface = ResourcesCompat.getFont(this,R.font.andada);
            etNotes1.setTypeface(typeface);
            etHeading1.setTypeface(typeface);
            tvEditTime1.setTypeface(typeface);
        }
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
        this.menu=menu;
        if(category.equals("hidden"))
        {
            getMenuInflater().inflate(R.menu.shownotesmenu1,menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.tickbutton,menu);
        }
        if(f==0)
        {
            f=1;
        }
        return super.onCreateOptionsMenu(menu);
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
            AlertDialog.Builder builder=new AlertDialog.Builder(ShowNotesActivity.this);
            View view=getLayoutInflater().inflate(R.layout.reminderdialog,null);
            ImageView ivDate=(ImageView)view.findViewById(R.id.ivDate);
            ImageView ivTime=(ImageView)view.findViewById(R.id.ivTime);
            Button btnCancel1=view.findViewById(R.id.btnCancel1);
            Button btnOk1=view.findViewById(R.id.btnOk1);
            tvDate1=(TextView)view.findViewById(R.id.tvDate1);
            tvTime=(TextView)view.findViewById(R.id.tvTime);

            Calendar c1=Calendar.getInstance();
            String date= DateFormat.getDateInstance().format(c1.getTimeInMillis());
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
                    isKeyPressed=true;
                    Toast.makeText(ShowNotesActivity.this,"Alarm is set",Toast.LENGTH_SHORT).show();
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
            isKeyPressed=true;
            Toast.makeText(ShowNotesActivity.this,"This notes is hidden Successfully",Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        else if(id==R.id.unhide)
        {
            category="public";
            isKeyPressed=true;
            onBackPressed();
        }
        else if(id==R.id.delete)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(ShowNotesActivity.this,R.style.my_dialog_theme);
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
                    etHeading1.setText(null);
                    etNotes1.setText(null);
                    dialog.dismiss();
                    onBackPressed();
                    ShowNotesActivity.this.finish();
                    Toast.makeText(ShowNotesActivity.this,"Deleted successfully",Toast.LENGTH_SHORT).show();
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

        String str=etHeading1.getText().toString();
        String s=etNotes1.getText().toString()+"";
        if(calendar!=null)
        {
            alarmDate=DateFormat.getDateInstance().format(calendar.getTimeInMillis());
            alarmTime=DateFormat.getTimeInstance().format(calendar.getTimeInMillis());
        }
        if(flag==2)
        {
            Intent intent=new Intent(ShowNotesActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("notes",s);
            intent.putExtra("heading",str);
            intent.putExtra("flag",flag);
            intent.putExtra("id",id);
            intent.putExtra("index",index);
            intent.putExtra("isKeyPressed",isKeyPressed);
            intent.putExtra("isAlarmSet",isALarmSet);
            intent.putExtra("calendar",calendar);
            intent.putExtra("alarmDate",alarmDate);
            intent.putExtra("alarmTime",alarmTime);
            intent.putExtra("category",category);
            intent.putExtra("todoList",todoList);
            startActivity(intent);
            finish();
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
        else
        {
            Intent intent=new Intent();
            intent.putExtra("notes",s);
            intent.putExtra("heading",str);
            intent.putExtra("flag",flag);
            intent.putExtra("id",id);
            intent.putExtra("index",index);
            intent.putExtra("isKeyPressed",isKeyPressed);
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
            ShowNotesActivity.this.finish();
        }

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
        if(currentTime==null)
        {
            return new String("");
        }
        else
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
    }

    public String getDate(String currentDate)
    {
        String s="";
        for(int i=0;i<currentDate.length();i++)
        {
            if(currentDate.charAt(i)==',')
            {
                s=currentDate.substring(0,i);
                break;
            }
        }
        return s;
    }

    public String getDateStamp(String date,String time)
    {
        String monthArr[]={"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct",
                "Nov","Dec"};
        int year=Integer.parseInt(date.substring(6));
        String mon=monthArr[Integer.parseInt(date.substring(3,5))];
        int day=Integer.parseInt(date.substring(0,2));
        Calendar calendar1=Calendar.getInstance();
        int cyear=calendar1.get(Calendar.YEAR);
        int m=calendar1.get(Calendar.MONTH);
        String cmon=monthArr[m+1];
        int cday=calendar1.get(Calendar.DAY_OF_MONTH);
        int hour=Integer.parseInt(time.substring(0,2));
        String min=time.substring(3,5);
        String period="";
        if(hour<12)
        {
            hour=hour%12;
            if(hour==0) hour=12;
            period="AM";
        }
        else
        {
            hour-=12;
            if(hour==0) hour=12;
            period="PM";
        }
        String textTime=hour+":"+min+" "+period;
        String d="";
        if(cyear>year)
        {
            d=mon+" "+day+", "+year;
        }
        else if(cyear==year && cmon.equals(mon) && cday==day)
        {
            d=textTime;
        }
        else
        {
            d=mon+" "+day;
        }
        return d;
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
        isKeyPressed=true;
    }

    @Override
    public void listText(int index, String text) {
        todoList.get(index).setTodo(text);
        if(recyclerview_set>0)
        {
            recyclerview_set--;
        }
        else
        {
            isKeyPressed=true;
        }
//        Toast.makeText(this,"checked",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clearItem(int index) {
        todoList.remove(index);
        TodoAdapter.f=1;
        myAdapter.notifyDataSetChanged();
        if(todoList.isEmpty())
        {
            btnAddItem.setVisibility(View.GONE);
            ivAdd1.animate().alpha(1).start();
            ivAdd1.setEnabled(true);
        }
        else
        {
            btnAddItem.setVisibility(View.VISIBLE);
            ivAdd1.animate().alpha(0.5f).start();
            ivAdd1.setEnabled(false);
        }
        isKeyPressed=true;
    }
}