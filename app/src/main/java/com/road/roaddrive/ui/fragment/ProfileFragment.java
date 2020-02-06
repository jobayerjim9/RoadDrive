package com.road.roaddrive.ui.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.road.roaddrive.R;
import com.road.roaddrive.model.DriverProfile;
import com.road.roaddrive.ui.activity.SignInActivity;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private DriverProfile driverProfile;
    private TextView balanceText, totalEarnText, mobileNumberText, addressText, passwordText, nameText;
    private Context context;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_profile, container, false);
        balanceText = v.findViewById(R.id.balanceText);
        totalEarnText = v.findViewById(R.id.totalEarnText);
        mobileNumberText = v.findViewById(R.id.mobileNumberText);
        addressText = v.findViewById(R.id.addressText);
        nameText = v.findViewById(R.id.nameText);
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("DriverProfile").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                driverProfile = dataSnapshot.getValue(DriverProfile.class);
                if (driverProfile != null) {
                    nameText.setText(driverProfile.getName());
                    mobileNumberText.setText(driverProfile.getMobile());
                    String placeHolder = driverProfile.getBalanceProfile().getCurrentBalance() + " BDT";
                    balanceText.setText(placeHolder);
                    placeHolder = driverProfile.getBalanceProfile().getTotalEarn() + " BDT";
                    totalEarnText.setText(placeHolder);
                    addressText.setText(driverProfile.getAddress());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        addressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAddress();
            }
        });

        CardView logoutCard=v.findViewById(R.id.logoutCard);
        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(context, SignInActivity.class));
                Objects.requireNonNull(getActivity()).finish();
            }
        });
        return v;
    }


    private void updateAddress() {
        final FlatDialog flatDialog = new FlatDialog(context);
        flatDialog.setTitle("Enter Your Address")
                .setFirstTextFieldHint("Dhaka, Bangladesh")
                .setFirstButtonText("Ok!")
                .setSecondButtonText("Cancel")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setCancelable(true);
                        progressDialog.setMessage("Updating Your Address");
                        progressDialog.show();
                        DatabaseReference profile = FirebaseDatabase.getInstance().getReference().child("AgentProfile").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                        String mobile = flatDialog.getFirstTextField().trim();

                        profile.child("address").setValue(mobile).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    flatDialog.dismiss();
                                    Toast.makeText(context, "Successfully Updated!", Toast.LENGTH_SHORT).show();
                                } else {
                                    flatDialog.dismiss();
                                    Toast.makeText(context, "Try Again Later!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flatDialog.dismiss();
                    }
                })
                .isCancelable(false)
                .show();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
