package com.road.roaddrive.ui.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.road.roaddrive.R;
import com.road.roaddrive.controller.adapter.BikeHomeAdapter;
import com.road.roaddrive.controller.adapter.CarHomeAdapter;
import com.road.roaddrive.controller.adapter.MicroHomeAdapter;
import com.road.roaddrive.controller.adapter.TruckHomeAdapter;
import com.road.roaddrive.model.BikeDataModel;
import com.road.roaddrive.model.CarDataModel;
import com.road.roaddrive.model.DriverProfile;
import com.road.roaddrive.model.MicroDataModel;
import com.road.roaddrive.model.TruckDataModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView homeRecycler;
    Date currentTime;
    private ArrayList<TruckDataModel> truckDataModels=new ArrayList<>();
    private ArrayList<CarDataModel> carDataModels=new ArrayList<>();
    private ArrayList<MicroDataModel> microDataModels=new ArrayList<>();
    private ArrayList<BikeDataModel> bikeDataModels=new ArrayList<>();
    private TruckHomeAdapter truckHomeAdapter;
    private CarHomeAdapter carHomeAdapter;
    private MicroHomeAdapter microHomeAdapter;
    private BikeHomeAdapter bikeHomeAdapter;
    private Context context;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);
        homeRecycler=v.findViewById(R.id.homeRecycler);
        currentTime = Calendar.getInstance().getTime();
        homeRecycler.setLayoutManager(new LinearLayoutManager(context));
        truckHomeAdapter=new TruckHomeAdapter(context,truckDataModels);
        carHomeAdapter=new CarHomeAdapter(context,carDataModels);
        microHomeAdapter=new MicroHomeAdapter(context,microDataModels);
        Log.d("CurrentDate",currentTime.getDate()+" "+currentTime.getMonth()+" "+currentTime.getYear());
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("DriverProfile").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DriverProfile driverProfile=dataSnapshot.getValue(DriverProfile.class);
                if(driverProfile!=null)
                {
                    bikeHomeAdapter=new BikeHomeAdapter(context,bikeDataModels,driverProfile);
                    if(driverProfile.getDriverType().equals("Truck"))
                    {
                        homeRecycler.setAdapter(truckHomeAdapter);
                        getTruckData();
                    }
                    else if(driverProfile.getDriverType().equals("Car"))
                    {
                        homeRecycler.setAdapter(carHomeAdapter);
                        getCarData();
                    }
                    else if(driverProfile.getDriverType().equals("Micro"))
                    {
                        homeRecycler.setAdapter(microHomeAdapter);
                        getMicroData();
                    }
                    else
                    {
                        homeRecycler.setAdapter(bikeHomeAdapter);
                        getBikeData(driverProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        return v;
    }

    private void getBikeData(DriverProfile driverProfile) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("BidPosts").child(driverProfile.getDriverType());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot truckPost:dataSnapshot.getChildren())
                {
                    BikeDataModel bikeDataModel=truckPost.getValue(BikeDataModel.class);
                    if(bikeDataModel!=null)
                    {
                        if(bikeDataModel.getTimeStamp().getDay()>=currentTime.getDate() && bikeDataModel.getTimeStamp().getMonth()>=(currentTime.getMonth()+1) && bikeDataModel.getTimeStamp().getYear()>=currentTime.getYear()) {
                            bikeDataModel.setKey(truckPost.getKey());
                            bikeDataModels.add(bikeDataModel);
                            bikeHomeAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getMicroData() {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("BidPosts").child("Micro");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot truckPost:dataSnapshot.getChildren())
                {
                    MicroDataModel microDataModel=truckPost.getValue(MicroDataModel.class);
                    if(microDataModel!=null)
                    {
                        if(microDataModel.getTimeStamp().getDay()>=currentTime.getDate() && microDataModel.getTimeStamp().getMonth()>=(currentTime.getMonth()+1) && microDataModel.getTimeStamp().getYear()>=currentTime.getYear()) {
                            microDataModel.setKey(truckPost.getKey());
                            microDataModels.add(microDataModel);
                            microHomeAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCarData() {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("BidPosts").child("Car");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot truckPost:dataSnapshot.getChildren())
                {
                    CarDataModel carDataModel=truckPost.getValue(CarDataModel.class);
                    if(carDataModel!=null)
                    {
                        if(carDataModel.getTimeStamp().getDay()>=currentTime.getDate() && carDataModel.getTimeStamp().getMonth()>=(currentTime.getMonth()+1) && carDataModel.getTimeStamp().getYear()>=currentTime.getYear()) {
                            carDataModel.setKey(truckPost.getKey());
                            carDataModels.add(carDataModel);
                            carHomeAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTruckData() {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("BidPosts").child("Truck");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot truckPost:dataSnapshot.getChildren())
                {
                    TruckDataModel truckDataModel=truckPost.getValue(TruckDataModel.class);
                    if(truckDataModel!=null)
                    {
                        if(truckDataModel.getTimeStamp().getDay()>=currentTime.getDate() && truckDataModel.getTimeStamp().getMonth()>=(currentTime.getMonth()+1) && truckDataModel.getTimeStamp().getYear()>=currentTime.getYear()) {

                            truckDataModel.setKey(truckPost.getKey());
                            truckDataModels.add(truckDataModel);
                            truckHomeAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }
}
