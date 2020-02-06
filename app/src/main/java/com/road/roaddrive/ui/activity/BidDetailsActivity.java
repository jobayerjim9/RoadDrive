package com.road.roaddrive.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.road.roaddrive.R;
import com.road.roaddrive.controller.helper.FetchURL;
import com.road.roaddrive.controller.helper.TaskLoadedCallback;
import com.road.roaddrive.model.AppData;
import com.road.roaddrive.model.BidModel;
import com.road.roaddrive.model.BikeDataModel;
import com.road.roaddrive.model.CarDataModel;
import com.road.roaddrive.model.DriverProfile;
import com.road.roaddrive.model.MicroDataModel;
import com.road.roaddrive.model.TruckDataModel;

import java.util.Objects;

public class BidDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private DriverProfile driverProfile;
    private TruckDataModel truckDataModel;
    private CarDataModel carDataModel;
    private MicroDataModel microDataModel;
    private BikeDataModel bikeDataModel;
    private GoogleMap mMap;
    private MarkerOptions source, destination;
    private Polyline currentPolyline;
    private TextView tripStartsEnd,typeBid,itemLabourBid,totalVehicleBid,customerRequirementBid,averageBidText;
    private Button bidButton;
    private DatabaseReference postRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_details);
        driverProfile= AppData.getDriverProfile();
        tripStartsEnd=findViewById(R.id.tripStartsEnd);
        bidButton=findViewById(R.id.bidButton);
        typeBid=findViewById(R.id.typeBid);
        itemLabourBid=findViewById(R.id.itemLabourBid);
        totalVehicleBid=findViewById(R.id.totalVehicleBid);
        customerRequirementBid=findViewById(R.id.customerRequirementBid);
        averageBidText=findViewById(R.id.averageBidText);

        if(driverProfile.getDriverType().equals("Truck"))
        {
            truckDataModel=AppData.getTruckDataModel();
            setUpAsTruck();
        }
        else if(driverProfile.getDriverType().equals("Car"))
        {
            carDataModel=AppData.getCarDataModel();
            setUpAsCar();
        }
        else if (driverProfile.getDriverType().equals("Micro"))
        {
            microDataModel=AppData.getMicroDataModel();
            setUpAsMicro();
        }
        else
        {
            bikeDataModel=AppData.getBikeDataModel();
            setUpAsOther();
        }
        new FetchURL(this).execute(getUrl(source.getPosition(), destination.getPosition(), "driving"), "driving");
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(this);
        bidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bidOnThePost();
            }
        });
    }

    private void bidOnThePost() {
        switch (driverProfile.getDriverType()) {
            case "Truck":
                postRef = FirebaseDatabase.getInstance().getReference().child("BidPosts").child(driverProfile.getDriverType()).child(truckDataModel.getKey()).child("DriversBid").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                break;
            case "Car":
                postRef = FirebaseDatabase.getInstance().getReference().child("BidPosts").child(driverProfile.getDriverType()).child(carDataModel.getKey()).child("DriversBid").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                break;
            case "Micro":
                postRef = FirebaseDatabase.getInstance().getReference().child("BidPosts").child(driverProfile.getDriverType()).child(microDataModel.getKey()).child("DriversBid").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                break;
            default:
                postRef = FirebaseDatabase.getInstance().getReference().child("BidPosts").child(driverProfile.getDriverType()).child(bikeDataModel.getKey()).child("DriversBid").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                break;
        }
        final FlatDialog flatDialog=new FlatDialog(this);
        flatDialog.setTitle("Welcome To Bid!")
                .setSubtitle("Enter Your Bid Amount & Description")
                .withFirstTextField(true)
                .setFirstTextFieldHint("Enter Bid Amount")
                .setFirstTextFieldInputType(InputType.TYPE_CLASS_NUMBER)
                .withLargeText(true)
                .setLargeTextFieldHint("Additional Comment!")
                .setFirstButtonText("Bid!")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int amount;
                        String bidAmount=flatDialog.getFirstTextField().trim();
                        String comment=flatDialog.getLargeTextField();
                        if(bidAmount.isEmpty())
                        {
                            Toast.makeText(BidDetailsActivity.this, "Please Enter Correct Amount", Toast.LENGTH_SHORT).show();
                        }
                        else if(comment.isEmpty())
                        {
                            Toast.makeText(BidDetailsActivity.this, "Please Write Something About Your Bid", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            try {
                                final ProgressDialog progressDialog=new ProgressDialog(BidDetailsActivity.this);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("Bidding!");
                                progressDialog.show();
                                amount = Integer.parseInt(bidAmount);
                                BidModel bidModel = new BidModel(amount, comment);
                                postRef.setValue(bidModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(BidDetailsActivity.this, "Successfully Bid!", Toast.LENGTH_SHORT).show();
                                            bidButton.setEnabled(false);
                                            flatDialog.dismiss();
                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(BidDetailsActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(BidDetailsActivity.this, "Please Enter Correct Amount", Toast.LENGTH_SHORT).show();
                            }
                        }


                    }
                }).isCancelable(false).show();



    }

    private void setUpAsOther() {
        source=new MarkerOptions().position(new LatLng(bikeDataModel.getSource().getLat(),bikeDataModel.getSource().getLng())).title(bikeDataModel.getSourceName()+"(Start)").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        destination=new MarkerOptions().position(new LatLng(bikeDataModel.getDestination().getLat(),bikeDataModel.getDestination().getLng())).title(bikeDataModel.getDestinationName()+"(End)").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        String placeHolder="Trip Starts "+ bikeDataModel.getTimeStamp().getDay()+"/"+ bikeDataModel.getTimeStamp().getMonth()+"/"+bikeDataModel.getTimeStamp().getYear()+" At "+ bikeDataModel.getTimeStamp().getHours()+":"+bikeDataModel.getTimeStamp().getMinute();
        tripStartsEnd.setText(placeHolder);
        typeBid.setVisibility(View.GONE);
        placeHolder="Required For " + bikeDataModel.getHoursType();
        itemLabourBid.setText(placeHolder);
        placeHolder="Total "+bikeDataModel.getBikeRequired()+" "+driverProfile.getDriverType()+" Required";
        totalVehicleBid.setText(placeHolder);
        placeHolder="Customer: "+bikeDataModel.getAdditional();
        customerRequirementBid.setText(placeHolder);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("BidPosts").child(driverProfile.getDriverType()).child(bikeDataModel.getKey()).child("DriversBid").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    bidButton.setEnabled(false);
                    bidButton.setText("Already Bidden!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpAsMicro() {
        source=new MarkerOptions().position(new LatLng(microDataModel.getSource().getLat(),microDataModel.getSource().getLng())).title(microDataModel.getSourceName()+"(Start)").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        destination=new MarkerOptions().position(new LatLng(microDataModel.getDestination().getLat(),microDataModel.getDestination().getLng())).title(microDataModel.getDestinationName()+"(End)").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        String placeHolder="Trip Starts "+ microDataModel.getTimeStamp().getDay()+"/"+ microDataModel.getTimeStamp().getMonth()+"/"+microDataModel.getTimeStamp().getYear()+" At "+ microDataModel.getTimeStamp().getHours()+":"+microDataModel.getTimeStamp().getMinute();
        tripStartsEnd.setText(placeHolder);
        placeHolder="Must Be "+microDataModel.getMicroType();
        typeBid.setText(placeHolder);
        placeHolder="Required For "+microDataModel.getHoursType();
        itemLabourBid.setText(placeHolder);
        placeHolder="Total "+microDataModel.getMicroRequired()+" Micro Required";
        totalVehicleBid.setText(placeHolder);
        placeHolder="Customer: "+microDataModel.getAdditional();
        customerRequirementBid.setText(placeHolder);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("BidPosts").child(driverProfile.getDriverType()).child(microDataModel.getKey()).child("DriversBid").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    bidButton.setEnabled(false);
                    bidButton.setText("Already Bidden!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpAsCar() {
        source=new MarkerOptions().position(new LatLng(carDataModel.getSource().getLat(),carDataModel.getSource().getLng())).title(carDataModel.getSourceName()+"(Start)").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        destination=new MarkerOptions().position(new LatLng(carDataModel.getDestination().getLat(),carDataModel.getDestination().getLng())).title(carDataModel.getDestinationName()+"(End)").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        String placeHolder="Trip Starts "+ carDataModel.getTimeStamp().getDay()+"/"+ carDataModel.getTimeStamp().getMonth()+"/"+carDataModel.getTimeStamp().getYear()+" At "+ carDataModel.getTimeStamp().getHours()+":"+carDataModel.getTimeStamp().getMinute();
        tripStartsEnd.setText(placeHolder);
        placeHolder="Must Be "+carDataModel.getCarType();
        typeBid.setText(placeHolder);
        placeHolder="Required For " + carDataModel.getHoursRequired();
        itemLabourBid.setText(placeHolder);
        placeHolder="Total "+carDataModel.getCarRequired()+" Car Required";
        totalVehicleBid.setText(placeHolder);
        placeHolder="Customer: "+carDataModel.getDetails();
        customerRequirementBid.setText(placeHolder);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("BidPosts").child(driverProfile.getDriverType()).child(carDataModel.getKey()).child("DriversBid").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    bidButton.setEnabled(false);
                    bidButton.setText("Already Bidden!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpAsTruck() {
        source=new MarkerOptions().position(new LatLng(truckDataModel.getSource().getLat(),truckDataModel.getSource().getLng())).title(truckDataModel.getLoadLocation()+"(Start)").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        destination=new MarkerOptions().position(new LatLng(truckDataModel.getDestination().getLat(),truckDataModel.getDestination().getLng())).title(truckDataModel.getUnloadLocation()+"(End)").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        String placeHolder="Trip Starts "+ truckDataModel.getTimeStamp().getDay()+"/"+truckDataModel.getTimeStamp().getMonth()+"/"+truckDataModel.getTimeStamp().getYear()+" At "+ truckDataModel.getTimeStamp().getHours()+":"+truckDataModel.getTimeStamp().getMinute();
        tripStartsEnd.setText(placeHolder);
        placeHolder="Must Be "+truckDataModel.getVehicleType() +" & "+truckDataModel.getVehicleSize();
        typeBid.setText(placeHolder);
        placeHolder="Carry " + truckDataModel.getItemType()+" Item & Need "+truckDataModel.getLabour()+ " Labour!";
        itemLabourBid.setText(placeHolder);
        placeHolder="Total "+truckDataModel.getTotalTruck()+" Truck Required";
        totalVehicleBid.setText(placeHolder);
        placeHolder="Customer: "+truckDataModel.getProductDescription();
        customerRequirementBid.setText(placeHolder);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("BidPosts").child(driverProfile.getDriverType()).child(truckDataModel.getKey()).child("DriversBid").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    bidButton.setEnabled(false);
                    bidButton.setText("Already Bidden!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.api_key);
        return url;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        mMap.addMarker(source);
        mMap.addMarker(destination);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(source.getPosition());
        builder.include(destination.getPosition());
        int padding = 50;
        LatLngBounds bounds = builder.build();
        /**create the camera with bounds and padding to set into map*/
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setLatLngBoundsForCameraTarget(bounds);
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

    }
}
