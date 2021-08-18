package com.example.mynotes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PatternMatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.google.android.material.button.MaterialButton;
import java.util.List;


public class CreatePassword extends AppCompatActivity {

    TextView tvText;
    PatternLockView patternLockView;
    TextView tvUnlock;
    MaterialButton btnClear,btnNext;
    int flag=0;
    String s1="";
    String s2="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        flag=0;

        tvText=findViewById(R.id.tvText);
        patternLockView=findViewById(R.id.patternLockView);
        tvUnlock=findViewById(R.id.tvUnlock);
        btnClear=findViewById(R.id.btnClear);
        btnNext=findViewById(R.id.btnNext);

        btnClear.animate().alpha(0.3f).start();
        btnClear.setEnabled(false);
        btnNext.animate().alpha(0.3f).start();
        btnNext.setEnabled(false);

        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
                tvUnlock.setText("Release finger when done");
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                String p=PatternLockUtils.patternToString(patternLockView,pattern);
                if(flag==0)
                {
                    s1=p;
                    if(s1.length()<4)
                    {
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                        tvUnlock.setText("Connect at least 4 dots. Try again.");
                    }
                    else
                    {
                        flag++;
                        btnClear.setEnabled(true);
                        btnClear.animate().alpha(1.0f).start();
                        tvUnlock.setText("Pattern recorded");
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tvUnlock.setText("Draw pattern again to confirm:");
                            }
                        },500);
                    }
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            patternLockView.clearPattern();
                        }
                    },1000);
                }
                else if(flag==1)
                {
                    s2=p;
                    if(!s1.equals(s2))
                    {
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                        tvUnlock.setText("Try again:");
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                patternLockView.clearPattern();
                            }
                        },1000);
                    }
                    else
                    {
                        tvUnlock.setText("Your new unlock pattern:");
                        btnNext.animate().alpha(1.0f).start();
                        btnNext.setEnabled(true);
                    }
                }

            }

            @Override
            public void onCleared() {

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=0;
                patternLockView.clearPattern();
                tvUnlock.setText("Draw an unlock pattern:");
                btnNext.animate().alpha(0.3f).start();
                btnNext.setEnabled(false);
                btnClear.animate().alpha(0.3f).start();
                btnClear.setEnabled(false);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=getSharedPreferences("PREFS",0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("password",s1);
                editor.apply();
                int flag=getIntent().getIntExtra("flag",0);
                if(flag==2)
                {
                    onBackPressed();
                }
                else
                {
                    Intent intent=new Intent(CreatePassword.this,MainActivity1.class);
                    startActivity(intent);
                }
                finish();
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
    }
}