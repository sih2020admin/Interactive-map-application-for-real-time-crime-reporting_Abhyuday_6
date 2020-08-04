package com.example.hackathonoriginal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackathonoriginal.Databases.DatabaseHelper;
import com.example.hackathonoriginal.Databases.SessionManager;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdharAuthentication extends AppCompatActivity implements SurfaceHolder.Callback, Detector.Processor{

    private SurfaceView cameraView;
    private TextView txtView;
    private CameraSource cameraSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhar_authentication);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        cameraView = findViewById(R.id.surface_view);
        txtView = findViewById(R.id.txtview);

        TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!txtRecognizer.isOperational()) {
            Log.e("Main Activity", "Detector dependencies are not yet available");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), txtRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback((SurfaceHolder.Callback) this);
            txtRecognizer.setProcessor((Detector.Processor<TextBlock>) AdharAuthentication.this);
        }



    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},1);
                return;
            }
            cameraSource.start(cameraView.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSource.stop();
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections detections) {
        SparseArray items = detections.getDetectedItems();
        final StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < items.size(); i++)
        {
            TextBlock item = (TextBlock)items.valueAt(i);
            strBuilder.append(item.getValue());
            strBuilder.append("/");
            // The following Process is used to show how to use lines & elements as well
            for (int j = 0; j < items.size(); j++) {
                TextBlock textBlock = (TextBlock) items.valueAt(j);
                strBuilder.append(textBlock.getValue());
                strBuilder.append("/");
                for (Text line : textBlock.getComponents()) {
                    //extract scanned text lines here
                    Log.v("lines", line.getValue());
                    strBuilder.append(line.getValue());
                    strBuilder.append("/");
                    for (Text element : line.getComponents()) {
                        //extract scanned text words here
                        Log.v("element", element.getValue());
                        strBuilder.append(element.getValue());
                    }
                }
            }
        }
        Log.v("strBuilder.toString()", strBuilder.toString());

        final String phoneno=getIntent().getStringExtra("phoneNo");
        final String ageString=getIntent().getStringExtra("ageString");
        final String fullNameString=getIntent().getStringExtra("fullNameString");
        final String adharCardString=getIntent().getStringExtra("adharCardString");
        final String addressString=getIntent().getStringExtra("addressString");
        final String genderString=getIntent().getStringExtra("genderString");

        txtView.post(new Runnable() {
            @Override
            public void run() {

                txtView.setText(strBuilder.toString());
                String pfString=fullNameString.replace(" ","");
                String str=strBuilder.toString().replace(" ","");
                if(str.contains(pfString) && str.contains(adharCardString)){

                    SessionManager sessionManager = new SessionManager(AdharAuthentication.this);
                    sessionManager.setMobile(phoneno);
                    sessionManager.setFullName(fullNameString);
                    sessionManager.setAdharCard(adharCardString);
                    sessionManager.setAddress(addressString);
                    sessionManager.setGender(genderString);
                    sessionManager.setAge(ageString);

                    String val = "";
                    DatabaseHelper help = new DatabaseHelper(phoneno, fullNameString, adharCardString, addressString, genderString, ageString, val);
                    FirebaseDatabase database;

                    database = FirebaseDatabase.getInstance();
                    DatabaseReference reference;
                    reference = database.getReference("Users");
                    reference.child("Victim").child(phoneno).setValue(help);
                    txtView.setText("Authentication SuccessFul..");
                    startActivity(new Intent(AdharAuthentication.this,DrawerActivity.class));
                    finish();
                }
                else if((strBuilder.toString().replace(" ","").contains("Aadhaar") && strBuilder.toString().replace(" ","").contains("Unique")) || (strBuilder.toString().replace(" ","").contains("VID") && strBuilder.toString().replace(" ","").contains("Government") )){
                  txtView.setText("Aadhar Card Found.");
                }
                else{
                    txtView.setText("No Aadhar Card Found.");
                }

            }
        });
    }
}

