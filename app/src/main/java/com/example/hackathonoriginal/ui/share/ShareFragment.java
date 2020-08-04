package com.example.hackathonoriginal.ui.share;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.hackathonoriginal.Databases.DatabaseHelper;
import com.example.hackathonoriginal.Databases.SessionManager;
import com.example.hackathonoriginal.Databases.Upload;
import com.example.hackathonoriginal.DrawerActivity;
import com.example.hackathonoriginal.LoginOTP;
import com.example.hackathonoriginal.R;
import com.example.hackathonoriginal.SignUp;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ShareFragment extends Fragment {

    private ShareViewModel shareViewModel;

    TextInputEditText fullName, adharNo, Address, Gender, Phone, Age;
    Button editProfile;
    ImageView editImage;
    Boolean value = false;
    CircleImageView img;

    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);


        fullName = root.findViewById(R.id.SfullName);
        adharNo = root.findViewById(R.id.SadharcardNo);
        Address = root.findViewById(R.id.Saddress);
        editProfile = root.findViewById(R.id.editprofile);
        Phone = root.findViewById(R.id.Sphone);
        Age = root.findViewById(R.id.Sage);
        Gender = root.findViewById(R.id.Sgender);
        editImage = root.findViewById(R.id.editImage);
        img = root.findViewById(R.id.proImage);
        progressDialog = new ProgressDialog(getActivity());

        final String number = Phone.getText().toString();
        final SessionManager sessionManager = new SessionManager(getContext());
        fullName.setText(sessionManager.getFullName());
        adharNo.setText(sessionManager.getAdharCard());
        Address.setText(sessionManager.getAddress());
        Age.setText(sessionManager.getAge());
        Gender.setText(sessionManager.getGender());
        Phone.setText(sessionManager.getMobile());
        loadLocale();

        if (!sessionManager.getProfileURL().isEmpty())
            Glide.with(getActivity()).load(sessionManager.getProfileURL()).into(img);


        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!value) {
                    value = true;
                    editProfile.setText("Submit");
                    editImage.setVisibility(View.VISIBLE);
                    Phone.setFocusable(true);
                    Phone.setFocusableInTouchMode(true);
                    Phone.setClickable(true);


                } else {
                    value = false;
                    uploadDetails();

                    editImage.setVisibility(View.INVISIBLE);

                    if(Phone.getText().toString().isEmpty()){
                        Phone.setError("Phone Number Can't Be Empty");
                    }
                    else if(Phone.getText().toString().length() <10 || Phone.getText().toString().length() >13 ){
                        Phone.setError("Phone Number Should be of 10 Digits");
                    }
                    else{
                        if (sessionManager.getMobile().equals(Phone.getText().toString())) {
                            editProfile.setText("Edit Profile");
                            startActivity(new Intent(getActivity(),DrawerActivity.class));
                            return;
                        }

                        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").child("Victim").orderByChild("phoneNo").equalTo(sessionManager.getMobile());

                        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String fullPhoneNumber;
                                    String val = Phone.getText().toString().trim();

                                    if (val.startsWith("+91")) {
                                        fullPhoneNumber = Phone.getText().toString().trim();
                                    } else {
                                        fullPhoneNumber = "+91" + Phone.getText().toString().trim();
                                    }

                                    Query checkUser = FirebaseDatabase.getInstance().getReference("Users").child("Victim").orderByChild("phoneNo").equalTo(fullPhoneNumber);
                                    final String finalFullPhoneNumber = fullPhoneNumber;
                                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                Phone.setError("Account to this phone Number Already Exist");
                                            } else {
                                                editProfile.setText("Edit Profile");
                                                Phone.setFocusable(false);
                                                Phone.setFocusableInTouchMode(false);
                                                Phone.setClickable(false);
                                                DatabaseReference policeRef = FirebaseDatabase.getInstance().getReference("Users").child("Victim").child(sessionManager.getMobile());
                                                policeRef.removeValue();
                                                if (sessionManager.getProfileURL() == null || sessionManager.getProfileURL().isEmpty()) {
                                                    sessionManager.setProfileURL("");
                                                }
                                                sessionManager.setMobile(finalFullPhoneNumber);
                                                DatabaseHelper help = new DatabaseHelper(Phone.getText().toString(), fullName.getText().toString(), adharNo.getText().toString(), Address.getText().toString(), Gender.getText().toString(), Age.getText().toString(), sessionManager.getProfileURL());
                                                DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
                                                db.child("Victim").child(finalFullPhoneNumber).setValue(help);

                                                Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getActivity(),DrawerActivity.class));
                                            }
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

                }
            }
        });
        return root;
    }

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;

    private void openFileChooser() {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            img.setImageURI(imageUri);
        }

    }

    private void uploadDetails() {

        final SessionManager sessionManager = new SessionManager(getActivity());

        if (imageUri != null) {

            progressDialog.setTitle("Please Wait...");
            progressDialog.setMessage("Profile Updating..");
            progressDialog.show();

            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Profile_Images").child(sessionManager.getMobile());
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

            byte[] data = byteArrayOutputStream.toByteArray();

            UploadTask uploadTask = filepath.putBytes(data);


            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Victim").child(sessionManager.getMobile());

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //String downloadURL1 = taskSnapshot.getStorage().getDownloadUrl().toString();
                    String downloadURL = "gs://janrakshak-app-new.appspot.com/Profile_Images/" + sessionManager.getMobile();
                    final Map map = new HashMap();

                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(downloadURL);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sessionManager.setProfileURL(uri.toString());
                            map.put("profileURL", uri.toString());
                            reference.updateChildren(map);
                        }
                    });
                    progressDialog.dismiss();
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    getActivity().finish();

                }
            });


        } else {
            progressDialog.dismiss();
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