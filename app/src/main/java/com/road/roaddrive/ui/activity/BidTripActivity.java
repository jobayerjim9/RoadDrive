package com.road.roaddrive.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.road.roaddrive.R;
import com.road.roaddrive.controller.helper.FetchURL;
import com.road.roaddrive.controller.helper.TaskLoadedCallback;

public class BidTripActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private String riderName,riderMobile;
    private int fare;
    private double sourceLat,sourceLng,desLat,desLng;
    private TextView riderNameText,fareText;
    private Polyline currentPolyline;
    private ImageView riderCallButton;
    private Button navigationButton;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private MarkerOptions source, destination;
    private GoogleMap mMap;
    private boolean loop=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_trip);
        fare=getIntent().getIntExtra("fare",0);
        riderName=getIntent().getStringExtra("riderName");
        riderMobile=getIntent().getStringExtra("riderMobile");
        sourceLat=getIntent().getDoubleExtra("sourceLat",0);
        sourceLng=getIntent().getDoubleExtra("sourceLng",0);
        desLat=getIntent().getDoubleExtra("desLat",0);
        desLng=getIntent().getDoubleExtra("desLng",0);
        riderNameText=findViewById(R.id.riderNameText);
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
        fareText=findViewById(R.id.fareText);
        source=new MarkerOptions().position(new LatLng(sourceLat,sourceLng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        destination=new MarkerOptions().position(new LatLng(desLat,desLng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        riderCallButton=findViewById(R.id.riderCallButton);
        navigationButton=findViewById(R.id.navigationButton);
        String place="Your Bidden Amount "+fare+" BDT";
        fareText.setText(place);
        riderNameText.setText(riderName);
        riderCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri u = Uri.parse("tel:" + riderMobile);
                // Create the intent and set the data for the
                // intent as the phone number.
                Intent i = new Intent(Intent.ACTION_DIAL, u);

                try {
                    startActivity(i);
                } catch (SecurityException s) {
                    Toast.makeText(BidTripActivity.this ,s.getLocalizedMessage(), Toast.LENGTH_LONG)
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
                    double dist=distance(desLat,desLng,locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());
                    Log.d("LastLocation",dist+"");
                    if(dist<0.1 && loop)
                    {
                        startActivity(new Intent(BidTripActivity.this,TripEndActivity.class));
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
                String uri="google.navigation:q="+desLat+","+desLng;
                Uri gmmIntentUri = Uri.parse(uri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        try {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.mapNearBy);
            mapFragment.getMapAsync(BidTripActivity.this);
            new FetchURL(BidTripActivity.this).execute(getUrl(source.getPosition(), destination.getPosition(), "driving"), "driving");


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
