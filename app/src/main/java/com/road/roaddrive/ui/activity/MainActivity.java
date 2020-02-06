package com.road.roaddrive.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.road.roaddrive.R;
import com.road.roaddrive.controller.adapter.BottomTabAdapter;
import com.road.roaddrive.controller.helper.MyViewPager;
import com.road.roaddrive.model.AppData;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    SmoothBottomBar bottomBar;
    MyViewPager bottomViewPager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomBar=findViewById(R.id.bottomBar);
        DatabaseReference driverLocation= FirebaseDatabase.getInstance().getReference("Locations").child("Driver");
        GeoFire geoFire = new GeoFire(driverLocation);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest=new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    try {
                        geoFire.setLocation(FirebaseAuth.getInstance().getUid(), new GeoLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                driverLocation.child(FirebaseAuth.getInstance().getUid()).child("type").setValue(AppData.getDriverProfile().getDriverType());
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
        bottomViewPager=findViewById(R.id.bottomViewPager);
        BottomTabAdapter bottomTabAdapter=new BottomTabAdapter(getSupportFragmentManager(),3);
        bottomViewPager.setAdapter(bottomTabAdapter);
        bottomViewPager.setPagingEnabled(false);
        bottomViewPager.setCurrentItem(0);
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelect(int i) {
                Log.d("TabID",i+"");

                bottomViewPager.setCurrentItem(i);
            }
        });
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("LiveTrip");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("ChildAdded",s + " "+dataSnapshot.getKey());
                if (dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                    Log.d("ChildAdded",s + " "+dataSnapshot.getKey());
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        assert v != null;
                        v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        assert v != null;
                        v.vibrate(1000);
                    }
                    startActivity(new Intent(MainActivity.this, FixedTripActivity.class));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
