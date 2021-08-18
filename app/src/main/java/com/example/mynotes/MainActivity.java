package com.example.mynotes;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NotesAdapter.ItemClicked , Serializable {

    RecyclerView rvList;
    RecyclerView.LayoutManager layoutManager;
    public static NotesAdapter myAdapter;
    public static ArrayList<Notes> listNotes;
    ImageView floatingActionButton;
    ActivityResultLauncher<Intent> launcher;
    static int layout=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SharedPreferences sharedPreferences=getSharedPreferences("PREFS",0);
//        SharedPreferences.Editor editor=sharedPreferences.edit();
//        editor.putString("fontSize","30");
//        editor.apply();

        setRecyclerView();

        addNewNotes();

        setFloatingActionButton();

        layout=0;

        // when main activity is reached through ShowNotesActivity in case of alarm
        Intent received=getIntent();
        if(received!=null)
        {
            int flag=received.getIntExtra("flag",0);
            if(flag==2)
            {
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SaveDataAndSetRecyclerView saveData=new SaveDataAndSetRecyclerView(MainActivity.this,
                                received,listNotes,myAdapter,"public");
                        saveData.saveDataAndRecyclerView();
                    }
                },400);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_actionbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.hiddenNotes)
        {
            SharedPreferences sharedPreferences=getSharedPreferences("PREFS",0);
            String password=sharedPreferences.getString("password","0");
            if(password.equals("0"))
            {
                Intent intent=new Intent(MainActivity.this,CreatePassword.class);
                startActivity(intent);
            }
            else
            {
                Intent intent=new Intent(MainActivity.this,InputPassword.class);
                startActivity(intent);
            }
        }
        else if(id==R.id.settings)
        {
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            this.finish();
        }
        else if(id==R.id.changeLayout)
        {
            if(layout==0)
            {
                layoutManager=new LinearLayoutManager(this);
                rvList.setLayoutManager(layoutManager);
                item.setIcon(R.drawable.ic_baseline_grid_view_24);
                layout=1;
            }
            else
            {
                layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                rvList.setLayoutManager(layoutManager);
                item.setIcon(R.drawable.list_icon1);
                layout=0;
            }
        }
        else if(id==R.id.searchNotes)
        {
            SearchView searchView= (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    myAdapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    // method to set the functionality of floatingActionButton
    public void setFloatingActionButton()
    {
        floatingActionButton=findViewById(R.id.fabNotes);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,AddNotesActivity.class);
                intent.putExtra("flag",0);
                intent.putExtra("category","public");
                launcher.launch(intent);
            }
        });
    }

    // method to get data from AddNotesActivity and add it to the recyclerview
    public void addNewNotes()
    {
        launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result)
                    {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent received = result.getData();
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SaveDataAndSetRecyclerView saveData=new SaveDataAndSetRecyclerView(MainActivity.this,
                                            received,listNotes,myAdapter,"public");
                                    saveData.saveDataAndRecyclerView();
                                }
                            },400);
                        }
                    }
        });
    }

    // method to create recyclerview and fetch the data from database
    public void setRecyclerView()
    {
        rvList=findViewById(R.id.rvList);
        rvList.setHasFixedSize(true);

        StaggeredGridLayoutManager manager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        layoutManager=manager;
        rvList.setLayoutManager(layoutManager);

        listNotes=new ArrayList<>();
        try
        {
            NotesDB notesDB=new NotesDB(this);
            notesDB.open();
            listNotes=notesDB.getData();
            notesDB.close();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        ArrayList<Notes> list=new ArrayList<>();
        for(int i=0;i<listNotes.size();i++)
        {
            if(listNotes.get(i).getCategory().equals("public"))
            {
                list.add(listNotes.get(i));
            }
        }
        SortingClass sortingClass=new SortingClass();
        sortingClass.sort(list);
        listNotes=new ArrayList<>(list);
        for(int i=0;i<listNotes.size();i++)
        {
            Notes notes=listNotes.get(i);
            String s=notes.getNotes();
            String str=notes.getHeading();
            s=SaveDataAndSetRecyclerView.trimTheString(s,0);
            str=SaveDataAndSetRecyclerView.trimTheString(str,1);
            notes.setNotes(s);
            notes.setHeading(str);
        }
        myAdapter=new NotesAdapter(listNotes,this);
        rvList.setAdapter(myAdapter);
    }

    // when list items are clicked
    @Override
    public void onItemClicked(int index) {
        Intent intent=new Intent(this,ShowNotesActivity.class);
        String id=listNotes.get(index).getId();
        intent.putExtra("id",id);
        intent.putExtra("flag",1);
        intent.putExtra("index",index);
        launcher.launch(intent);
    }

    @Override
    public void onLongItemClicked(int index) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.my_dialog_theme);
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
                try
                {
                    if(listNotes.get(index).isAlarmSet())
                    {
                        int requestCode=Integer.parseInt(listNotes.get(index).getId());
                        AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        Intent intent=new Intent(MainActivity.this,AlertReceiver.class);
                        PendingIntent pendingIntent=PendingIntent.getBroadcast(MainActivity.this,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pendingIntent);
                    }
                    NotesDB notesDB=new NotesDB(MainActivity.this);
                    notesDB.open();
                    notesDB.deleteEntry(listNotes.get(index).getId());
                    notesDB.close();
                    listNotes.remove(index);
                    myAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this,"deleted successfully!",Toast.LENGTH_SHORT).show();
                }
                catch (SQLiteException e)
                {
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

}