package com.property.david.tp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.property.david.tp.Fragments.MapFragment;
import com.property.david.tp.Fragments.RecentFragment;
import com.property.david.tp.Fragments.SearchFragment;
import com.property.david.tp.Models.Mark;
import com.property.david.tp.Models.Topic;
import com.property.david.tp.Utils.FontManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity" ;
    private LinearLayout layoutRecent;
    private TextView txtIconRecent;
    private TextView txtRecent;
    private LinearLayout layoutSearch;
    private TextView txtIconSearch;
    private TextView txtSearch;
    public ProgressBar progressBar;
    public RecentFragment recentFragment;
    public SearchFragment searchFragment;
    public MapFragment mapFragment;
    public List<Topic> allTopicList;
    public List<Topic> myTopicList;

    private Uri data = null;
    private String dynamicUrl = null;
    private String defaultTopicTitle = null;
    private Topic defaultTopic = null;

    public DatabaseReference mDatabase;

    public String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        if (intent != null) {
            data = intent.getData();
            if (data != null)
                dynamicUrl = data.toString();
        }

        if (dynamicUrl != null) {
            Log.d("--------", dynamicUrl);
            defaultTopicTitle = dynamicUrl.substring(75, dynamicUrl.length());
        }

        findViews();

        setInit();

        setEvents();

        getSupportFragmentManager().beginTransaction().replace(R.id.frgMain, searchFragment).commit();

        if (defaultTopicTitle != null) {

            progressBar.setVisibility(View.VISIBLE);

            mDatabase.child("topic").child(defaultTopicTitle).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    defaultTopic = new Topic();
                    defaultTopic.setTitle(defaultTopicTitle);

                    List<Mark> currentMarkList = new ArrayList<>();

                    for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {

                        Mark currentMark = new Mark();
                        String currentUserID = dataSnapshotChild.getKey();
                        currentMark.setUserId(currentUserID);

                        String currentMarkDate = dataSnapshotChild.child("date").getValue(String.class);
                        currentMark.setDate(currentMarkDate);

                        String[] currentLatLog = dataSnapshotChild.child("location").getValue(String.class).split(",");
                        currentMark.setLatitude(Double.valueOf(currentLatLog[0]));
                        currentMark.setLongitude(Double.valueOf(currentLatLog[1]));

                        long currentMarkDateValue = Long.valueOf(currentMark.getDate());
                        long currentDateValue = System.currentTimeMillis() / 1000;

                        if ((currentDateValue - currentMarkDateValue) < ONE_DAY_SECONDS ) {

                            currentMarkList.add(currentMark);
                            if (currentUserID.equals(user_id)) {
                                defaultTopic.setMyMark(currentMark);
                            }
                        }

                    }
                    defaultTopic.setMarkList(currentMarkList);

                    mapFragment.setTopic(defaultTopic);
                    getSupportFragmentManager().beginTransaction().add(R.id.frgMain, mapFragment).commit();
                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void findViews() {
        txtIconRecent = findViewById(R.id.txtIconRecent_Main);
        txtRecent = findViewById(R.id.txtRecent_Main);
        layoutRecent = findViewById(R.id.layoutRecent_Main);
        txtIconSearch = findViewById(R.id.txtIconSearch_Main);
        txtSearch = findViewById(R.id.txtSearch_Main);
        layoutSearch = findViewById(R.id.layoutSearch_Main);
        progressBar = findViewById(R.id.progressBar_Main);

        recentFragment = new RecentFragment();
        recentFragment.setMyActivity(this);
        searchFragment = new SearchFragment();
        searchFragment.setMyActivity(this);
        mapFragment = new MapFragment();
        mapFragment.setMyActivity(this);
    }

    @SuppressLint("HardwareIds")
    private void setInit(){
        FontManager.setFontType(findViewById(R.id.layoutMain_Main), iconFont);
        tryToRequestMarshmallowAccessFineLocationPermission();
        tryToRequestMarshmallowAccessCoarseLocationPermission();

        allTopicList = new ArrayList<>();
        myTopicList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        user_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        progressBar.setVisibility(View.GONE);

        refreshList();

    }

    private void setEvents() {

        layoutRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtIconRecent.setTextColor(getResources().getColor(R.color.colorMyBlue1));
                txtRecent.setTextColor(getResources().getColor(R.color.colorMyBlue1));
                txtIconSearch.setTextColor(getResources().getColor(R.color.colorMyGray_10));
                txtSearch.setTextColor(getResources().getColor(R.color.colorMyGray_10));

                getSupportFragmentManager().beginTransaction().replace(R.id.frgMain, recentFragment).commit();
            }
        });

        layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtIconRecent.setTextColor(getResources().getColor(R.color.colorMyGray_10));
                txtRecent.setTextColor(getResources().getColor(R.color.colorMyGray_10));
                txtIconSearch.setTextColor(getResources().getColor(R.color.colorMyBlue1));
                txtSearch.setTextColor(getResources().getColor(R.color.colorMyBlue1));

                getSupportFragmentManager().beginTransaction().replace(R.id.frgMain, searchFragment).commit();
            }
        });
    }

    public void refreshList() {

        progressBar.setVisibility(View.VISIBLE);

        mDatabase.child("topic").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                allTopicList.clear();
                myTopicList.clear();

                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                    Topic topic = new Topic();
                    topic.setTitle(dataSnapshotChild.getKey());

                    List<Mark> currentMarkList = new ArrayList<>();

                    for (DataSnapshot dataSnapshotGrandSon : dataSnapshotChild.getChildren()) {

                        Mark currentMark = new Mark();
                        String currentUserID = dataSnapshotGrandSon.getKey();
                        currentMark.setUserId(currentUserID);

                        String currentMarkDate = dataSnapshotGrandSon.child("date").getValue(String.class);
                        currentMark.setDate(currentMarkDate);

                        String[] currentLatLog = dataSnapshotGrandSon.child("location").getValue(String.class).split(",");
                        currentMark.setLatitude(Double.valueOf(currentLatLog[0]));
                        currentMark.setLongitude(Double.valueOf(currentLatLog[1]));

                        long currentMarkDateValue = Long.valueOf(currentMarkDate);
                        long currentDateValue = System.currentTimeMillis() / 1000;

                        if ((currentDateValue - currentMarkDateValue) < ONE_DAY_SECONDS ) {

                            currentMarkList.add(currentMark);
                            if (currentUserID.equals(user_id)) {
                                topic.setMyMark(currentMark);
                            }
                        }

                    }
                    topic.setMarkList(currentMarkList);

                    allTopicList.add(topic);
                    if (topic.getMyMark() != null)
                        myTopicList.add(topic);

                }

                recentFragment.refreshListView();
                searchFragment.refreshListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
//    private void deleteOldData() {
//        long before24TimeStamp = System.currentTimeMillis() / 1000 - 60 * 60 * 24;
//        Query queryRef = mDatabase.child("marks").orderByChild("date").endAt(before24TimeStamp);
//        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                long num = dataSnapshot.getChildrenCount();
//                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
//                    for (DataSnapshot dataSnapshotGrandSon : dataSnapshotChild.getChildren()) {
//                        String current_user_id = dataSnapshotGrandSon.getKey();
//                        if (current_user_id.equals(user_id)) {
//                            dataSnapshotGrandSon.getRef().removeValue();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }

}
