package com.example.hackathonoriginal.ui.slideshow;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hackathonoriginal.Databases.SessionManager;
import com.example.hackathonoriginal.R;
import com.example.hackathonoriginal.Victim;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        loadLocale();


        SessionManager sessionManager= new SessionManager(getActivity());
        mDatabase= FirebaseDatabase.getInstance().getReference().child("VictimHistory").child(sessionManager.getMobile());
        mDatabase.keepSynced(true);
        mCardView= root.findViewById(R.id.myrecyclerview);
        mCardView.setHasFixedSize(true);
        mCardView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }

    private RecyclerView mCardView;
    private DatabaseReference mDatabase;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Victim,VictimViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Victim, VictimViewHolder>(Victim.class,R.layout.cardview,VictimViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(VictimViewHolder victimViewHolder, Victim victim, int i) {
                victimViewHolder.setCases( "Case : "+ victim.getCases());
                victimViewHolder.setDateComplete("Request Complete : "+victim.getDateComplete());
                victimViewHolder.setDateReq( "Request Time : "+victim.getDateReq());
                victimViewHolder.setVictimName( "Police Name : "+victim.getPoliceName());
                //victimViewHolder.setVictimPhone("Victim Phone : "+victim.getVictimPhone());
            }
        };
        mCardView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class VictimViewHolder extends RecyclerView.ViewHolder{
        View nView;
        public VictimViewHolder(View itemView)
        {
            super(itemView);
            nView=itemView;
        }
        void setCases(String cases)
        {
            TextView post_cases=(TextView)nView.findViewById(R.id.post_cases);
            post_cases.setText(cases);
        }
        void setDateComplete(String dateComplete)
        {
            TextView post_dateComplete=(TextView)nView.findViewById(R.id.post_dateComplete);
            post_dateComplete.setText(dateComplete);
        }
        public void setDateReq(String dateReq)
        {
            TextView post_dateReq=(TextView)nView.findViewById(R.id.post_dateReq);
            post_dateReq.setText(dateReq);
        }
        public void setVictimName(String victimName)
        {
            TextView post_victimName=(TextView)nView.findViewById(R.id.post_victimName);
            post_victimName.setText(victimName);
        }
     /*   public void setVictimPhone(String victimPhone)
        {
            TextView post_victimPhone=(TextView)nView.findViewById(R.id.post_victimPhone);
            post_victimPhone.setText(victimPhone);
        }*/
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