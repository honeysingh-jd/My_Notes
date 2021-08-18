package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    RadioButton rbConvergence,rbMonospace,rbAndada,rbSmall,rbMedium,rbLarge;
    RadioGroup radioGroup1,radioGroup2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        rbConvergence=findViewById(R.id.rbConvergence);
        rbMonospace=findViewById(R.id.rbMonospace);
        rbAndada=findViewById(R.id.rbAndada);
        rbSmall=findViewById(R.id.rbSmall);
        rbMedium=findViewById(R.id.rbMedium);
        rbLarge=findViewById(R.id.rbLarge);

        SharedPreferences sharedPreferences1=getSharedPreferences("PREFS",0);
        String bodyFont=sharedPreferences1.getString("bodyFont","convergence");
        radioGroup1=findViewById(R.id.radioGroup1);
        if(bodyFont.equals("convergence"))
        {
            radioGroup1.check(R.id.rbConvergence);
            rbConvergence.setButtonDrawable(R.drawable.check_ok);
        }
        else if(bodyFont.equals("monospace"))
        {
            radioGroup1.check(R.id.rbMonospace);
            rbMonospace.setButtonDrawable(R.drawable.check_ok);
        }
        else
        {
            radioGroup1.check(R.id.rbAndada);
            rbAndada.setButtonDrawable(R.drawable.check_ok);
        }
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.rbConvergence)
                {
                    SharedPreferences sharedPreferences=getSharedPreferences("PREFS",0);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("bodyFont","convergence");
                    editor.apply();
                    rbConvergence.setButtonDrawable(R.drawable.check_ok);
                    rbMonospace.setButtonDrawable(R.drawable.check);
                    rbAndada.setButtonDrawable(R.drawable.check);
                }
                else if(checkedId==R.id.rbMonospace)
                {
                    SharedPreferences sharedPreferences=getSharedPreferences("PREFS",0);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("bodyFont","monospace");
                    editor.apply();
                    rbMonospace.setButtonDrawable(R.drawable.check_ok);
                    rbConvergence.setButtonDrawable(R.drawable.check);
                    rbAndada.setButtonDrawable(R.drawable.check);
                }
                else if(checkedId==R.id.rbAndada)
                {
                    SharedPreferences sharedPreferences=getSharedPreferences("PREFS",0);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("bodyFont","andada");
                    editor.apply();
                    rbAndada.setButtonDrawable(R.drawable.check_ok);
                    rbMonospace.setButtonDrawable(R.drawable.check);
                    rbConvergence.setButtonDrawable(R.drawable.check);
                }
            }
        });

        radioGroup2=findViewById(R.id.radioGroup2);
        SharedPreferences sharedPreferences2=getSharedPreferences("PREFS",0);
        String fontSize=sharedPreferences2.getString("fontSize","medium");
        if(fontSize.equals("small"))
        {
            radioGroup2.check(R.id.rbSmall);
            rbSmall.setButtonDrawable(R.drawable.check_ok);
        }
        else if(fontSize.equals("medium"))
        {
            radioGroup2.check(R.id.rbMedium);
            rbMedium.setButtonDrawable(R.drawable.check_ok);
        }
        else
        {
            radioGroup2.check(R.id.rbLarge);
            rbLarge.setButtonDrawable(R.drawable.check_ok);
        }
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.rbSmall)
                {
                    rbSmall.setButtonDrawable(R.drawable.check_ok);
                    rbMedium.setButtonDrawable(R.drawable.check);
                    rbLarge.setButtonDrawable(R.drawable.check);
                    SharedPreferences sharedPreferences=getSharedPreferences("PREFS",0);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("fontSize","small");
                    editor.apply();
                }
                else if(checkedId==R.id.rbMedium)
                {
                    rbMedium.setButtonDrawable(R.drawable.check_ok);
                    rbSmall.setButtonDrawable(R.drawable.check);
                    rbLarge.setButtonDrawable(R.drawable.check);
                    SharedPreferences sharedPreferences=getSharedPreferences("PREFS",0);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("fontSize","medium");
                    editor.apply();
                }
                else if(checkedId==R.id.rbLarge)
                {
                    rbLarge.setButtonDrawable(R.drawable.check_ok);
                    rbSmall.setButtonDrawable(R.drawable.check);
                    rbMedium.setButtonDrawable(R.drawable.check);
                    SharedPreferences sharedPreferences=getSharedPreferences("PREFS",0);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("fontSize","large");
                    editor.apply();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()>0)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        this.finish();
    }
}