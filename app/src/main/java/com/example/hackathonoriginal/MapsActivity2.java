package com.example.hackathonoriginal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackathonoriginal.Databases.SessionManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    LocationRequest locationRequest;
    Location lastLocation;
    GoogleApiClient googleApiClient;
    final int LOCATION_REQUEST_CODE = 1;

    TextView VName, VGender, VPhone, VLocation, Condition;
    LinearLayout victimVieew;
    Button checkCondition;

    TextView show, hide;

    String phoneStatic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }

        loadLocale();
        startService(new Intent(getApplicationContext(), LockService.class));

        VName = findViewById(R.id.VName);
        VPhone = findViewById(R.id.VPhone);
        VGender = findViewById(R.id.VGender);
        VLocation = findViewById(R.id.VLocation);
        victimVieew = findViewById(R.id.victimView);

        hide = findViewById(R.id.hide);
        show = findViewById(R.id.show);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                victimVieew.setVisibility(View.VISIBLE);
                show.setVisibility(View.INVISIBLE);
                hide.setVisibility(View.VISIBLE);
            }
        });

        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                victimVieew.setVisibility(View.INVISIBLE);
                hide.setVisibility(View.INVISIBLE);
                show.setVisibility(View.VISIBLE);
            }
        });


        VPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri u = Uri.parse("tel:" + phoneStatic);
                Intent i = new Intent(Intent.ACTION_CALL, u);
                try {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MapsActivity2.this, "Calling permission is not Granted", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(i);
                }catch(Exception e){

                }
            }
        });


        Condition = findViewById(R.id.condition);
        Condition.setText("No Friend Found..");



        final SessionManager sessionManager=new SessionManager(MapsActivity2.this);
        victimID = getIntent().getStringExtra("victimID");
        Toast.makeText(MapsActivity2.this, victimID, Toast.LENGTH_SHORT).show();

        getAssignedVictim();

        assignedVictimLocationRef = FirebaseDatabase.getInstance().getReference().child("VictimRequest").child(victimID);
        assignedVictimLocationRefListener = assignedVictimLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Condition.setText("Your Friend is NOT SAFE.");

                }
                else{
                    victimVieew.setVisibility(View.INVISIBLE);
                    Condition.setText("Your Friend is SAFE now.");
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("TrustedCircle").child(sessionManager.getMobile());
                    reference.removeValue();
                    Toast.makeText(MapsActivity2.this, "Don't Worry Your Friend is Safe", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MapsActivity2.this, DrawerActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    LatLng victimLatLang;

    private void getAssignedVictim() {
        SessionManager sessionManager = new SessionManager(MapsActivity2.this);
        assignedVictimLocationRef = FirebaseDatabase.getInstance().getReference().child("VictimAvailable").child(sessionManager.getMobile()).child("l");
        assignedVictimLocationRefListener = assignedVictimLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLong = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLong = Double.parseDouble(map.get(1).toString());
                    }
                    victimLatLang = new LatLng(locationLat, locationLong);
                    getAssignedVictimPickupLocation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    assert mapFragment != null;
                    mapFragment.getMapAsync(this);
                } else {
                    Toast.makeText(this, "Please Provide Permissions", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    LatLng latLng;
    int temp=1;

    @Override
    public void onLocationChanged(Location location) {


        if (getApplicationContext() != null) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference policeAvailableRef = database.getReference("TrustedCircle");
            SessionManager sessionManager = new SessionManager(MapsActivity2.this);
            GeoFire geoFireAvailable = new GeoFire(policeAvailableRef);
            geoFireAvailable.setLocation(sessionManager.getMobile(), new GeoLocation(location.getLatitude(), location.getLongitude()));

        }


        lastLocation = location;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        if(temp==1){
            temp=0;
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

    }

    Marker pickupMarker;
    DatabaseReference assignedVictimLocationRef;
    private ValueEventListener assignedVictimLocationRefListener;
    float distance = 0;
    String victimID = "";

    private void getAssignedVictimPickupLocation() {

        victimID = getIntent().getStringExtra("victimID");

        SessionManager sessionManager=new SessionManager(this);

        assignedVictimLocationRef = FirebaseDatabase.getInstance().getReference().child("VictimAvailable").child(victimID).child("l");
        assignedVictimLocationRefListener = assignedVictimLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !victimID.equals("")) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLong = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLong = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng victimLatLang = new LatLng(locationLat, locationLong);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(victimLatLang).title("Friend"));

                    Location location = new Location("");
                    location.setLatitude(victimLatLang.latitude);
                    location.setLongitude(victimLatLang.longitude);

                    Location location1 = new Location("");
                    location1.setLatitude(locationLat);
                    location1.setLongitude(locationLong);

                    distance = location.distanceTo(location1);


                    String condition = "Your Friend is NOT SAFE";
                    Condition.setVisibility(View.VISIBLE);
                    Condition.setText(condition);
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("Users").child("Victim").child(victimID);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            SessionManager sessionManager = new SessionManager(MapsActivity2.this);

                            DatabaseReference assignedVictimCase = FirebaseDatabase.getInstance().getReference().child("VictimRequest").child(victimID).child("case");
                            assignedVictimCase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String gender = "Case   : " + dataSnapshot.getValue().toString();
                                        VGender.setText(gender);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            String id = "Friend's Name  : " + snapshot.child("fullName").getValue().toString();
                            String phone = "Contact No    : " + snapshot.child("phoneNo").getValue().toString();
                            phoneStatic=snapshot.child("phoneNo").getValue().toString();
                            VName.setText(id);
                            VPhone.setText(phone);
                            victimVieew.setVisibility(View.VISIBLE);
                            hide.setVisibility(View.VISIBLE);


                            String locationS = "";
                            if (distance > 1000) {
                                locationS = "Distance : " + (distance/1000) + " km";
                                VLocation.setText(locationS);
                            } else {
                                locationS = "Distance : " + distance + " m";
                                VLocation.setText(locationS);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SessionManager sessionManager = new SessionManager(MapsActivity2.this);
        sessionManager.setLanguage(lang);
    }

    public void loadLocale() {
        SessionManager sessionManager = new SessionManager(MapsActivity2.this);
        setLocale(sessionManager.getLanguage());
    }

}
