package com.road.roaddrive.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import com.road.roaddrive.model.BidDetailsModel;
import com.road.roaddrive.model.DriverProfile;
import com.road.roaddrive.model.StatementModel;
import com.road.roaddrive.model.TruckDataModel;

public class TripEndActivity extends AppCompatActivity {
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
        else
        {
            progressDialog.dismiss();
        }
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("DriverProfile").child(FirebaseAuth.getInstance().getUid()).child("RunningTrip");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    if(dataSnapshot1.getKey()!=null)
                    {

                        String key=dataSnapshot1.getKey();
                        Log.d("Key",key);
                        DatabaseReference truck=FirebaseDatabase.getInstance().getReference("BidPosts").child("Truck").child(key);
                        truck.child("status").setValue("Completed");
                        truck.child("DriversBid").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                BidDetailsModel bidDetailsModel=dataSnapshot.getValue(BidDetailsModel.class);
                                if(bidDetailsModel!=null)
                                {
                                    int amount=bidDetailsModel.getAmount();
                                    double companyD= amount*0.2;
                                    int company= (int) companyD;
                                    double agentD=amount*0.05;
                                    int agent=(int) agentD;

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
                                                                    StatementModel statementModel=new StatementModel(amount,amount-companyD-agentD,companyD,agentD,FirebaseAuth.getInstance().getUid(),agentUsername);
                                                                    DatabaseReference stRef=FirebaseDatabase.getInstance().getReference().child("Statements").child(FirebaseAuth.getInstance().getUid());
                                                                    stRef.push().setValue(statementModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            profile.child("RunningTrip").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    String placeHolder="Trip Completed!\nCollect "+bidDetailsModel.getAmount()+"/- From Customer!";
                                                                                    tripEndMessage.setText(placeHolder);
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
                                                        Toast.makeText(TripEndActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                        tripEndMessage.setText(databaseError.getDetails());
                                                    }
                                                });

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(TripEndActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            tripEndMessage.setText(databaseError.getDetails());
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(TripEndActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                tripEndMessage.setText(databaseError.getDetails());
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TripActivity",databaseError.getDetails());
                Toast.makeText(TripEndActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                tripEndMessage.setText(databaseError.getDetails());
            }
        });
    }
}
