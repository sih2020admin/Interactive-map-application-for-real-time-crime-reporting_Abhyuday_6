package com.example.hackathonoriginal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.hackathonoriginal.Databases.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOTP extends AppCompatActivity {

    PinView pinView;
    String codeBySystem;
    TextView otpSent;
    static String phoneN;
    Button verifyOTP;

    ProgressDialog progressDialog, progressDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loadLocale();
        pinView = findViewById(R.id.pinView);
        otpSent = findViewById(R.id.otpsent);
        phoneN = getIntent().getStringExtra("fullPhoneNumber");
        otpSent.setText("OTP sent on " + phoneN);
        otpSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOTP.this, Login.class);
                intent.putExtra("value", phoneN);
                startActivity(intent);

                finish();
            }
        });


        sendVerificationToUser(phoneN);

        progressDialog = new ProgressDialog(this);
        progressDialog1 = new ProgressDialog(this);
    }

    private void sendVerificationToUser(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();

                    if (code != null) {
                        pinView.setText(code);
                        // verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(LoginOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            progressDialog1.setTitle("Please Wait...");
                            progressDialog1.setMessage("Fetching Existing Users..");
                            progressDialog1.show();
                            final String fullPhoneNumber = getIntent().getStringExtra("fullPhoneNumber");

                            Query checkUser = FirebaseDatabase.getInstance().getReference("Users").child("Victim").orderByChild("phoneNo").equalTo(fullPhoneNumber);

                            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        progressDialog1.dismiss();

                                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference ref = database.getReference("Users").child("Victim").child(fullPhoneNumber);
                                        ref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                SessionManager sessionManager = new SessionManager(LoginOTP.this);

                                                sessionManager.setMobile(fullPhoneNumber);
                                                sessionManager.setFullName(snapshot.child("fullName").getValue().toString());
                                                sessionManager.setAdharCard(snapshot.child("adharCard").getValue().toString());
                                                sessionManager.setAddress(snapshot.child("address").getValue().toString());
                                                sessionManager.setGender(snapshot.child("gender").getValue().toString());
                                                sessionManager.setAge(snapshot.child("age").getValue().toString());
                                                sessionManager.setProfileURL(snapshot.child("profileURL").getValue().toString());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                        Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
                                        intent.putExtra("fullPhoneNumber", fullPhoneNumber);
                                        Pair[] pairs = new Pair[1];
                                        pairs[0] = new Pair<>(findViewById(R.id.verifyOTP), "continueButtonL");

                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginOTP.this, pairs);
                                        startActivity(intent, options.toBundle());
                                        Toast.makeText(LoginOTP.this, "Successfully Logged in ", Toast.LENGTH_SHORT).show();
                                        finish();

                                    } else {
                                        progressDialog1.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), SignUp.class);
                                        intent.putExtra("fullPhoneNumber", fullPhoneNumber);
                                        Toast.makeText(LoginOTP.this, "New User, create your account. ", Toast.LENGTH_SHORT).show();
                                        Pair[] pairs = new Pair[1];
                                        pairs[0] = new Pair<>(findViewById(R.id.verifyOTP), "continueButtonL");

                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginOTP.this, pairs);
                                        startActivity(intent, options.toBundle());
                                        Toast.makeText(LoginOTP.this, "New User, create your account. ", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginOTP.this, "Incorrect Code. Please Try Again.", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            }
                            return;
                        }
                    }
                });
    }

    public void callNewScreenAfterOTP(View view) {

        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        verifyOTP = findViewById(R.id.verifyOTP);
        verifyOTP.setEnabled(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        verifyOTP.setEnabled(true);
                    }
                });
            }
        }, 5000);
        String code = pinView.getText().toString();

        if (!code.isEmpty()) {
            verifyCode(code);
        }
    }

    private void setLocale(String lang){
        Locale locale=new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SessionManager sessionManager=new SessionManager(LoginOTP.this);
        sessionManager.setLanguage(lang);
    }

    public void loadLocale(){
        SessionManager sessionManager=new SessionManager(LoginOTP.this);
        setLocale(sessionManager.getLanguage());
    }

}
