package com.road.roaddrive.ui.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.road.roaddrive.R;
import com.road.roaddrive.model.BidDetailsModel;
import com.road.roaddrive.model.TruckDataModel;
import com.road.roaddrive.model.UserProfile;
import com.road.roaddrive.ui.activity.BidTripActivity;
import com.road.roaddrive.ui.activity.BidWonActivity;
import com.road.roaddrive.ui.activity.FixedTripActivity;
import com.road.roaddrive.ui.activity.TripActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {
    private boolean noDouble=true;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_menu, container, false);
        CardView bidWonCard=v.findViewById(R.id.bidWonCard);
        bidWonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BidWonActivity.class));
            }
        });
        CardView tripCard=v.findViewById(R.id.tripCard);
        tripCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noDouble=true;
                loadTripDetails();
            }
        });
        CardView fixTripCard=v.findViewById(R.id.fixTripCard);
        fixTripCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FixedTripActivity.class));
            }
        });
        CardView helplineCard=v.findViewById(R.id.helplineCard);
        helplineCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri u = Uri.parse("tel:" + "+8801889992271" );

                // Create the intent and set the data for the
                // intent as the phone number.
                Intent i = new Intent(Intent.ACTION_DIAL, u);

                try {
                    startActivity(i);
                } catch (SecurityException s) {
                    Toast.makeText(getContext(), s.getLocalizedMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        return v;
    }



    private void loadTripDetails() {
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading Your Trip");
        progressDialog.show();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("DriverProfile").child(FirebaseAuth.getInstance().getUid()).child("RunningTrip");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Running trip Found!", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    if(dataSnapshot1.getKey()!=null)
                    {
                        String key=dataSnapshot1.getKey();
                        Log.d("Key",key);
                        DatabaseReference truck=FirebaseDatabase.getInstance().getReference("BidPosts").child("Truck").child(key);
                        truck.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final TruckDataModel truckDataModel=dataSnapshot.getValue(TruckDataModel.class);
                                if(truckDataModel!=null)
                                {
                                    DatabaseReference bid=truck.child("DriversBid").child(FirebaseAuth.getInstance().getUid());
                                    bid.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            final BidDetailsModel bidDetailsModel=dataSnapshot.getValue(BidDetailsModel.class);
                                            if (bidDetailsModel!=null)
                                            {
                                                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("user").child("UserProfile").child(truckDataModel.getUid());
                                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        final UserProfile userProfile=dataSnapshot.getValue(UserProfile.class);
                                                        if (userProfile!=null)
                                                        {
                                                            if (noDouble) {
                                                                noDouble=false;
                                                                progressDialog.dismiss();
                                                                final Intent intent = new Intent(getContext(), BidTripActivity.class);
                                                                intent.putExtra("fare", bidDetailsModel.getAmount());
                                                                intent.putExtra("riderName", userProfile.getName());
                                                                intent.putExtra("riderMobile", userProfile.getMobile());
                                                                intent.putExtra("sourceLat", truckDataModel.getSource().getLat());
                                                                intent.putExtra("sourceLng", truckDataModel.getSource().getLng());
                                                                intent.putExtra("desLat", truckDataModel.getDestination().getLat());
                                                                intent.putExtra("desLng", truckDataModel.getDestination().getLng());
                                                                startActivity(intent);
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

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressDialog.dismiss();
                                Log.d("TripActivity",databaseError.getDetails());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TripActivity",databaseError.getDetails());
                progressDialog.dismiss();
            }
        });
    }

}
