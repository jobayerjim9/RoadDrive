package com.road.roaddrive.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.road.roaddrive.R;
import com.road.roaddrive.controller.adapter.StatementAdapter;
import com.road.roaddrive.model.BalanceProfile;
import com.road.roaddrive.model.DriverProfile;
import com.road.roaddrive.model.StatementModel;
import com.softbd.aamarpay.PayByAamarPay;
import com.softbd.aamarpay.interfaces.OnPaymentRequestListener;
import com.softbd.aamarpay.model.OptionalFields;
import com.softbd.aamarpay.model.PaymentResponse;
import com.softbd.aamarpay.model.RequiredFields;
import com.softbd.aamarpay.utils.Params;

import java.util.ArrayList;
import java.util.Random;

public class StatementActivity extends AppCompatActivity {
    private ArrayList<StatementModel> statementModels=new ArrayList<>();
    private ArrayList<StatementModel> statementModelsUnpaid=new ArrayList<>();
    private StatementAdapter statementAdapter;
    private double dueAmountTotal=0;
    private DriverProfile driverProfile;
    Button payButton;
    BalanceProfile balanceProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);
        RecyclerView recyclerView =findViewById(R.id.statementRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        statementAdapter=new StatementAdapter(this,statementModels);
        recyclerView.setAdapter(statementAdapter);
        payButton=findViewById(R.id.payButton);
        payButton.setEnabled(false);
        getProfile();
    }

    private void getProfile() {
        DatabaseReference profileRef= FirebaseDatabase.getInstance().getReference().child("DriverProfile").child(FirebaseAuth.getInstance().getUid());
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DriverProfile driverProfile= dataSnapshot.getValue(DriverProfile.class);
                if (driverProfile!=null)
                {
                    balanceProfile=driverProfile.getBalanceProfile();
                    getStatementData(driverProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getStatementData(DriverProfile driverProfile) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Statements").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                statementModels.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    StatementModel statementModel=dataSnapshot1.getValue(StatementModel.class);
                    if (statementModel!=null)
                    {
                        statementModel.setId(dataSnapshot1.getKey());
                        statementModels.add(statementModel);
                        statementAdapter.notifyDataSetChanged();
                    }

                }
                for (StatementModel statementModel: statementModels) {
                    if (!statementModel.isPaid())
                    {
                        double due= statementModel.getAgentEarn()+ statementModel.getCompanyEarn();
                        dueAmountTotal=dueAmountTotal+due;
                        statementModelsUnpaid.add(statementModel);
                    }
                }
                payButton.setEnabled(true);
                String placeHolder= "Pay All ("+dueAmountTotal+"/-)";
                payButton.setText(placeHolder);

                payButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProgressDialog progressDialog=new ProgressDialog(StatementActivity.this);
                        progressDialog.setMessage("Paying Your Dues");
                        progressDialog.setCancelable(false);
                        RequiredFields requiredFields = new RequiredFields(driverProfile.getName(),driverProfile.getEmail(),driverProfile.getAddress(),"Dhaka",
                                "Dhaka","1216","Bangladesh",driverProfile.getMobile(),"No Desc",dueAmountTotal+"", Params.CURRENCY_BDT,statementModelsUnpaid.get(0).getId(),"road1971","b3f5920cd76890f16a3340265f8abae7","null","fail","cancel");
                        OptionalFields optionalFields=new OptionalFields();
                        DatabaseReference stateId = FirebaseDatabase.getInstance().getReference().child("Statements").child(FirebaseAuth.getInstance().getUid());
                        PayByAamarPay.getInstance(StatementActivity.this,requiredFields,optionalFields).payNow(new OnPaymentRequestListener() {
                            @Override
                            public void onPaymentResponse(int i, PaymentResponse paymentResponse) {

                                progressDialog.show();
                                Log.d("paymentResponse",paymentResponse.getPayStatus());
                                if (paymentResponse.getPayStatus().contains("Successful")) {
                                   DatabaseReference profile=FirebaseDatabase.getInstance().getReference("DriverProfile").child(FirebaseAuth.getInstance().getUid()).child("balanceProfile");
                                    double bal=balanceProfile.getCurrentBalance()+dueAmountTotal;
                                    balanceProfile.setCurrentBalance(bal);
                                    profile.setValue(balanceProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            for (StatementModel statementModel:statementModelsUnpaid) {
                                                stateId.child(statementModel.getId()).child("paid").setValue(true);
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });

                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(StatementActivity.this, paymentResponse.getError(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        });

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StatementActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
