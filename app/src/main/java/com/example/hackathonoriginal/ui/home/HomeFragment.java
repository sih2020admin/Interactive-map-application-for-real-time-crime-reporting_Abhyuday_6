package com.example.hackathonoriginal.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.hackathonoriginal.Databases.SessionManager;
import com.example.hackathonoriginal.DrawerActivity;
import com.example.hackathonoriginal.MapsActivity;
import com.example.hackathonoriginal.MapsActivity4;
import com.example.hackathonoriginal.NormalCase;
import com.example.hackathonoriginal.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {

    public static ListView mListView;
    ArrayList<String> myArrayList = new ArrayList<>();
    EditText GetValue;
    FirebaseDatabase database;
    DatabaseReference mRef;

    TextView lang;


    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        lang = root.findViewById(R.id.lang);
        loadLocale();

        final SessionManager sessionManager = new SessionManager(getActivity());

            //lang.setText(sessionManager.getLanguage());


        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sessionManager.getLanguage().equals("") || sessionManager.getLanguage().equals("en")){
                    sessionManager.setLanguage("hi");
                    setLocale("hi");
                    startActivity(new Intent(getActivity(),DrawerActivity.class));
                }
                else if(sessionManager.getLanguage().equals("hi")){
                    sessionManager.setLanguage("en");
                    setLocale("en");
                    startActivity(new Intent(getActivity(),DrawerActivity.class));
                }

            }
        });


        mListView = (ListView) root.findViewById(R.id.listView);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, myArrayList);

        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Cases");
        mListView.setAdapter(arrayAdapter);
        myArrayList.add("----> EMERGENCY COMPLAINTS");
        myArrayList.add("Accident");
        myArrayList.add("Acid Attack");
        myArrayList.add("Domestic Violence");
        myArrayList.add("Fire");
        myArrayList.add("Murder");
        myArrayList.add("Rape");
        myArrayList.add("Wildlife / Human Trafficking");
        myArrayList.add("Other E");
        //myArrayList.add("-----------------------------------------");
        myArrayList.add("----> NON-EMERGENCY COMPLAINTS");
        myArrayList.add("Cyber Crime");
        myArrayList.add("Corruption");
        myArrayList.add("Dowry");
        myArrayList.add("Drug Trade");
        myArrayList.add("Lost and Found");
        myArrayList.add("Poaching");
        myArrayList.add("Robbery");
        myArrayList.add("Stalking");
        myArrayList.add("Other N");
        arrayAdapter.notifyDataSetChanged();

        if (!isConnected(getActivity()))
            buildDialog(getActivity()).show();
        else {





  /*
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @com.google.firebase.database.annotations.Nullable String s) {
                String values = dataSnapshot.getValue(String.class);
                myArrayList.add(values);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @com.google.firebase.database.annotations.Nullable String s) {
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @com.google.firebase.database.annotations.Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
*/
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    Intent intent1 = new Intent(getActivity(), NormalCase.class);
                    if (mListView.getItemAtPosition(i).toString().equals("----> EMERGENCY COMPLAINTS")) {
                        Toast.makeText(getActivity(), "Please select complaint from the list", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (mListView.getItemAtPosition(i).toString().equals("Accident")) {
                        intent.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Acid Attack")) {
                        intent.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Domestic Violence")) {
                        intent.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Fire")) {
                        intent.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Murder")) {
                        intent.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Rape")) {
                        intent.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Wildlife / Human Trafficking")) {
                        intent.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Other E")) {
                        intent.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent);
                    } else if (mListView.getItemAtPosition(i).toString().equals("----> NON-EMERGENCY COMPLAINTS")) {
                        Toast.makeText(getActivity(), "Please select complaint from the list", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (mListView.getItemAtPosition(i).toString().equals("Cyber Crime")) {
                        intent1.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent1);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Corruption")) {
                        intent1.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent1);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Dowry")) {
                        intent1.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent1);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Drug Trade")) {
                        intent1.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent1);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Lost and Found")) {
                        intent1.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent1);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Poaching")) {
                        intent1.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent1);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Robbery")) {
                        intent1.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent1);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Stalking")) {
                        intent1.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent1);
                    } else if (mListView.getItemAtPosition(i).toString().equals("Other N")) {
                        intent1.putExtra("case", mListView.getItemAtPosition(i).toString());
                        startActivity(intent1);
                    }

                }
            });
        }

        return root;
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

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Switch To OFFLINE MODE");

        builder.setPositiveButton("OffLINE MODE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                String value = "Emergency Offline Mode";
                intent.putExtra("case", value);
                startActivity(intent);
            }
        });
        return builder;
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(configuration, getActivity().getBaseContext().getResources().getDisplayMetrics());
        SessionManager sessionManager = new SessionManager(getActivity());
        sessionManager.setLanguage(lang);
    }

    public void loadLocale() {
        SessionManager sessionManager = new SessionManager(getActivity());
        setLocale(sessionManager.getLanguage());
    }
}