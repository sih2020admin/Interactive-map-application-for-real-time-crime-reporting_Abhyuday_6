package com.example.hackathonoriginal.ui;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackathonoriginal.Databases.SessionManager;
import com.example.hackathonoriginal.LockService;
import com.example.hackathonoriginal.R;
import com.example.hackathonoriginal.ui.tools.ToolsViewModel;

import java.util.Locale;

public class HelpLine extends Fragment {

    private HelpLineViewModel mViewModel;

    ListView listView;

    String mTitle[] = {"National Emergency Number", "Police", "Fire", "Ambulance","Aids Helpline","Air Ambulance","Anti Poison","Call Centre","Central Vigilance Commission","Children In Difficult Situation","Disaster Management Services","Earthquake / Flood / Disaster N.D.R.F","LPG Leak Helpline","Railway Enquiry","Railway Accident Emergency Service","Relief Commissioner For Natural Calamities","Road Accident Emergency Service","Senior Citizen Helpline","Tourist Helpline"};
    String mDescription[] = {"112", "100", "101","102","1097","9540161344","011-1066","1551","1964","1098","108","011-24363260","1906","139","1072"," 1070","1073","1091","1363"};



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel =
                ViewModelProviders.of(this).get(HelpLineViewModel.class);

        View root = inflater.inflate(R.layout.help_line_fragment, container, false);


        listView = root.findViewById(R.id.listView);

        MyAdapter adapter = new MyAdapter(getActivity(), mTitle, mDescription);
        listView.setAdapter(adapter);
        loadLocale();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    Uri u = Uri.parse("tel:" + mDescription[0]);

                    // Create the intent and set the data for the
                    // intent as the phone number.
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        // Launch the Phone app's dialer with a phone
                        // number to dial a call.
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                        // show() method display the toast with
                        // exception message.
                        //Toast.makeText(this, s.Message, Toast.LENGTH_LONG)
                        //.show();
                    }



                }
                if (position == 1) {
                    Uri u = Uri.parse("tel:" + mDescription[1]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 2) {
                    Uri u = Uri.parse("tel:" + mDescription[2]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }

                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 3) {
                    Uri u = Uri.parse("tel:" + mDescription[3]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 4) {
                    Uri u = Uri.parse("tel:" + mDescription[4]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 5) {
                    Uri u = Uri.parse("tel:" + mDescription[5]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 6) {
                    Uri u = Uri.parse("tel:" + mDescription[6]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 7) {
                    Uri u = Uri.parse("tel:" + mDescription[7]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 8) {
                    Uri u = Uri.parse("tel:" + mDescription[8]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 9) {
                    Uri u = Uri.parse("tel:" + mDescription[9]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 10) {
                    Uri u = Uri.parse("tel:" + mDescription[10]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 11) {
                    Uri u = Uri.parse("tel:" + mDescription[11]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 12) {
                    Uri u = Uri.parse("tel:" + mDescription[12]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 13) {
                    Uri u = Uri.parse("tel:" + mDescription[13]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 14) {
                    Uri u = Uri.parse("tel:" + mDescription[14]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }

                if (position == 15) {
                    Uri u = Uri.parse("tel:" + mDescription[15]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 16) {
                    Uri u = Uri.parse("tel:" + mDescription[16]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 17) {
                    Uri u = Uri.parse("tel:" + mDescription[17]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 18) {
                    Uri u = Uri.parse("tel:" + mDescription[18]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }
                if (position == 19) {
                    Uri u = Uri.parse("tel:" + mDescription[19]);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try
                    {
                        startActivity(i);
                    }
                    catch (SecurityException s)
                    {
                    }
                }

            }
        });

        return root;
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];
        String rDescription[];


        MyAdapter (Context c, String title[], String description[]) {
            super(c, R.layout.row, R.id.textView1, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = layoutInflater.inflate(R.layout.row, parent, false);


            TextView myTitle = row.findViewById(R.id.textView1);
            TextView myDescription = row.findViewById(R.id.textView2);


            myTitle.setText(rTitle[position]);
            myDescription.setText(rDescription[position]);

            return row;
        }
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
