package com.example.hackathonoriginal.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.hackathonoriginal.R;
import com.example.hackathonoriginal.SpeechActivity;

import java.util.Locale;

import static android.graphics.Color.WHITE;

public class FloatingScreen extends Service {


    private WindowManager wm;
    private LinearLayout ll;
 public static Button buttonVoice;
    private Button close;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        ll = new LinearLayout(this);
        LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.BLACK);
        ll.setLayoutParams(llParameters);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(100, 100, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.CENTER;


        buttonVoice=new Button(this);
        close=new Button(this);

        ViewGroup.LayoutParams voiceParam= new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonVoice.setForeground(getDrawable(R.drawable.ic_keyboard_voice_black_24dp));
        buttonVoice.setBackground(getDrawable(R.drawable.black_border_new));
        ll.addView(buttonVoice);
        wm.addView(ll,params);

        buttonVoice.setOnTouchListener(new View.OnTouchListener() {
            private WindowManager.LayoutParams updateParams = params;
            int x, y;
            float touchX, touchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = updateParams.x;
                        y = updateParams.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateParams.x=(int)(x+(event.getRawX())-touchX);
                        updateParams.y=(int)(y+(event.getRawY())-touchY);
                        wm.updateViewLayout(ll,updateParams);
                    default:break;
                }
                return false;
            }
        });

        ll.setOnTouchListener(new View.OnTouchListener() {
            private WindowManager.LayoutParams updateParams = params;
            int x, y;
            float touchX, touchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = updateParams.x;
                        y = updateParams.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateParams.x=(int)(x+(event.getRawX())-touchX);
                        updateParams.y=(int)(y+(event.getRawY())-touchY);
                        wm.updateViewLayout(ll,updateParams);
                        default:break;
                }
                return false;
            }
        });


        buttonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FloatingScreen.this, SpeechActivity.class));
            }
        });

    }


}
