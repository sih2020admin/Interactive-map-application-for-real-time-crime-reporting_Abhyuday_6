package com.example.hackathonoriginal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hackathonoriginal.Databases.SessionManager;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Animation topAnim, botAnim;
    ImageView logoImg;
    TextView name;
    private static int SPLASH_SCREEN=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loadLocale();
        //Load Animations
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_anim);
        botAnim=AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        final SessionManager sessionManager=new SessionManager(MainActivity.this);
        //Mapping

        logoImg=findViewById(R.id.imageLogo);
        name=findViewById(R.id.appName);
        logoImg.setAnimation(topAnim);
        name.setAnimation(botAnim);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sessionManager.getMobile()!="") {
                    startService(new Intent(getApplicationContext(), LockService.class));
                    Intent intent = new Intent(MainActivity.this, DrawerActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        },SPLASH_SCREEN);

    }
    private void setLocale(String lang){
        Locale locale=new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SessionManager sessionManager=new SessionManager(MainActivity.this);
        sessionManager.setLanguage(lang);
    }

    public void loadLocale(){
        SessionManager sessionManager=new SessionManager(MainActivity.this);
        setLocale(sessionManager.getLanguage());
    }
}
