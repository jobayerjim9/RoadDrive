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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.road.roaddrive.R;
import com.road.roaddrive.model.AppData;
import com.road.roaddrive.model.BidDetailsModel;
import com.road.roaddrive.model.TruckDataModel;
import com.road.roaddrive.ui.activity.BidDetailsActivity;
import com.road.roaddrive.ui.activity.TripActivity;

import java.util.ArrayList;

public class TruckBidWonAdapter extends RecyclerView.Adapter<TruckBidWonAdapter.HomeViewHolder> {
    private Context context;
    private ArrayList<TruckDataModel> truckDataModels;
    private ArrayList<BidDetailsModel> bidDetailsModels;

    public TruckBidWonAdapter(Context context, ArrayList<TruckDataModel> truckDataModels, ArrayList<BidDetailsModel> bidDetailsModels) {
        this.context = context;
        this.truckDataModels = truckDataModels;
        this.bidDetailsModels = bidDetailsModels;
    }

    @NonNull
    @Override
    public TruckBidWonAdapter.HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(context).inflate(R.layout.bid_won_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final TruckBidWonAdapter.HomeViewHolder holder, final int position) {
        String placeHolder="Trip Starts "+ truckDataModels.get(position).getTimeStamp().getDay()+"/"+ truckDataModels.get(position).getTimeStamp().getMonth()+"/"+truckDataModels.get(position).getTimeStamp().getYear()+" At "+ truckDataModels.get(position).getTimeStamp().getHours()+":"+truckDataModels.get(position).getTimeStamp().getMinute();
        holder.tripStartsEnd.setText(placeHolder);
        placeHolder="Must Be "+truckDataModels.get(position).getVehicleType() +" & "+truckDataModels.get(position).getVehicleSize();
        holder.typeBid.setText(placeHolder);
        placeHolder="Carry " + truckDataModels.get(position).getItemType()+" Item & Need "+truckDataModels.get(position).getLabour()+ " Labour!";
        holder.itemLabourBid.setText(placeHolder);
        placeHolder="Total "+truckDataModels.get(position).getTotalTruck()+" Truck Required";
        holder.totalVehicleBid.setText(placeHolder);
        placeHolder="Customer: "+truckDataModels.get(position).getProductDescription();
        holder.customerRequirementBid.setText(placeHolder);
        holder.startPoint.setText(truckDataModels.get(position).getLoadLocation());
        holder.endPoint.setText(truckDataModels.get(position).getUnloadLocation());
        String place="Trip Fixed At "+bidDetailsModels.get(position).getAmount() + " Taka";
        holder.amountBid.setText(place);
        holder.amountBid.setVisibility(View.VISIBLE);
        holder.startTripButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent=new Intent(context, TripActivity.class);
                intent.putExtra("sourceLat",truckDataModels.get(position).getSource().getLat());
                intent.putExtra("sourceLng",truckDataModels.get(position).getSource().getLng());
                intent.putExtra("desLat",truckDataModels.get(position).getDestination().getLat());
                intent.putExtra("desLng",truckDataModels.get(position).getDestination().getLng());
                DatabaseReference profile= FirebaseDatabase.getInstance().getReference("DriverProfile").child(FirebaseAuth.getInstance().getUid()).child("RunningTrip");
                profile.child(truckDataModels.get(position).getKey()).child("exist").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DatabaseReference post=FirebaseDatabase.getInstance().getReference("BidPosts").child("Truck").child(truckDataModels.get(position).getKey());
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
        return truckDataModels.size();
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
