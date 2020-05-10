package com.road.roaddrive.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.road.roaddrive.R;
import com.road.roaddrive.model.BidDetailsModel;
import com.road.roaddrive.model.MicroDataModel;
import com.road.roaddrive.ui.activity.TripActivity;

import java.util.ArrayList;

public class MicroBidWonAdapter extends RecyclerView.Adapter<MicroBidWonAdapter.HomeViewHolder> {
    private Context context;
    private ArrayList<MicroDataModel> microDataModels;
    private ArrayList<BidDetailsModel> bidDetailsModels;

    public MicroBidWonAdapter(Context context, ArrayList<MicroDataModel> microDataModels, ArrayList<BidDetailsModel> bidDetailsModels) {
        this.context = context;
        this.microDataModels = microDataModels;
        this.bidDetailsModels = bidDetailsModels;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(context).inflate(R.layout.bid_won_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeViewHolder holder, final int position) {
        String placeHolder="Trip Starts "+ microDataModels.get(position).getTimeStamp().getDay()+"/"+ microDataModels.get(position).getTimeStamp().getMonth()+"/"+microDataModels.get(position).getTimeStamp().getYear()+" At "+ microDataModels.get(position).getTimeStamp().getHours()+":"+microDataModels.get(position).getTimeStamp().getMinute();
        holder.tripStartsEnd.setText(placeHolder);
        placeHolder="Must Be "+microDataModels.get(position).getMicroType();
        holder.typeBid.setText(placeHolder);
        placeHolder="Required For "+microDataModels.get(position).getHoursType();
        holder.itemLabourBid.setText(placeHolder);
        placeHolder="Total "+microDataModels.get(position).getMicroRequired()+" Micro Required";
        holder.totalVehicleBid.setText(placeHolder);
        placeHolder="Customer: "+microDataModels.get(position).getAdditional();
        holder.customerRequirementBid.setText(placeHolder);
        holder.startTripButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference profile= FirebaseDatabase.getInstance().getReference("DriverProfile").child(FirebaseAuth.getInstance().getUid()).child("RunningTrip");
                profile.child(microDataModels.get(position).getKey()).child("exist").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DatabaseReference post=FirebaseDatabase.getInstance().getReference("BidPosts").child("Micro").child(microDataModels.get(position).getKey());
                        post.child("status").setValue("Trip").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(context, "Trip Started!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return microDataModels.size();
    }




    class HomeViewHolder extends RecyclerView.ViewHolder
    {
        TextView tripStartsEnd,typeBid,itemLabourBid,totalVehicleBid,customerRequirementBid,startPoint,endPoint,amountBid;
        CardView bidCard;
        Button startTripButon;
        HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tripStartsEnd=itemView.findViewById(R.id.tripStartsEnd);
            startPoint=itemView.findViewById(R.id.startPoint);
            endPoint=itemView.findViewById(R.id.endPoint);
            typeBid=itemView.findViewById(R.id.typeBid);
            itemLabourBid=itemView.findViewById(R.id.itemLabourBid);
            totalVehicleBid=itemView.findViewById(R.id.totalVehicleBid);
            customerRequirementBid=itemView.findViewById(R.id.customerRequirementBid);
            bidCard=itemView.findViewById(R.id.bidCard);
            amountBid=itemView.findViewById(R.id.amountBid);
            startTripButon=itemView.findViewById(R.id.startTripButon);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
