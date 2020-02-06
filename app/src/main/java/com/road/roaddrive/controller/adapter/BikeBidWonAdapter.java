package com.road.roaddrive.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.road.roaddrive.R;
import com.road.roaddrive.model.AppData;
import com.road.roaddrive.model.BidDetailsModel;
import com.road.roaddrive.model.BikeDataModel;
import com.road.roaddrive.ui.activity.BidDetailsActivity;
import com.road.roaddrive.ui.activity.TripActivity;

import java.util.ArrayList;

public class BikeBidWonAdapter extends RecyclerView.Adapter<BikeBidWonAdapter.HomeViewHolder> {
    private Context context;
    private ArrayList<BikeDataModel> bikeDataModels;
    private ArrayList<BidDetailsModel> bidDetailsModels;

    public BikeBidWonAdapter(Context context, ArrayList<BikeDataModel> bikeDataModels, ArrayList<BidDetailsModel> bidDetailsModels) {
        this.context = context;
        this.bikeDataModels = bikeDataModels;
        this.bidDetailsModels = bidDetailsModels;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(context).inflate(R.layout.bid_won_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeViewHolder holder, final int position) {
        String placeHolder="Trip Starts "+ bikeDataModels.get(position).getTimeStamp().getDay()+"/"+ bikeDataModels.get(position).getTimeStamp().getMonth()+"/"+bikeDataModels.get(position).getTimeStamp().getYear()+" At "+ bikeDataModels.get(position).getTimeStamp().getHours()+":"+bikeDataModels.get(position).getTimeStamp().getMinute();
        holder.tripStartsEnd.setText(placeHolder);
        holder.typeBid.setVisibility(View.GONE);
        placeHolder="Required For " + bikeDataModels.get(position).getHoursType();
        holder.itemLabourBid.setText(placeHolder);
        placeHolder="Total "+bikeDataModels.get(position).getBikeRequired()+" "+" Bike Required";
        holder.totalVehicleBid.setText(placeHolder);
        placeHolder="Customer: "+bikeDataModels.get(position).getAdditional();
        holder.customerRequirementBid.setText(placeHolder);
        holder.bidCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppData.setBikeDataModel(bikeDataModels.get(position));
                context.startActivity(new Intent(context, BidDetailsActivity.class));
            }
        });
        holder.startPoint.setText(bikeDataModels.get(position).getSourceName());
        holder.endPoint.setText(bikeDataModels.get(position).getDestinationName());
        holder.startTripButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent=new Intent(context, TripActivity.class);
                intent.putExtra("sourceLat",bikeDataModels.get(position).getSource().getLat());
                intent.putExtra("sourceLng",bikeDataModels.get(position).getSource().getLng());
                intent.putExtra("desLat",bikeDataModels.get(position).getDestination().getLat());
                intent.putExtra("desLng",bikeDataModels.get(position).getDestination().getLng());
                DatabaseReference profile= FirebaseDatabase.getInstance().getReference("DriverProfile").child(FirebaseAuth.getInstance().getUid()).child("RunningTrip");
                profile.child(bikeDataModels.get(position).getKey()).child("exist").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DatabaseReference post=FirebaseDatabase.getInstance().getReference("BidPosts").child("Bike").child(bikeDataModels.get(position).getKey());
                        post.child("status").setValue("Trip").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    context.startActivity(intent);
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
        return bikeDataModels.size();
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
