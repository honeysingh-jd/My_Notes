package com.example.mynotes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import java.util.List;

public class InputPassword extends AppCompatActivity {

    TextView tvUnlock;
    PatternLockView patternLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_password);

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
                        startActivity(new Intent(InputPassword.this,MainActivity1.class));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.input_password_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.changePassword)
        {
            startActivity(new Intent(InputPassword.this,InputPassword2.class));
        }
        else
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