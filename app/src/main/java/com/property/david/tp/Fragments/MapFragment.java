package com.property.david.tp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.property.david.tp.BaseActivity;
import com.property.david.tp.MainActivity;
import com.property.david.tp.Models.Mark;
import com.property.david.tp.Models.Topic;
import com.property.david.tp.R;
import com.property.david.tp.SplashActivity;
import com.property.david.tp.Utils.FontManager;
import com.property.david.tp.Utils.GPSTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private boolean isSearch;
    private Button btnRtn;
    private Button btnShare;
    private TextView txtName;
    private TextView txtCount;
    private Button btnAdd;
    private ImageView imgSuccess;
    private SupportMapFragment mapFragment;

    private boolean isLoadedMap;
    private GoogleMap googleMap;

    private Typeface iconFont;

    private Topic topic;

    private MainActivity myActivity;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public void setMyActivity(MainActivity myActivity) {
        this.myActivity = myActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        findViews(view);

        setInit();

        setFormula(view);

        setEvents();

        return view;
    }

    private void findViews(View view){
        imgSuccess = view.findViewById(R.id.imgSuccess_MapFrg);
        btnAdd = view.findViewById(R.id.btnAdd_MapFrg);
        btnRtn = view.findViewById(R.id.btnRtn_MapFrg);
        btnShare = view.findViewById(R.id.btnShare_MapFrg);
        txtName = view.findViewById(R.id.txtName_MapFrg);
        txtCount = view.findViewById(R.id.txtCount_MapFrg);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    }

    private void setInit(){

        mapFragment.getMapAsync(this);
        isLoadedMap = false;

        if (topic != null)
            txtName.setText(topic.getTitle());
        iconFont = FontManager.getTypeface(myActivity, FontManager.FONTAWESOME);
        imgSuccess.setVisibility(View.GONE);
        btnAdd.setVisibility(View.VISIBLE);

    }

    private void setFormula(View view){
        FontManager.setFontType(view.findViewById(R.id.layoutMain_MapFrg), iconFont);
    }

    private void setEvents(){

        btnRtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myActivity.getSupportFragmentManager().beginTransaction().remove(MapFragment.this).commit();
            }
        });

        btnAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (isLoadedMap) {
                    // Add a marker in Sydney, Australia,
                    // and move the map's camera to the same location.

                    GPSTracker gpsTracker = new GPSTracker(myActivity);
                    double latitude = 0.0, longitude = 0.0;

                    // check if GPS enabled
                    if (gpsTracker.canGetLocation())
                    {
                        latitude = gpsTracker.getLatitude();
                        longitude=gpsTracker.getLongitude();
                    }
                    else
                    {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gpsTracker.showSettingsAlert();
                    }

                    LatLng current_LatLng = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions()
                            .position(current_LatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_mark)));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(current_LatLng));

                    String location = latitude + "," + longitude;

                    Map<String, Object> newMark = new HashMap<>();
                    newMark.put("location", location);
                    newMark.put("date", String.valueOf(System.currentTimeMillis() / 1000));
                    myActivity.mDatabase.child("topic").child(topic.getTitle()).child(myActivity.user_id).updateChildren(newMark);

                    imgSuccess.setVisibility(View.VISIBLE);
                    btnAdd.setVisibility(View.GONE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                        imgSuccess.setVisibility(View.GONE);
                        btnAdd.setVisibility(View.VISIBLE);
                        }

                    }, 1000);

                }
                return false;
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse(topic.getTitle()))
                        .setDynamicLinkDomain("z8abe.app.goo.gl")
                        // Open links with this app on Android
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                        // Open links with com.example.ios on iOS
                        .setIosParameters(new DynamicLink.IosParameters.Builder("com.bailang.tp").build())
                        .buildDynamicLink();

                Uri dynamicLinkUri = dynamicLink.getUri();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString());
//                startActivity(Intent.createChooser(sendIntent, topic.getTitle()));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isLoadedMap = true;
        this.googleMap = googleMap;
        displayMarks();
    }

    private void displayMarks() {
        myActivity.mDatabase.child("topic").child(topic.getTitle()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

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



                        LatLng mark_LatLng = new LatLng(currentMark.getLatitude(), currentMark.getLongitude());
                        long currentMarkDateValue = Long.valueOf(currentMarkDate);
                        long before24Date = System.currentTimeMillis() / 1000 - BaseActivity.ONE_DAY_SECONDS;
                        long currentDateValue = System.currentTimeMillis() / 1000;

                        if ((currentDateValue - currentMarkDateValue) < BaseActivity.ONE_DAY_SECONDS ) {

                            currentMarkList.add(currentMark);

                            if (currentUserID.equals(myActivity.user_id))
                                topic.setMyMark(currentMark);

                            int markSize = getMarkSize(currentMarkDateValue);
                            int alpa = 155 + (int) ((currentMarkDateValue - before24Date) * 100 / BaseActivity.ONE_DAY_SECONDS);
                            if (alpa > 255) alpa = 255;
                            googleMap.addMarker(new MarkerOptions()
                                    .position(mark_LatLng)
    //                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_mark)));
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("red_mark", markSize, markSize, alpa))));


                        }
                    }

                    txtCount.setText(String.valueOf(currentMarkList.size()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public Bitmap resizeMapIcons(String iconName,int width, int height, int alpa){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", myActivity.getPackageName()));

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);


        return makeTransparent(scaledBitmap, alpa);
    }

    public int getMarkSize(long markDate) {
        long before24Date = System.currentTimeMillis() / 1000 - BaseActivity.ONE_DAY_SECONDS;
        int a = (int) ((markDate - before24Date) / (60 * 24) + 20);
        return a * 2;
    }

    public Bitmap makeTransparent(Bitmap src, int value) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap transBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(transBitmap);
        canvas.drawARGB(0, 0, 0, 0);
        // config paint
        final Paint paint = new Paint();
        paint.setAlpha(value);
        canvas.drawBitmap(src, 0, 0, paint);
        return transBitmap;
    }
}