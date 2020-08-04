package com.example.hackathonoriginal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackathonoriginal.Databases.SessionManager;
import com.example.hackathonoriginal.Databases.Upload;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class NormalCase extends AppCompatActivity {

    TextInputEditText NFullName, NAddress, NCase, NDescription, NPhone;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button chooseImage;
    private Button submitRequest;
    private ProgressDialog progressDialog;
    private Uri imageUri;

    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_case);
        String case1 = getIntent().getStringExtra("case");
        SessionManager sessionManager = new SessionManager(NormalCase.this);

        loadLocale();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        NFullName = findViewById(R.id.NfullName);
        NAddress = findViewById(R.id.Naddress);
        NCase = findViewById(R.id.NCase);
        NDescription = findViewById(R.id.NDescription);
        NPhone = findViewById(R.id.Nphone);

        startService(new Intent(getApplicationContext(), LockService.class));

        NFullName.setText(sessionManager.getFullName());
        NAddress.setText(sessionManager.getAddress());
        NCase.setText(case1);
        NPhone.setText(sessionManager.getMobile());


        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        getClosestPoliceStation();


        chooseImage = findViewById(R.id.AddImage);
        submitRequest = findViewById(R.id.submitRequest);

        progressDialog = new ProgressDialog(this);

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        submitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadDetails();
            }
        });


    }


    private void uploadDetails() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String dateRequest = "" + dtf.format(now);

        String name1 = NFullName.getText().toString();
        String Location = NAddress.getText().toString();
        String Case = NCase.getText().toString();
        final String parent = NPhone.getText().toString();
        String Description = NDescription.getText().toString();

        if (name1.isEmpty()) {
            NFullName.setError("Name is Compulsory");
            return;
        } else if (Location.isEmpty()) {
            NAddress.setError("Add Location Name only");
            return;
        } else if (Case.isEmpty()) {
            NCase.setError("Add Incident Title");
            return;
        } else if (parent.isEmpty()) {
            NPhone.setError("Add Phone Number");
            return;
        } else if (Description.isEmpty()) {
            NDescription.setError("Add Short Description");
            return;
        } else {

            progressDialog.setTitle("Please Wait...");
            progressDialog.setMessage("Loading..");
            progressDialog.show();

            Random random = new Random();
            final int rand = random.nextInt(1000);

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("PoliceStations").child(policeFoundID).child("Complaints").child(parent + "-" + rand);
            Upload upload = new Upload(name1, Location, Case, Description, NPhone.getText().toString(),dateRequest);
            reference.setValue(upload);

            if (imageUri != null) {
                final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("NormalCaseImages").child(parent + "-" + rand);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

                byte[] data = byteArrayOutputStream.toByteArray();

                UploadTask uploadTask = filepath.putBytes(data);


                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadURL1 = taskSnapshot.getStorage().getDownloadUrl().toString();
                        String downloadURL = "gs://janrakshak-app-new.appspot.com/NormalCaseImages/" + parent + "-" + rand;
                        final Map map = new HashMap();

                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(downloadURL);
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                map.put("UrlImage", uri.toString());
                                reference.updateChildren(map);
                            }
                        });
                        progressDialog.dismiss();
                        Toast.makeText(NormalCase.this, "We'll get Back to you soon", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        finish();

                    }
                });

            } else {
                Toast.makeText(NormalCase.this, "We'll get Back to you soon", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }*/
        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
                    imageUri = data.getData();
                }
                break;
        }
    }

    GeoQuery geoQuery;

    private int radius = 1;
    private Boolean policeFound = false;
    String policeFoundID;

    private void getClosestPoliceStation() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PSAvailable");
        SessionManager sessionManager = new SessionManager(NormalCase.this);

        GeoFire geoFire = new GeoFire(reference);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), radius);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!policeFound) {
                    policeFound = true;
                    policeFoundID = key;


                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!policeFound) {
                    radius++;
                    getClosestPoliceStation();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SessionManager sessionManager = new SessionManager(NormalCase.this);
        sessionManager.setLanguage(lang);
    }

    public void loadLocale() {
        SessionManager sessionManager = new SessionManager(NormalCase.this);
        setLocale(sessionManager.getLanguage());
    }

}
