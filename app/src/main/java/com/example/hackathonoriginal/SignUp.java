package com.example.hackathonoriginal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.hackathonoriginal.Databases.DatabaseHelper;
import com.example.hackathonoriginal.Databases.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

import static com.example.hackathonoriginal.LoginOTP.phoneN;

public class SignUp extends AppCompatActivity {

    Button signUp;
    TextInputEditText FullName, AdharCard, Address;
    RadioGroup radioGroup;
    DatePicker datePicker;
    RadioButton radioButton;
    ImageView backButton;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loadLocale();
        signUp = findViewById(R.id.signUp);
        FullName = findViewById(R.id.fullName);
        AdharCard = findViewById(R.id.adharcardNo);
        Address = findViewById(R.id.address);
        datePicker = findViewById(R.id.datep);
        radioGroup = findViewById(R.id.radioGroup);
        backButton = findViewById(R.id.back_buttonReg);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, Login.class));
                finish();
            }
        });


    }


    public void CallHomeScreen(View view) {

        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isValidAge = currentYear - userAge;

        String fullNameString = FullName.getText().toString().trim();
        String adharCardString = AdharCard.getText().toString().trim();
        String addressString = Address.getText().toString().trim();
        String genderString = radioButton.getText().toString().trim();
        String ageString = isValidAge + "";
        database = FirebaseDatabase.getInstance();
       // reference = database.getReference("Users");
        String phoneno = getIntent().getStringExtra("fullPhoneNumber");

        if (!validateFullName() | !validateAdharCard() | !validateAddress() | !validateGender() | !validateAge()) {

        } else {

            Intent intent = new Intent(getApplicationContext(), AdharAuthentication.class);

            intent.putExtra("phoneNo",phoneno);
            intent.putExtra("fullNameString",fullNameString);
            intent.putExtra("adharCardString",adharCardString);
            intent.putExtra("addressString",addressString);
            intent.putExtra("genderString",genderString);
            intent.putExtra("ageString",ageString);



            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<>(findViewById(R.id.signUp), "nextButtonL");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp.this, pairs);
                Toast.makeText(this, "Please Verify your Aadhaar Card", Toast.LENGTH_SHORT).show();
                startActivity(intent, options.toBundle());
                finish();

            } else {
                startActivity(intent);
                finish();
            }

        }

    }

    private boolean validateAddress() {
        String val = Address.getText().toString().trim();

        if (val.isEmpty()) {
            Address.setError("Address is Compulsory..");
            return false;
        } else {
            Address.setError(null);
            return true;
        }
        //return true;
    }

    private boolean validateAdharCard() {
        String val = AdharCard.getText().toString().trim();
        String checkSpaces = "\\A\\w{1,16}\\z";

        if (val.isEmpty()) {
            AdharCard.setError("Adhar Card Number is Compulsory..");
            return false;
        } else if (val.length() > 13 || val.length() < 12) {
            AdharCard.setError("Adhar Number should be of 12 digits.");
            return false;
        } else if (!val.matches(checkSpaces)) {
            AdharCard.setError("No white Spaces allowed.");
            return false;
        } else {
            AdharCard.setError(null);
            return true;
        }
    }

    private boolean validateFullName() {
        String val = FullName.getText().toString().trim();

        if (val.isEmpty()) {
            FullName.setError("Full Name is Compulsory..");
            return false;
        } else {
            FullName.setError(null);
            return true;
        }
        //return true;
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isValidAge = currentYear - userAge;
        if (isValidAge < 4) {
            Toast.makeText(this, "You are not eligible to use this app", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public void onBackPressed() {

        startActivity(new Intent(SignUp.this, Login.class));
        finish();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SessionManager sessionManager = new SessionManager(SignUp.this);
        sessionManager.setLanguage(lang);
    }

    public void loadLocale() {
        SessionManager sessionManager = new SessionManager(SignUp.this);
        setLocale(sessionManager.getLanguage());
    }
}
