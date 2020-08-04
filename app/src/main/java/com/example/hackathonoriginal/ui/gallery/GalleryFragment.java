package com.example.hackathonoriginal.ui.gallery;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
import com.example.hackathonoriginal.DrawerActivity;
import com.example.hackathonoriginal.MapsActivity;
import com.example.hackathonoriginal.MapsActivity2;
import com.example.hackathonoriginal.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class GalleryFragment extends Fragment {

    private Button editUsers;
    private TextInputEditText user1, user2, friendPhone, policeT;
    private GalleryViewModel galleryViewModel;

    Button MapsActivity2;
    Boolean value = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        loadLocale();

        user1 = root.findViewById(R.id.trustedUser1);
        user2 = root.findViewById(R.id.trustedUser2);
        editUsers = root.findViewById(R.id.editUsers);
        friendPhone = root.findViewById(R.id.friendPhone);
        SessionManager sessionManager = new SessionManager(getActivity());
        user1.setText(sessionManager.getTuser1());
        user2.setText(sessionManager.getTuser2());
        policeT = root.findViewById(R.id.trustedPolice);
        policeT.setText(sessionManager.getTPolice());

        if (!user1.getText().toString().isEmpty()) {
            editUsers.setText("Edit Close People");
        }

        editUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!value) {
                    value = true;

                    editUsers.setText("Update Close People");
                    user1.setFocusable(true);
                    user1.setFocusableInTouchMode(true);
                    user1.setClickable(true);

                    user2.setFocusable(true);
                    user2.setFocusableInTouchMode(true);
                    user2.setClickable(true);

                    policeT.setFocusable(true);
                    policeT.setFocusableInTouchMode(true);
                    policeT.setClickable(true);
                } else {

                    if (validatePhone()) {

                        value = false;
                        editUsers.setText("Edit Close People");
                        String user11 = "", user22 = "";
                        user11 = user1.getText().toString();
                        user22 = user2.getText().toString();

                        String fullPhoneNumber1 = user11;
                        String fullPhoneNumber2 = user22;

                        if (!fullPhoneNumber1.startsWith("+91")) {
                            fullPhoneNumber1 = "+91" + fullPhoneNumber1;
                        }

                        if (!fullPhoneNumber2.startsWith("+91")) {
                            fullPhoneNumber2 = "+91" + fullPhoneNumber2;
                        }

                        SessionManager sessionManager = new SessionManager(getContext());
                        sessionManager.setTuser1(fullPhoneNumber1);
                        sessionManager.setTuser2(fullPhoneNumber2);

                        String policeT1 = "";
                        policeT1 = policeT.getText().toString();

                        if (!policeT1.isEmpty()) {

                            String fullPhone = policeT1;
                            if (policeT1.length() != 10) {
                                policeT.setError("Phone Number should be of 10 digits.");
                                return;

                            } else {
                                if (!policeT1.startsWith("+91")) {
                                    policeT1 = "+91" + policeT1;
                                    sessionManager.setTPolice(policeT1);
                                }else{
                                    sessionManager.setTPolice(policeT1);
                                }
                            }

                        }
                        else{
                            policeT.setError("This field can't be empty..");
                        }
                        Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                        user1.setFocusable(false);
                        user1.setFocusableInTouchMode(false);
                        user1.setClickable(false);

                        user2.setFocusable(false);
                        user2.setFocusableInTouchMode(false);
                        user2.setClickable(false);

                        policeT.setFocusable(false);
                        policeT.setFocusableInTouchMode(false);
                        policeT.setClickable(false);

                        startActivity(new Intent(getActivity(), DrawerActivity.class));

                    } else {
                        return;
                    }
                }
            }
        });

        MapsActivity2 = root.findViewById(R.id.traceUser);
        MapsActivity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String policeT1;
                String NoFull;
                policeT1 = friendPhone.getText().toString();

                if (!policeT1.isEmpty()) {

                    String fullPhone = policeT1;
                    if (policeT1.length() != 10) {
                        friendPhone.setError("Invalid Phone Number..");

                    } else if (!policeT1.startsWith("+91")) {
                        NoFull = "+91" + policeT1;
                        Intent intent = new Intent(getActivity(), MapsActivity2.class);
                        intent.putExtra("victimID", NoFull);
                        getActivity().finish();
                        startActivity(intent);
                    } else {
                        NoFull = policeT1;
                        Intent intent = new Intent(getActivity(), MapsActivity2.class);
                        intent.putExtra("victimID", NoFull);
                        getActivity().finish();
                        startActivity(intent);
                    }


                } else {
                    friendPhone.setError("Phone Number is Compulsory");
                }

            }
        });
        return root;
    }

    public Boolean validatePhone() {

        String val1 = user1.getText().toString().trim();
        String val2 = user2.getText().toString().trim();

        if (val1.length() != 13 && val2.length() != 13) {

            if (val1.isEmpty()) {
                user1.setError("Close Person 1 is Compulsory");
                return false;
            } else if (val1.length() != 10) {
                user1.setError("Invalid Phone Number..");
                return false;
            } else if (val2.isEmpty()) {
                user2.setError("Close Person 2 is Compulsory");
                return false;
            } else if (val2.length() != 10) {
                user2.setError("Invalid Phone Number..");
                return false;
            }
        }
        return true;
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