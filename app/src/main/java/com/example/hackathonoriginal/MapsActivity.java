package com.example.hackathonoriginal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.hackathonoriginal.Databases.History;
import com.example.hackathonoriginal.Databases.HistoryVictim;
import com.example.hackathonoriginal.Databases.SessionManager;
import com.example.hackathonoriginal.Service.FloatingScreen;
import com.example.hackathonoriginal.ui.home.HomeFragment;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener {


    private GoogleMap mMap;
    LocationRequest locationRequest;
    Location lastLocation;
    GoogleApiClient googleApiClient;
    String victim;
    TextView incident;

    TextView show, hide;

    Button findPoliceButton;
    private LatLng pickupLocation;
    private LatLng Hotspot;
    private Boolean requestBool = false;
    private Marker pickupMarker;
    int tem1 = 1;

    TextView VName, VGender, VPhone, PLocation;
    LinearLayout victimView;

    String policeFoundID = "";
    String dateRequest, dateComplete;
    String case1;
    Button notifyCloseOnes;

    ArrayList<Hotspots> myArrayList = new ArrayList<>();

    final DatabaseReference hotspotRefLocation = FirebaseDatabase.getInstance().getReference().child("Hotspots");
    ChildEventListener hotspotRefLocationListner = null;

    int smsCount = 1, smsCount1 = 1;

    ValueEventListener valueEventListener;
    ValueEventListener valueEventListener1;

    String phoneStatic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {

            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }

        loadLocale();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startService(new Intent(getApplicationContext(), LockService.class));

        incident = findViewById(R.id.caseName);

        VName = findViewById(R.id.VName);
        VPhone = findViewById(R.id.VPhone);
        VGender = findViewById(R.id.VGender);
        PLocation = findViewById(R.id.VLocation);
        victimView = findViewById(R.id.victimView);

        hide = findViewById(R.id.hide);
        show = findViewById(R.id.show);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                victimView.setVisibility(View.VISIBLE);
                show.setVisibility(View.INVISIBLE);
                hide.setVisibility(View.VISIBLE);
            }
        });

        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                victimView.setVisibility(View.INVISIBLE);
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
                        Toast.makeText(MapsActivity.this, "Calling permission is not Granted..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(i);
                } catch (Exception e) {

                }
            }
        });


        final SessionManager sessionManager1 = new SessionManager(this);


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("VictimRequest");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(sessionManager1.getMobile()).exists()) {
                    if (!sessionManager1.getPoliceFoundIDSession().isEmpty()) {
                        Toast.makeText(MapsActivity.this, "In the Victim Request", Toast.LENGTH_SHORT).show();

                        findPoliceButton.performClick();
                        databaseReference.removeEventListener(valueEventListener);
                    }
                } else {
                    databaseReference.removeEventListener(valueEventListener);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final SessionManager sessionManager=new SessionManager(MapsActivity.this);







        notifyCloseOnes = findViewById(R.id.notifyCloseOnes);
        notifyCloseOnes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MapsActivity.this, FloatingScreen.class));

                final SessionManager sessionManager = new SessionManager(MapsActivity.this);
                String userPhone = sessionManager.getMobile();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("VictimAvailable");

                GeoFire geoFire = new GeoFire(reference);
                geoFire.setLocation(userPhone, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));
                pickupLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                // pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Victim").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_victim)));
                if (!sessionManager.getTuser1().isEmpty() && !sessionManager.getTuser2().isEmpty()) {
                    if (smsCount == 1) {
                        smsCount = 0;
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            String user11 = sessionManager.getTuser1();
                            String user22 = sessionManager.getTuser2();
                            String message = "EMERGENCY-CP\n" + sessionManager.getFullName() + "\n" + "My Location" + "\nhttps://www.google.com/maps/search/?api=1&query=" + pickupLocation.latitude + "," + pickupLocation.longitude;
                            smsManager.sendTextMessage(user11, null, message, null, null);
                            smsManager.sendTextMessage(user22, null, message, null, null);
                            Toast.makeText(MapsActivity.this, "Location Sent", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(MapsActivity.this, "Check your Phone Number", Toast.LENGTH_SHORT).show();
                        } finally {
                            startActivity(new Intent(MapsActivity.this, DrawerActivity.class));
                        }
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Please Add Trusted Users To send Notifications", Toast.LENGTH_SHORT).show();
                }

            }
        });

        case1 = "Incident: " + getIntent().getStringExtra("case");
        incident.setText(case1);

        case1 = "Incident: " + getIntent().getStringExtra("case");
        incident.setText(case1);


        findPoliceButton = findViewById(R.id.findPoliceButton);

        if (!isConnected(MapsActivity.this)) {
            findPoliceButton.setText("Click to Send Location");
        }

        findPoliceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBool) {

                    requestBool = false;
                    smsToPoliceCheck = 0;
                    if (!isConnected(MapsActivity.this)) {
                        findPoliceButton.setText("Click Again to Send Location");
                    } else {


                        String text1 = "Click Here if You are SAFE";
                        String text2 = "Police Found";
                        if (findPoliceButton.getText().equals(text1) || findPoliceButton.getText().toString().equals(text2)) {

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            dateComplete = "" + dtf.format(now);
                            String victimPhone = sessionManager.getMobile();
                            String victimName = sessionManager.getFullName();
                            String cases;
                            if (sessionManager.getCaseSession().isEmpty()) {
                                cases = getIntent().getStringExtra("case");
                            } else {
                                cases = sessionManager.getCaseSession();
                            }
                            sessionManager.setCaseSession("");
                            policeRefLocation.removeEventListener(policeRefLocationListner);
                            victimRefLocation.removeEventListener(victimRefLocationListener);

                            String PoliceID = policeFoundID;
                            Random rand = new Random();
                            int rand1 = rand.nextInt(10000);

                            History hi = new History(victimName, victimPhone, dateRequest, dateComplete, cases);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PoliceHistory");
                            reference.child(PoliceID).child(victimPhone + "-" + rand1).setValue(hi);

                            HistoryVictim hrV = new HistoryVictim(cases, PoliceID, dateComplete, dateComplete);
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("VictimHistory");
                            reference1.child(victimPhone).child(PoliceID + "-" + rand1).setValue(hrV);
                        }

                        String userPhone = sessionManager.getMobile();

                        if (policeFoundID != null) {
                            DatabaseReference policeRef = FirebaseDatabase.getInstance().getReference("PoliceAssignedTask").child(policeFoundID);
                            policeRef.removeValue();
                            policeFoundID = "";
                            victimView.setVisibility(View.INVISIBLE);
                            hide.setVisibility(View.INVISIBLE);
                            show.setVisibility(View.INVISIBLE);
                            notifyCloseOnes.setVisibility(View.VISIBLE);
                            sessionManager.setPoliceFoundIDSession("");
                        }

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("VictimRequest");
                        GeoFire geoFire = new GeoFire(reference);
                        geoFire.removeLocation(userPhone);


                        if (policeMarker != null) {
                            policeMarker.remove();
                        }
                        if (findPoliceButton.getText().equals("Cancel Request...")) {
                            findPoliceButton.setText("Find Police");
                            startActivity(new Intent(MapsActivity.this, DrawerActivity.class));
                        }
                        if (findPoliceButton.getText().equals("Police Found")) {
                            findPoliceButton.setText("Find Police");
                            startActivity(new Intent(MapsActivity.this, DrawerActivity.class));
                        }
                        findPoliceButton.setText("Find Police");
                        finish();
                    }
                } else {

                    requestBool = true;


                    final String userPhone = sessionManager.getMobile();

                    notifyCloseOnes.setVisibility(View.INVISIBLE);

                    //When there is nothing in session manager

                    if (sessionManager.getPoliceFoundIDSession().isEmpty()) {
                        smsToPoliceCheck = 1;
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("VictimRequest");
                        GeoFire geoFire = new GeoFire(reference);
                        geoFire.setLocation(userPhone, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));
                        pickupLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        reference.child(userPhone).child("case").setValue(getIntent().getStringExtra("case"));
                        sessionManager.setCaseSession(getIntent().getStringExtra("case"));

                    } else {

                        //if session is present. then set location from there.
                        smsToPoliceCheck = 0;
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("VictimRequest").child(userPhone).child("l");
                        valueEventListener1 = reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<Object> map = (List<Object>) snapshot.getValue();
                                double locationLat = 0;
                                double locationLong = 0;
                                if (map.get(0) != null) {
                                    locationLat = Double.parseDouble(map.get(0).toString());
                                }
                                if (map.get(1) != null) {
                                    locationLong = Double.parseDouble(map.get(1).toString());
                                }
                                LatLng policeLatLang = new LatLng(locationLat, locationLong);
                                pickupLocation = policeLatLang;
                                reference.removeEventListener(valueEventListener1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    if (sessionManager.getPoliceFoundIDSession().isEmpty()) {
                        Hotspot = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        Random random = new Random();
                        int randomNo = random.nextInt(100000);
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Hotspots");
                        GeoFire geoFire1 = new GeoFire(reference1);
                        geoFire1.setLocation("Hotspot-" + randomNo, new GeoLocation(Hotspot.latitude, Hotspot.longitude));

                    }

                    if (!isConnected(MapsActivity.this)) {
                        if (!sessionManager.getTuser1().isEmpty() && !sessionManager.getTuser2().isEmpty()) {
                            if (smsCount == 1) {
                                smsCount = 0;
                                try {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    String user11 = sessionManager.getTuser1();
                                    String user22 = sessionManager.getTuser2();
                                    String message = "EMERGENCY\n" + sessionManager.getFullName() + "\n" + case1 + "\nhttps://www.google.com/maps/search/?api=1&query=" + pickupLocation.latitude + "," + pickupLocation.longitude;
                                    smsManager.sendTextMessage(user11, null, message, null, null);
                                    smsManager.sendTextMessage(user22, null, message, null, null);
                                    if(!sessionManager.getTPolice().isEmpty()){
                                        smsManager.sendTextMessage(sessionManager.getTPolice(), null, message, null, null);

                                    }

                                    while (counter < 2) {

                                        smsManager = SmsManager.getDefault();
                                        user11 = sessionManager.getTuser1();
                                        user22 = sessionManager.getTuser2();
                                        if (!sessionManager.getTuser1().isEmpty() && !sessionManager.getTuser2().isEmpty()) {


                                            try {
                                                Thread.sleep(6000 * 5);
                                                counter++;
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            String message1 = "EMERGENCY-CP-" + counter + "\n" + sessionManager.getFullName() + "\n" + case1 + "\nhttps://www.google.com/maps/search/?api=1&query=" + pickupLocation.latitude + "," + pickupLocation.longitude;

                                            smsManager.sendTextMessage(user11, null, message1, null, null);
                                            smsManager.sendTextMessage(user22, null, message1, null, null);
                                            if (!sessionManager.getTPolice().isEmpty()) {
                                                String message2 = "EMERGENCY-KN-" + counter + "\n" + sessionManager.getFullName() + "\n" + case1 + "\nhttps://www.google.com/maps/search/?api=1&query=" + pickupLocation.latitude + "," + pickupLocation.longitude;

                                                smsManager.sendTextMessage(sessionManager.getTPolice(), null, message2, null, null);
                                            }
                                            findPoliceButton.setText("Location Sent.. ");
                                            Toast.makeText(MapsActivity.this, "Location Sent", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    findPoliceButton.setText("Location Sent.. ");
                                    Toast.makeText(MapsActivity.this, "Location Sent to Close People", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MapsActivity.this, DrawerActivity.class));
                                } catch (Exception e) {
                                    Toast.makeText(MapsActivity.this, "Check your Phone Number", Toast.LENGTH_SHORT).show();
                                }
                            }


                        }

                    } else {
                        if (sessionManager.getPoliceFoundIDSession().isEmpty()) {
                            findPoliceButton.setText("Cancel Request...");
                        } else {
                            findPoliceButton.setText("Getting you Police Details");
                        }
                        getClosestPolice();
                    }
                }

            }
        });



    }

    int counter=0;
    private int radius = 1;
    private Boolean policeFound = false;


    GeoQuery geoQuery;

    private void getClosestPolice() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PoliceAvailable");

        SessionManager sessionManager = new SessionManager(MapsActivity.this);

        //SEND SMS TO TRUSTED USERS

        if (sessionManager.getPoliceFoundIDSession().isEmpty()) {

            if (smsCount1 == 1) {
                smsCount1 = 0;
                if (!sessionManager.getTuser1().isEmpty() && !sessionManager.getTuser2().isEmpty()) {
                    SmsManager smsManager = SmsManager.getDefault();
                    String user11 = sessionManager.getTuser1();
                    String user22 = sessionManager.getTuser2();
                    String message = "EMERGENCY-CF\n" + sessionManager.getFullName() + "\n" + case1 + "\nhttps://www.google.com/maps/search/?api=1&query=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude();
                    smsManager.sendTextMessage(user11, null, message, null, null);
                    smsManager.sendTextMessage(user22, null, message, null, null);
                } else {
                    Toast.makeText(MapsActivity.this, "Add Close Friends To Send Live Location", Toast.LENGTH_SHORT).show();
                }
        }


        // SEND SMS TO KNOWN POLICE
        if (tem1 == 1) {

            tem1 = 0;
            try {

                SmsManager smsManager = SmsManager.getDefault();
                String user11 = sessionManager.getTPolice();

                if (!user11.isEmpty()) {
                    String message = "EMERGENCY-KN\n" + sessionManager.getFullName() + "\n" + incident + "\nhttps://www.google.com/maps/search/?api=1&query=" + pickupLocation.latitude + "," + pickupLocation.longitude;
                    smsManager.sendTextMessage(smsToPolice, null, message, null, null);
                } else {
                    Toast.makeText(this, "Known Police Not Found", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        GeoFire geoFire = new GeoFire(reference);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!policeFound && requestBool) {
                    policeFound = true;
                    policeFoundID = key;

                    DatabaseReference policeRef = FirebaseDatabase.getInstance().getReference("PoliceAssignedTask").child(key);
                    SessionManager sessionManager = new SessionManager(MapsActivity.this);


                    String victimId = sessionManager.getMobile();
                    HashMap map = new HashMap();
                    map.put("victimId", victimId);
                    policeRef.updateChildren(map);

                    sessionManager.setPoliceFoundIDSession(policeFoundID);

                    getPoliceLocation();
                    geoQuery.removeAllListeners();

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
                    getClosestPolice();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    } else

    {
        policeFoundID = sessionManager.getPoliceFoundIDSession();
        getPoliceLocation();

    }

}


    Marker policeMarker;
    DatabaseReference policeRefLocation;
    DatabaseReference victimRefLocation;
    private ValueEventListener policeRefLocationListner;
    private ValueEventListener victimRefLocationListener;
    float distance;
    String smsToPolice;

    int smsToPoliceCheck = 1;

    Location location = new Location("");

    ValueEventListener valueEventListenerNew;



    LatLng victimLatLang;

    private void getPoliceLocation() {
        //VICTIMS LIVE LOCATION CODE

        SessionManager sessionManager1=new SessionManager(MapsActivity.this);
        victimRefLocation = FirebaseDatabase.getInstance().getReference().child("VictimAvailable").child(sessionManager1.getMobile()).child("l");
        victimRefLocationListener = victimRefLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && requestBool) {
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double locationLat = 0;
                    double locationLong = 0;

                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLong = Double.parseDouble(map.get(1).toString());
                    }

                    location.setLatitude(locationLat);
                    location.setLongitude(locationLong);

                    victimLatLang= new LatLng(locationLat,locationLong);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        policeRefLocation = FirebaseDatabase.getInstance().getReference().child("PoliceLocation").child(policeFoundID).child("l");


        policeRefLocationListner = policeRefLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && requestBool) {
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double locationLat = 0;
                    double locationLong = 0;
                    findPoliceButton.setText("Police Found");
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLong = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng policeLatLang = new LatLng(locationLat, locationLong);

                    if (policeMarker != null) {
                        policeMarker.remove();
                    }


                    Location location1 = new Location("");
                    location1.setLatitude(locationLat);
                    location1.setLongitude(locationLong);
                    distance = location.distanceTo(location1);
                   // policeMarker = mMap.addMarker(new MarkerOptions().position(policeLatLang).title("Police"));

                    Findroutes(victimLatLang,policeLatLang);

                    final FirebaseDatabase database = FirebaseDatabase.getInstance();

                    final DatabaseReference ref = database.getReference("Users").child("Police").child(policeFoundID);

                    valueEventListenerNew = ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            phoneStatic = snapshot.child("phone").getValue().toString();
                            String id = "Police ID  : " + snapshot.child("id").getValue().toString();
                            String phone = "Contact No : " + snapshot.child("phone").getValue().toString();
                            smsToPolice = snapshot.child("phone").getValue().toString();
                            String gender = "Police Station     : " + snapshot.child("policeStation").getValue().toString();
                            VName.setText(id);
                            VPhone.setText(phone);
                            VGender.setText(gender);
                            SessionManager sessionManager=new SessionManager(MapsActivity.this);
                                if (smsToPoliceCheck == 1) {
                                    smsToPoliceCheck = 0;
                                    SmsManager smsManager = SmsManager.getDefault();
                                    String message = "EMERGENCY-P\n" + sessionManager.getFullName() + "\n" + case1 + "\nhttps://www.google.com/maps/search/?api=1&query=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude();
                                    smsManager.sendTextMessage(phoneStatic, null, message, null, null);
                                }

                            String locationS = "";
                            if (distance > 300) {
                                locationS = "Police is " + (int)Math.ceil(distance / 1000) + " km away";
                                PLocation.setText(locationS);

                            } else {
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationS = "Police is " + distance + " m away";
                                PLocation.setText(locationS);
                                if (distance < 30) {
                                    findPoliceButton.setText("Click Here if You are SAFE");
                                }
                            }



                            victimView.setVisibility(View.VISIBLE);
                            hide.setVisibility(View.VISIBLE);

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            dateRequest = "" + dtf.format(now);
                            ref.removeEventListener(valueEventListenerNew);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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

    final int LOCATION_REQUEST_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    int temp = 1;

    @Override
    public void onLocationChanged(Location location) {

        if (getApplicationContext() != null) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference policeAvailableRef = database.getReference("VictimAvailable");
            SessionManager sessionManager = new SessionManager(MapsActivity.this);
            GeoFire geoFireAvailable = new GeoFire(policeAvailableRef);
            geoFireAvailable.setLocation(sessionManager.getMobile(), new GeoLocation(location.getLatitude(), location.getLongitude()));
            pickupLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }

        lastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        if (temp == 1) {
            temp = 0;
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

        pickupLocation = new LatLng(location.getLatitude(), location.getLongitude());

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        String buttonName = findPoliceButton.getText().toString().trim();
        outState.putString("ButtonName", buttonName);
        super.onSaveInstanceState(outState);
    }

    String button = "";

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        button = savedInstanceState.getString("ButtonName", "Find Police");
        findPoliceButton.setText(button);
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


    public AlertDialog.Builder buildDialog1(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        builder.setTitle("No Internet Connection");
        builder.setMessage("Click On Offline Mode");

        builder.setPositiveButton("Offline Mode", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MapsActivity.this, DrawerActivity.class);
                String value = "Emergency Offline Mode";
                intent.putExtra("case", value);
                startActivity(intent);
            }
        });
        return builder;
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(MapsActivity.this, DrawerActivity.class));
        finish();
        super.onBackPressed();
    }


    Location myLocation=null;
    Location destinationLocation=null;
    protected LatLng start=null;
    protected LatLng end=null;

    private List<Polyline> polylines=null;

    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(MapsActivity.this,"Unable to get location", Toast.LENGTH_LONG).show();
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyBgi2_1j6HsJFAO1X-JvcdNv5aXo6Ev3Zo")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }




    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <arrayList.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(arrayList.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

            }
            else {

            }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location");
        mMap.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        mMap.addMarker(endMarker);
    }


    @Override
    public void onRoutingCancelled() {

    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SessionManager sessionManager = new SessionManager(MapsActivity.this);
        sessionManager.setLanguage(lang);
    }

    public void loadLocale() {
        SessionManager sessionManager = new SessionManager(MapsActivity.this);
        setLocale(sessionManager.getLanguage());
    }

}