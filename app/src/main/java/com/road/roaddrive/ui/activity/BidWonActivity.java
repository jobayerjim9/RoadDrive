package com.road.roaddrive.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.road.roaddrive.R;
import com.road.roaddrive.controller.adapter.BikeBidWonAdapter;
import com.road.roaddrive.controller.adapter.BikeHomeAdapter;
import com.road.roaddrive.controller.adapter.CarBidWonAdapter;
import com.road.roaddrive.controller.adapter.CarHomeAdapter;
import com.road.roaddrive.controller.adapter.MicroBidWonAdapter;
import com.road.roaddrive.controller.adapter.MicroHomeAdapter;
import com.road.roaddrive.controller.adapter.TruckBidWonAdapter;
import com.road.roaddrive.controller.adapter.TruckHomeAdapter;
import com.road.roaddrive.model.AppData;
import com.road.roaddrive.model.BidDetailsModel;
import com.road.roaddrive.model.BikeDataModel;
import com.road.roaddrive.model.CarDataModel;
import com.road.roaddrive.model.DriverProfile;
import com.road.roaddrive.model.MicroDataModel;
import com.road.roaddrive.model.TruckDataModel;

import java.util.ArrayList;
import java.util.Objects;

public class BidWonActivity extends AppCompatActivity {
    private RecyclerView bidWonRecycler;
    private TruckBidWonAdapter truckBidWonAdapter;
    private CarBidWonAdapter carBidWonAdapter;
    private MicroBidWonAdapter microBidWonAdapter;
    private BikeBidWonAdapter bikeBidWonAdapter;
    private ArrayList<TruckDataModel> truckDataModels=new ArrayList<>();
    private ArrayList<CarDataModel> carDataModels=new ArrayList<>();
    private ArrayList<MicroDataModel> microDataModels=new ArrayList<>();
    private ArrayList<BikeDataModel> bikeDataModels=new ArrayList<>();
    private DriverProfile driverProfile;
    private ArrayList<BidDetailsModel> bidDetailsModels=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_won);
        DatabaseReference profile=FirebaseDatabase.getInstance().getReference("DriverProfile").child(FirebaseAuth.getInstance().getUid());
        profile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                driverProfile=dataSnapshot.getValue(DriverProfile.class);
                if (driverProfile!=null) {
                    try {
                        bidWonRecycler = findViewById(R.id.bidWonRecycler);
                        if (driverProfile.getDriverType().equals("Truck")) {
                            setUpAsTruck();
                        } else if (driverProfile.getDriverType().equals("Car")) {
                            setUpAsCar();

                        } else if (driverProfile.getDriverType().equals("Micro")) {
                            setupAsMicro();
                        } else if (driverProfile.getDriverType().equals("Bike")) {
                            setupAsBike();
                        }
                        else
                        {
                            Toast.makeText(BidWonActivity.this, "Under Maintenance!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setupAsBike() {
        bikeBidWonAdapter=new BikeBidWonAdapter(this,bikeDataModels,bidDetailsModels);
        bidWonRecycler.setLayoutManager(new LinearLayoutManager(this));
        bidWonRecycler.setAdapter(bikeBidWonAdapter);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("BidPosts").child("Bike");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot truckPost:dataSnapshot.getChildren()) {
                    final BikeDataModel bikeDataModel = truckPost.getValue(BikeDataModel.class);
                    if (bikeDataModel != null) {
                        bikeDataModel.setKey(truckPost.getKey());
                        try {
                            if (bikeDataModel.getStatus().equals("Running")) {
                                DatabaseReference bidRef = truckPost.getRef().child("DriversBid").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                                bidRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        BidDetailsModel bidDetailsModel = dataSnapshot.getValue(BidDetailsModel.class);
                                        if (bidDetailsModel != null) {
                                            if (bidDetailsModel.isChoosed() != null) {

                                                if (bidDetailsModel.isChoosed()) {
                                                    bidDetailsModels.add(bidDetailsModel);
                                                    bikeDataModels.add(bikeDataModel);
                                                    bikeBidWonAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupAsMicro() {
        microBidWonAdapter=new MicroBidWonAdapter(this,microDataModels,bidDetailsModels);
        bidWonRecycler.setLayoutManager(new LinearLayoutManager(this));
        bidWonRecycler.setAdapter(microBidWonAdapter);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("BidPosts").child("Micro");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot truckPost:dataSnapshot.getChildren())
                {
                    final MicroDataModel microDataModel=truckPost.getValue(MicroDataModel.class);
                    if(microDataModel!=null) {
                        microDataModel.setKey(truckPost.getKey());
                        try {
                        if (microDataModel.getStatus().equals("Running")) {
                            DatabaseReference bidRef = truckPost.getRef().child("DriversBid").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                            bidRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    BidDetailsModel bidDetailsModel = dataSnapshot.getValue(BidDetailsModel.class);
                                    if (bidDetailsModel != null) {
                                        if (bidDetailsModel.isChoosed() != null) {

                                            if (bidDetailsModel.isChoosed()) {
                                                bidDetailsModels.add(bidDetailsModel);
                                                microDataModels.add(microDataModel);
                                                microBidWonAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpAsCar() {
        carBidWonAdapter=new CarBidWonAdapter(this,carDataModels,bidDetailsModels);
        bidWonRecycler.setLayoutManager(new LinearLayoutManager(this));
        bidWonRecycler.setAdapter(carBidWonAdapter);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("BidPosts").child("Car");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot truckPost:dataSnapshot.getChildren())
                {
                    final CarDataModel carDataModel=truckPost.getValue(CarDataModel.class);
                    if(carDataModel!=null) {
                        carDataModel.setKey(truckPost.getKey());
                        try {
                            if (carDataModel.getStatus().equals("Running")) {
                                DatabaseReference bidRef = truckPost.getRef().child("DriversBid").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                                bidRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        BidDetailsModel bidDetailsModel = dataSnapshot.getValue(BidDetailsModel.class);
                                        if (bidDetailsModel != null) {
                                            if (bidDetailsModel.isChoosed() != null) {

                                                if (bidDetailsModel.isChoosed()) {
                                                    bidDetailsModels.add(bidDetailsModel);
                                                    carDataModels.add(carDataModel);
                                                    carBidWonAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpAsTruck() {
        truckBidWonAdapter=new TruckBidWonAdapter(this,truckDataModels,bidDetailsModels);
        bidWonRecycler.setLayoutManager(new LinearLayoutManager(this));
        bidWonRecycler.setAdapter(truckBidWonAdapter);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("BidPosts").child("Truck");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot truckPost : dataSnapshot.getChildren()) {
                    final TruckDataModel truckDataModel = truckPost.getValue(TruckDataModel.class);
                    if (truckDataModel != null) {
                        truckDataModel.setKey(truckPost.getKey());
                        try {
                            if (truckDataModel.getStatus().equals("Running")) {
                                DatabaseReference bidRef = truckPost.getRef().child("DriversBid").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                                bidRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        BidDetailsModel bidDetailsModel = dataSnapshot.getValue(BidDetailsModel.class);
                                        if (bidDetailsModel != null) {
                                            if (bidDetailsModel.isChoosed() != null) {

                                                if (bidDetailsModel.isChoosed()) {
                                                    bidDetailsModels.add(bidDetailsModel);
                                                    truckDataModels.add(truckDataModel);
                                                    truckBidWonAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
