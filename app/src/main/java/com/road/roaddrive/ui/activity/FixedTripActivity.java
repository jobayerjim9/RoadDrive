package com.road.roaddrive.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.road.roaddrive.model.FixTripDetailsModel;
import com.road.roaddrive.model.UserProfile;

public class FixedTripActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private GoogleMap mMap;
    private MarkerOptions source, destination;
    private Polyline currentPolyline;
    private TextView fareText,riderNameText;
    private Button acceptFixedTrip;
    private Button declineFixedTrip;
    DatabaseReference liveTrip;
    DatabaseReference liveTripCust;
    private ImageView riderCallButton;
    FixTripDetailsModel fixTripDetailsModel;
    FixTripDetailsModel fixTripDetailsModelCustomer;
    LinearLayout fixTripButton;
    Button navigationButton;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private CardView riderContactLayout;
    private boolean loop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_trip);
        fareText=findViewById(R.id.fareText);
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
        liveTrip= FirebaseDatabase.getInstance().getReference("LiveTrip").child(FirebaseAuth.getInstance().getUid());
        liveTripCust= FirebaseDatabase.getInstance().getReference("LiveTrip");
        acceptFixedTrip=findViewById(R.id.acceptFixedTrip);
        fixTripButton=findViewById(R.id.fixTripButton);
        riderCallButton=findViewById(R.id.riderCallButton);
        navigationButton=findViewById(R.id.navigationButton);
        riderContactLayout=findViewById(R.id.riderContactLayout);
        riderNameText=findViewById(R.id.riderNameText);
        declineFixedTrip=findViewById(R.id.declineFixedTrip);

        loadFixTripDetails();

        declineFixedTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveTrip.removeValue();
            }
        });
    }
    private void loadFixTripDetails() {
        liveTrip.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    Toast.makeText(FixedTripActivity.this, "No Trip Request!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    fixTripDetailsModel=dataSnapshot.getValue(FixTripDetailsModel.class);
                    if(fixTripDetailsModel!=null) {
                        liveTripCust= FirebaseDatabase.getInstance().getReference("LiveTrip").child(fixTripDetailsModel.getRequestorUid());
                        source=new MarkerOptions().position(new LatLng(fixTripDetailsModel.getSourceLat(),fixTripDetailsModel.getDesLng())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        destination=new MarkerOptions().position(new LatLng(fixTripDetailsModel.getDesLat(),fixTripDetailsModel.getDesLng())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        String place="Estimated Fare "+ fixTripDetailsModel.getFare()+" BDT";
                        DatabaseReference userProfile=FirebaseDatabase.getInstance().getReference("user").child("UserProfile").child(fixTripDetailsModel.getRequestorUid());

                        liveTripCust.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                fixTripDetailsModelCustomer=dataSnapshot.getValue(FixTripDetailsModel.class);
                                if (fixTripDetailsModelCustomer!=null)
                                {
                                    acceptFixedTrip.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            liveTrip.child("accept").setValue(true);
                                            DatabaseReference trip=FirebaseDatabase.getInstance().getReference("LiveTrip");
//                        .child(fixTripDetailsModel.getRequestorUid());
                                            trip.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (final DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                                                    {
                                                        final FixTripDetailsModel tripModel=dataSnapshot1.getValue(FixTripDetailsModel.class);
                                                        if (tripModel!=null)
                                                        {
                                                            final String key=dataSnapshot1.getKey();
                                                            if (key!=null && !key.equals(fixTripDetailsModel.getRequestorUid()) && !key.equals(FirebaseAuth.getInstance().getUid()))
                                                            {
                                                                final DatabaseReference remove=trip.child(key);
                                                                remove.removeValue();
                                                            }
                                                        }
                                                    }
                                                    if (!fixTripDetailsModelCustomer.isAccept())
                                                    {
                                                        trip.child(fixTripDetailsModel.getRequestorUid()).child("accept").setValue(true);
                                                        trip.child(fixTripDetailsModel.getRequestorUid()).child("driverId").setValue(FirebaseAuth.getInstance().getUid());

                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(FixedTripActivity.this, "Already Accepted By Someone Else!", Toast.LENGTH_SHORT).show();
                                                        liveTrip.removeValue();
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });



                                        }
                                    });
                                    userProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserProfile user=dataSnapshot.getValue(UserProfile.class);
                                            if (user!=null)
                                            {
                                                if(fixTripDetailsModelCustomer.isAccept())
                                                {
                                                    fixTripButton.setVisibility(View.GONE);
                                                    riderContactLayout.setVisibility(View.VISIBLE);
                                                    riderNameText.setText(user.getName());
                                                    riderCallButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Uri u = Uri.parse("tel:" + user.getMobile());
                                                            // Create the intent and set the data for the
                                                            // intent as the phone number.
                                                            Intent i = new Intent(Intent.ACTION_DIAL, u);

                                                            try {
                                                                startActivity(i);
                                                            } catch (SecurityException s) {
                                                                Toast.makeText(FixedTripActivity.this ,s.getLocalizedMessage(), Toast.LENGTH_LONG)
                                                                        .show();
                                                            }
                                                        }
                                                    });
                                                    LocationRequest locationRequest=new LocationRequest();
                                                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                                    locationRequest.setInterval(5000);
                                                    locationCallback = new LocationCallback() {
                                                        @Override
                                                        public void onLocationResult(LocationResult locationResult) {
                                                            if (locationResult != null) {
                                                                Log.d("MyLocationOnTrip",locationResult.getLastLocation().getLatitude()+" "+locationResult.getLastLocation().getLongitude());
                                                                double dist=distance(fixTripDetailsModelCustomer.getDesLat(),fixTripDetailsModelCustomer.getDesLng(),locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());
                                                                Log.d("LastLocation",dist+"");
                                                                if(dist<0.1 && loop)
                                                                {
                                                                    startActivity(new Intent(FixedTripActivity.this,FixTripEndActivity.class));
                                                                    loop=false;
                                                                    finish();
                                                                }
                                                            }
                                                        }
                                                    };
                                                    fusedLocationClient.requestLocationUpdates(locationRequest,
                                                            locationCallback,
                                                            Looper.getMainLooper());
                                                    navigationButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            String uri="google.navigation:q="+fixTripDetailsModelCustomer.getDesLat()+","+fixTripDetailsModelCustomer.getDesLng();
                                                            Uri gmmIntentUri = Uri.parse(uri);
                                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                            mapIntent.setPackage("com.google.android.apps.maps");
                                                            startActivity(mapIntent);

//                                                            Intent i=new Intent(FixedTripActivity.this,FixTripNavigationActivity.class);
//
//
//
//                                                            i.putExtra("sourceLat",fixTripDetailsModel.getSourceLat());
//                                                            i.putExtra("sourceLng",fixTripDetailsModel.getSourceLng());
//                                                            i.putExtra("desLat",fixTripDetailsModel.getDesLat());
//                                                            i.putExtra("desLng",fixTripDetailsModel.getDesLng());
//                                                            startActivity(i);
//                                                            finish();

                                                        }
                                                    });
                                                    navigationButton.setVisibility(View.VISIBLE);
                                                }
                                                else
                                                {
                                                    fixTripButton.setVisibility(View.VISIBLE);
                                                    riderContactLayout.setVisibility(View.GONE);
                                                }


                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        fareText.setText(place);
                        try {
                            MapFragment mapFragment = (MapFragment) getFragmentManager()
                                    .findFragmentById(R.id.mapNearBy);
                            mapFragment.getMapAsync(FixedTripActivity.this);
                            new FetchURL(FixedTripActivity.this).execute(getUrl(source.getPosition(), destination.getPosition(), "driving"), "driving");

                        }
                        catch (Exception e)
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
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
