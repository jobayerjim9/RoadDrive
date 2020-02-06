package com.road.roaddrive.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.road.roaddrive.R;
import com.road.roaddrive.model.AppData;
import com.road.roaddrive.model.TruckDataModel;
import com.road.roaddrive.ui.activity.BidDetailsActivity;

import java.util.ArrayList;

public class TruckHomeAdapter extends RecyclerView.Adapter<TruckHomeAdapter.HomeViewHolder> {
    private Context context;
    private ArrayList <TruckDataModel> truckDataModels;
    public TruckHomeAdapter(Context context, ArrayList<TruckDataModel> truckDataModels) {
        this.context = context;
        this.truckDataModels = truckDataModels;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(context).inflate(R.layout.bid_item_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeViewHolder holder, final int position) {
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

        holder.bidCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, BidDetailsActivity.class);
             //   intent.putExtra("truckData",truckDataModels);
                AppData.setTruckDataModel(truckDataModels.get(position));
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return truckDataModels.size();
    }




    class HomeViewHolder extends RecyclerView.ViewHolder
    {
        TextView tripStartsEnd,typeBid,itemLabourBid,totalVehicleBid,customerRequirementBid,startPoint,endPoint;
        CardView bidCard;
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

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
