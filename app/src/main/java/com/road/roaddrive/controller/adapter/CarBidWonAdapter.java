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
import com.road.roaddrive.model.BidDetailsModel;
import com.road.roaddrive.model.CarDataModel;

import com.road.roaddrive.ui.activity.TripActivity;

import java.util.ArrayList;

public class CarBidWonAdapter extends RecyclerView.Adapter<CarBidWonAdapter.HomeViewHolder>  {
    private Context context;
    private ArrayList<CarDataModel> carDataModels;
    private ArrayList<BidDetailsModel> bidDetailsModels;

    public CarBidWonAdapter(Context context, ArrayList<CarDataModel> carDataModels, ArrayList<BidDetailsModel> bidDetailsModels) {
        this.context = context;
        this.carDataModels = carDataModels;
        this.bidDetailsModels = bidDetailsModels;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CarBidWonAdapter.HomeViewHolder(LayoutInflater.from(context).inflate(R.layout.bid_won_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeViewHolder holder, final int position) {
        String placeHolder="Trip Starts "+ carDataModels.get(position).getTimeStamp().getDay()+"/"+ carDataModels.get(position).getTimeStamp().getMonth()+"/"+carDataModels.get(position).getTimeStamp().getYear()+" At "+ carDataModels.get(position).getTimeStamp().getHours()+":"+carDataModels.get(position).getTimeStamp().getMinute();
        holder.tripStartsEnd.setText(placeHolder);
        placeHolder="Must Be "+carDataModels.get(position).getCarType();
        holder.typeBid.setText(placeHolder);
        placeHolder="Required For " + carDataModels.get(position).getHoursRequired();
        holder.itemLabourBid.setText(placeHolder);
        placeHolder="Total "+carDataModels.get(position).getCarRequired()+" Car Required";
        holder.totalVehicleBid.setText(placeHolder);
        placeHolder="Customer: "+carDataModels.get(position).getDetails();
        holder.customerRequirementBid.setText(placeHolder);
        holder.startPoint.setText(carDataModels.get(position).getSourceName());
        holder.endPoint.setText(carDataModels.get(position).getDestinationName());
        String place="Trip Fixed At "+bidDetailsModels.get(position).getAmount() + " Taka";
        holder.amountBid.setText(place);
        holder.amountBid.setVisibility(View.VISIBLE);
        holder.startTripButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent=new Intent(context, TripActivity.class);
                intent.putExtra("sourceLat",carDataModels.get(position).getSource().getLat());
                intent.putExtra("sourceLng",carDataModels.get(position).getSource().getLng());
                intent.putExtra("desLat",carDataModels.get(position).getDestination().getLat());
                intent.putExtra("desLng",carDataModels.get(position).getDestination().getLng());
                DatabaseReference profile= FirebaseDatabase.getInstance().getReference("DriverProfile").child(FirebaseAuth.getInstance().getUid()).child("RunningTrip");
                profile.child(carDataModels.get(position).getKey()).child("exist").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DatabaseReference post=FirebaseDatabase.getInstance().getReference("BidPosts").child("Car").child(carDataModels.get(position).getKey());
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
        return carDataModels.size();
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
