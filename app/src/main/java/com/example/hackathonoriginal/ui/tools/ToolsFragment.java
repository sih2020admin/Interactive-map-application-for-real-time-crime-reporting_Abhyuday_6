package com.example.hackathonoriginal.ui.tools;

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
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.hackathonoriginal.Databases.SessionManager;
import com.example.hackathonoriginal.Databases.Upload;
import com.example.hackathonoriginal.DrawerActivity;
import com.example.hackathonoriginal.NormalCase;
import com.example.hackathonoriginal.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;

    TextInputEditText NFullName, NAddress, NCase, NDescription, NPhone;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button chooseImage;
    private Button submitRequest;
    private ProgressDialog progressDialog;
    private Uri imageUri;

    double longitude;
    double latitude;

    String phoneStatic;
    int smsToPoliceCheck=1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);

        SessionManager sessionManager = new SessionManager(getActivity());

        NFullName = root.findViewById(R.id.NfullName);
        NAddress = root.findViewById(R.id.Naddress);
        NCase = root.findViewById(R.id.NCase);
        NDescription = root.findViewById(R.id.NDescription);
        NPhone = root.findViewById(R.id.Nphone);

        NFullName.setText(sessionManager.getFullName());
        NAddress.setText(sessionManager.getAddress());
        NPhone.setText(sessionManager.getMobile());

        loadLocale();

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return root;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        getClosestPoliceStation();


        chooseImage = root.findViewById(R.id.AddImage);
        submitRequest = root.findViewById(R.id.submitRequest);

        progressDialog = new ProgressDialog(getActivity());

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

        return root;
    }

    private void uploadDetails() {

        String name1 = NFullName.getText().toString();
        String Location = NAddress.getText().toString();
        String Case = NCase.getText().toString();
        final String parent = NPhone.getText().toString();
        String Description = NDescription.getText().toString();

        if(name1.isEmpty()){
            NFullName.setError("Name is Compulsory");
            return;
        }else if(Location.isEmpty()){
            NAddress.setError("Add Location Name only");
            return;
        }else if(Case.isEmpty()){
            NCase.setError("Add Incident Title");
            return;
        }else if(parent.isEmpty()){
            NPhone.setError("Add Phone Number");
            return;
        }else if(Description.isEmpty()) {
            NDescription.setError("Add Short Description");
            return;
        }
        else{
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

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String dateRequest = "" + dtf.format(now);
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("PoliceStations").child(policeFoundID).child("GoodCitizenComplaints").child(parent + "-" + rand);
                Upload upload = new Upload(name1, Location, Case, Description, NPhone.getText().toString(), dateRequest);
                reference.setValue(upload);
                Random random1=new Random();
                final int rand2=random1.nextInt(100);

                if (imageUri != null) {
                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("CitizenImages").child(parent + "-" + rand);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getApplicationContext().getContentResolver(), imageUri);
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
                            String downloadURL = "gs://janrakshak-app-new.appspot.com/CitizenImages/"+parent + "-" + rand;
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
                            Toast.makeText(getActivity(), "Thank You.. You are a good Citizen :)", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), DrawerActivity.class));
                            getActivity().finish();

                            return;
                        }
                    });
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            getActivity().finish();
                            return;

                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "Thank You.. You are a good Citizen :)", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity(), DrawerActivity.class));
                    getActivity().finish();
                }

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("Users").child("Police").child(policeFoundID);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        phoneStatic=snapshot.child("phone").getValue().toString();

                        SessionManager sessionManager= new SessionManager(getActivity());

                        if (smsToPoliceCheck == 1) {
                            smsToPoliceCheck = 0;
                            SmsManager smsManager = SmsManager.getDefault();
                            String user11 = sessionManager.getTuser1();
                            String user22 = sessionManager.getTuser2();
                            String message = "EMERGENCY-PS\n" + sessionManager.getFullName() + "\n" + "Good Citizen Response" + "\nhttps://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
                            smsManager.sendTextMessage(phoneStatic, null, message, null, null);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }*/
        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    Toast.makeText(getActivity(), "Image Selected", Toast.LENGTH_SHORT).show();
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
        SessionManager sessionManager = new SessionManager(getActivity());

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

    private void setLocale(String lang){
        Locale locale=new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getActivity().getBaseContext().getResources().updateConfiguration(configuration,getActivity().getBaseContext().getResources().getDisplayMetrics());
        SessionManager sessionManager=new SessionManager(getActivity());
        sessionManager.setLanguage(lang);
    }

    public void loadLocale(){
        SessionManager sessionManager=new SessionManager(getActivity());
        setLocale(sessionManager.getLanguage());
    }
}