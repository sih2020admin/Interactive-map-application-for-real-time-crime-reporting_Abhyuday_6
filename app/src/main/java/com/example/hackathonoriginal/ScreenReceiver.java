package com.example.hackathonoriginal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;



public class ScreenReceiver extends BroadcastReceiver {

    public static boolean wasScreenOn = true;
    int doubleBackToExitPressedOnce = 1;

    private int countPowerOff = 0;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.e("LOB", "onReceive");

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            countPowerOff++;
            doubleBackToExitPressedOnce++;
            wasScreenOn = false;
            Log.e("LOB", "wasScreenOn" + wasScreenOn);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // and do whatever you need to do here
            countPowerOff++;
            doubleBackToExitPressedOnce++;
            wasScreenOn = true;

        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {


                if (doubleBackToExitPressedOnce>=2) {
                   // Toast.makeText(context, "Emergency Mode Started", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(context, MapsActivity3.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                }

                //Toast.makeText(context, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        doubleBackToExitPressedOnce=1;

                    }
                }, 2000);

            }
        }


    }
