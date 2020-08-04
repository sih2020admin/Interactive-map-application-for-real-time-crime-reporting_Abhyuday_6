package com.example.hackathonoriginal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.hackathonoriginal.Databases.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Login extends AppCompatActivity {

    TextInputEditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

loadLocale();
        phone = findViewById(R.id.emailOrPhone);
        String phoneS="";
        phoneS=getIntent().getStringExtra("value");
        phone.setText(phoneS);
    }

    public void CallLoginScreen(View view) {


        if (!validatePhone()) {
            return;
        } else {

            Intent intent = new Intent(getApplicationContext(), LoginOTP.class);

            final String fullPhoneNumber = "+91" + phone.getText().toString().trim();

            intent.putExtra("fullPhoneNumber", fullPhoneNumber);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<>(findViewById(R.id.next), "nextButtonL");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
        }

    }

    private boolean validatePhone() {
        String val = phone.getText().toString().trim();


        if (val.isEmpty()) {
            phone.setError("Phone Number is Compulsory..");
            return false;
        } else if (val.length() != 10) {
            phone.setError("Phone Number should be of 10 digits.");
            return false;
        } else {
            phone.setError(null);
            return true;
        }
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    private void setLocale(String lang){
        Locale locale=new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SessionManager sessionManager=new SessionManager(Login.this);
        sessionManager.setLanguage(lang);
    }

    public void loadLocale(){
        SessionManager sessionManager=new SessionManager(Login.this);
        setLocale(sessionManager.getLanguage());
    }
}


