package com.example.mynotes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class InputPassword2 extends AppCompatActivity {

    TextView tvUnlock;
    PatternLockView patternLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_password2);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences=getSharedPreferences("PREFS",0);
        String password=sharedPreferences.getString("password","0");

        tvUnlock=findViewById(R.id.tvUnlock1);
        patternLockView=findViewById(R.id.patternLockView1);

        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                String s= PatternLockUtils.patternToString(patternLockView,pattern);
                if(s.equals(password))
                {
                    int flag=getIntent().getIntExtra("flag",0);
                    if(flag==2)
                    {
                        onBackPressed();
                    }
                    else
                    {
                        startActivity(new Intent(InputPassword2.this,CreatePassword.class));
                    }
                    finish();
                }
                else
                {
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    tvUnlock.setText(null);
                    Handler handler1=new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvUnlock.setText("Try again:");
                        }
                    },200);
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            patternLockView.clearPattern();
                        }
                    },1000);
                }
            }

            @Override
            public void onCleared() {

            }
        });
    }
}