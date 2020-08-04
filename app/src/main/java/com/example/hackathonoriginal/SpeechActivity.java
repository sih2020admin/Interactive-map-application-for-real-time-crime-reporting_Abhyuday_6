package com.example.hackathonoriginal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackathonoriginal.Databases.SessionManager;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechActivity extends AppCompatActivity {

    TextView speechText;

    double latitude = 0;
    double longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        speechText = findViewById(R.id.speech);

        SessionManager sessionManager = new SessionManager(SpeechActivity.this);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {


                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(SpeechActivity.this, "Sorry Your device is incompatible", Toast.LENGTH_SHORT).show();
                }
            }
        }, 500);

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        Location location;
        if (isConnected(getApplicationContext())) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final SessionManager sessionManager = new SessionManager(SpeechActivity.this);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //Toast.makeText(this, result.get(0), Toast.LENGTH_SHORT).show();
                    //speechText.setText(result.get(0));

                    final String recognize = speechText.getText().toString().toLowerCase();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (result.get(0).contains("emergency") || result.get(0).contains("help") || result.get(0).contains("save") || result.get(0).contains("i am in trouble")) {
                                startActivity(new Intent(SpeechActivity.this, MapsActivity3.class));
                                finish();

                            } else if (result.get(0).contains("call close person 1") || result.get(0).contains("call close person one") || result.get(0).contains("call close person") || result.get(0).contains("call friend")) {
                                String str=sessionManager.getTuser1().replace("+91","");
                                Uri u = Uri.parse("tel:"+str);
                                Intent i = new Intent(Intent.ACTION_CALL, u);
                                try {
                                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        Toast.makeText(SpeechActivity.this, "Calling permission is not Granted..", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    startActivity(i);
                                    finish();
                                } catch (Exception e) {

                                }

                            } else if (result.get(0).contains("call close person 2") || result.get(0).contains("call close person two")) {
                                String str=sessionManager.getTuser2().replace("+91","");
                                Uri u = Uri.parse("tel:"+str);
                                Intent i = new Intent(Intent.ACTION_CALL, u);
                                try {
                                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        Toast.makeText(SpeechActivity.this, "Calling permission is not Granted..", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    startActivity(i);
                                    finish();
                                } catch (Exception e) {

                                }
                            } else if (result.get(0).contains("call police") || result.get(0).contains("call close police") || result.get(0).contains("call trusted police") || result.get(0).contains("call known police")) {
                                if (!sessionManager.getTPolice().isEmpty()) {
                                    String str=sessionManager.getTPolice().replace("+91","");
                                    Uri u = Uri.parse("tel:"+str);
                                    Intent i = new Intent(Intent.ACTION_CALL, u);
                                    try {
                                        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            Toast.makeText(SpeechActivity.this, "Calling permission is not Granted..", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        startActivity(i);
                                        finish();
                                    } catch (Exception e) {

                                    }
                                } else {
                                    Toast.makeText(SpeechActivity.this, "Please Add Known Police in Close People Tab", Toast.LENGTH_SHORT).show();
                                }
                            } else if (result.get(0).contains("pick me here") || result.get(0).contains("pick me") || result.get(0).contains("i am here") || result.get(0).contains("here is me") || result.get(0).contains("i am here")) {
                                SmsManager smsManager = SmsManager.getDefault();
                                String user11 = sessionManager.getTuser1();
                                String user22 = sessionManager.getTuser2();
                                String message = "Pick Me\n" + sessionManager.getFullName() + "\n" + "My Location" + "\nhttps://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
                                smsManager.sendTextMessage(user11, null, message, null, null);
                                smsManager.sendTextMessage(user22, null, message, null, null);
                                Toast.makeText(SpeechActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SpeechActivity.this, DrawerActivity.class));

                            } else if (result.get(0).contains("home") || result.get(0).contains("home page") || result.get(0).contains("open app")) {
                                startActivity(new Intent(SpeechActivity.this, DrawerActivity.class));
                            }

                            else if (recognize.contains("logout me") || recognize.contains("log me out") || recognize.contains("log out")) {
                                sessionManager.remove();
                                startActivity(new Intent(SpeechActivity.this, Login.class));
                            }
                            else {
                                Toast.makeText(SpeechActivity.this, "Sorry, We can't perform this", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 500);
                    break;

                }
        }
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting() || (wifi != null && wifi.isConnectedOrConnecting())))
                return true;
            else
                return false;
        } else
            return false;
    }


    public void onBackPressed() {

        startActivity(new Intent(SpeechActivity.this,DrawerActivity.class));
        finish();

    }

}
