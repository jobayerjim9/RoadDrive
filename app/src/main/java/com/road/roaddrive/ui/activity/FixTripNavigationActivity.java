package com.road.roaddrive.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.road.roaddrive.R;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FixTripNavigationActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener, NavigationListener {

    private LatLng current;
    private MapView mapView;
    private MapboxMap mapboxMap;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private FusedLocationProviderClient fusedLocationClient;
    private FusedLocationProviderClient lastLocation;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private LocationEngine locationEngine;
    private LocationCallback locationCallback;
    private boolean loop=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.NavigationViewLight);
        Mapbox.getInstance(this, getString(R.string.access_token));
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trip);
        lastLocation= LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient=LocationServices.getFusedLocationProviderClient(this);


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }


    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        return false;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                double sourceLat=getIntent().getDoubleExtra("sourceLat",0);
                double sourceLng=getIntent().getDoubleExtra("sourceLng",0);
                double desLat=getIntent().getDoubleExtra("desLat",0);
                double desLng=getIntent().getDoubleExtra("desLng",0);
                Log.d("SourceDestination",sourceLat+" "+sourceLng);
                Point source=Point.fromLngLat(sourceLng,sourceLat);
                Point destination=Point.fromLngLat(desLng,desLat);
                LatLng src=new LatLng(sourceLat,sourceLng);
                LatLng des=new LatLng(desLat,desLng);
                getRoute(source,destination);
                // LatLngBounds.Builder builder = new LatLngBounds.Builder();
                LatLngBounds.Builder builder=new LatLngBounds.Builder();
                builder.include(src);
                builder.include(des);
                int padding = 50;
                LatLngBounds bounds = builder.build();
                final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mapboxMap.moveCamera(cu);


            }
        });
    }



    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            locationEngine=locationComponent.getLocationEngine();
            // Set the component's camera mode

            // current=new LatLng(locationComponent.getLastKnownLocation().getLatitude(),locationComponent.getLastKnownLocation().getLongitude());
            locationComponent.setCameraMode(CameraMode.TRACKING);
            LocationEngineRequest locationEngineRequest = new LocationEngineRequest.Builder(1000)
                    .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                    .setMaxWaitTime(5000)
                    .build();
            locationEngine.requestLocationUpdates(locationEngineRequest, new LocationEngineCallback<LocationEngineResult>() {
                @Override
                public void onSuccess(LocationEngineResult result) {
                    result.getLastLocation();
                }

                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            }, Looper.getMainLooper());
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(Objects.requireNonNull(mapboxMap.getStyle()));
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    private void getRoute(Point origin, Point destination) {
        lastLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Point curr=Point.fromLngLat(location.getLongitude(),location.getLatitude());
                NavigationRoute.builder(FixTripNavigationActivity.this)
                        .accessToken(getString(R.string.access_token))
                        .origin(curr)
                        .addWaypoint(origin)
                        .enableRefresh(true)
                        .profile(DirectionsCriteria.PROFILE_DRIVING)
                        .destination(destination)
                        .build()
                        .getRoute(new Callback<DirectionsResponse>() {
                            @Override
                            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                                // You can get the generic HTTP info about the response
                                Log.d(TAG, "Response code: " + response.code());
                                if (response.body() == null) {
                                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                    return;
                                } else if (response.body().routes().size() < 1) {
                                    Log.e(TAG, "No routes found");
                                    return;
                                }

                                currentRoute = response.body().routes().get(0);

                                // Draw the route on the map



//
//                            navigation.addProgressChangeListener(new ProgressChangeListener() {
//                                @Override
//                                public void onProgressChange(Location location, RouteProgress routeProgress) {
//                                    Log.d("RouteProgress",routeProgress.distanceRemaining()+"");
//                                    Toast.makeText(TripActivity.this, routeProgress.distanceRemaining() + "", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            navigationMapRoute.addProgressChangeListener(navigation);
//
//                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
//                                .directionsRoute(currentRoute)
//                                .shouldSimulateRoute(true)
//                                .build();

                                //navigation.setLocationEngine(locationEngine);

                                navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                                navigationMapRoute.addRoute(currentRoute);
                                LocationRequest locationRequest=new LocationRequest();
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationRequest.setInterval(2000);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        if (locationResult != null) {

                                            double dist=distance(destination.latitude(),destination.longitude(),locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());
                                            Log.d("LastLocation",dist+"");
                                            if(dist<0.1 && loop)
                                            {
                                                startActivity(new Intent(FixTripNavigationActivity.this,TripEndActivity.class));
                                                loop=false;
                                                finish();
                                            }
                                        }
                                    }
                                };
                                fusedLocationClient.requestLocationUpdates(locationRequest,
                                        locationCallback,
                                        Looper.getMainLooper());


                                NavigationLauncherOptions options1=NavigationLauncherOptions.builder()
                                        .directionsRoute(currentRoute)
                                        .waynameChipEnabled(true)
                                        .build();

                                NavigationLauncher.startNavigation(FixTripNavigationActivity.this,options1);

//// Call this method with Context from within an Activity
//                       // navigationView.startNavigation(options);
//                        NavigationLauncher.startNavigation(TripActivity.this, options);
                            }

                            @Override
                            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                                Log.e(TAG, "Error: " + throwable.getMessage());
                            }
                        });
            }
        });

    }

    @Override
    public void onCancelNavigation() {

    }

    @Override
    public void onNavigationFinished() {
        Log.d(TAG,"Finished");
        Toast.makeText(this, "Finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNavigationRunning() {

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

