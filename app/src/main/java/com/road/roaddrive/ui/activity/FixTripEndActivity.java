package com.road.roaddrive.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.road.roaddrive.R;
import com.road.roaddrive.model.AgentProfile;
import com.road.roaddrive.model.DriverProfile;
import com.road.roaddrive.model.FixTripDetailsModel;

public class FixTripEndActivity extends AppCompatActivity {

    TextView tripEndMessage;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_end);
        tripEndMessage=findViewById(R.id.tripEndMessage);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Finishing Your Trip! Do Not Close App!");
        progressDialog.setCancelable(false);
        finishingTrip();

    }

    private void finishingTrip() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("LiveTrip").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FixTripDetailsModel fixTripDetailsModel=dataSnapshot.getValue(FixTripDetailsModel.class);
                if(fixTripDetailsModel!=null)
                {
                    final int amount=fixTripDetailsModel.getFare();
                    final double companyD= amount*0.2;
                    final int company= (int) companyD;
                    final double agentD=amount*0.05;
                    final int agent=(int) agentD;
                    final DatabaseReference profile=FirebaseDatabase.getInstance().getReference("DriverProfile").child(FirebaseAuth.getInstance().getUid());
                    profile.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DriverProfile driverProfile=dataSnapshot.getValue(DriverProfile.class);
                            if(driverProfile!=null)
                            {
                                double balance=driverProfile.getBalanceProfile().getCurrentBalance();
                                balance=balance-company;
                                DatabaseReference upBalance=profile.getRef();
                                upBalance.child("balanceProfile").child("currentBalance").setValue(balance);
                                double totalEarn=driverProfile.getBalanceProfile().getTotalEarn();
                                totalEarn=totalEarn+(amount-company);
                                upBalance.child("balanceProfile").child("totalEarn").setValue(totalEarn);
                                String agentUsername=driverProfile.getAgentUsername();
                                DatabaseReference agentRef=FirebaseDatabase.getInstance().getReference("AgentProfile");
                                agentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot dataSnapshot2:dataSnapshot.getChildren())
                                        {
                                            AgentProfile agentProfile=dataSnapshot2.getValue(AgentProfile.class);
                                            if (agentProfile!=null)
                                            {
                                                if(agentProfile.getUsername().equals(agentUsername))
                                                {
                                                    DatabaseReference agentRef=dataSnapshot2.getRef();
                                                    double bal=agentProfile.getBalanceProfile().getCurrentBalance();
                                                    bal=bal+agent;
                                                    agentRef.child("balanceProfile").child("currentBalance").setValue(bal);
                                                    double total=agentProfile.getBalanceProfile().getTotalEarn();
                                                    total=total+agent;
                                                    agentRef.child("balanceProfile").child("totalEarn").setValue(total);
                                                    profile.child("RunningTrip").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            String placeHolder="Trip Completed!\nCollect "+fixTripDetailsModel.getFare()+"/- From Customer!";
                                                            tripEndMessage.setText(placeHolder);
                                                            DatabaseReference live= FirebaseDatabase.getInstance().getReference("LiveTrip");
                                                            live.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                                                                    {
                                                                        FixTripDetailsModel temp=dataSnapshot1.getValue(FixTripDetailsModel.class);
                                                                        if(temp.getRequestorUid().equals(fixTripDetailsModel.getRequestorUid()))
                                                                        {
                                                                            DatabaseReference remove=dataSnapshot1.getRef();
                                                                            remove.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    if (progressDialog.isShowing()) {
                                                                                        progressDialog.dismiss();
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    });
                                                    return;
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
