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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.road.roaddrive.R;
import com.road.roaddrive.model.AppData;
import com.road.roaddrive.model.BikeDataModel;
import com.road.roaddrive.model.CarDataModel;
import com.road.roaddrive.model.DriverProfile;
import com.road.roaddrive.model.TruckDataModel;
import com.road.roaddrive.ui.activity.BidDetailsActivity;

import java.util.ArrayList;

public class BikeHomeAdapter extends RecyclerView.Adapter<BikeHomeAdapter.HomeViewHolder> {
    private Context context;
    private ArrayList <BikeDataModel> bikeDataModels;
    private DriverProfile driverProfile;
    GoogleMap thisMap;

    public BikeHomeAdapter(Context context, ArrayList<BikeDataModel> bikeDataModels, DriverProfile driverProfile) {
        this.context = context;
        this.bikeDataModels = bikeDataModels;
        this.driverProfile=driverProfile;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(context).inflate(R.layout.bid_item_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeViewHolder holder, final int position) {
        String placeHolder="Trip Starts "+ bikeDataModels.get(position).getTimeStamp().getDay()+"/"+ bikeDataModels.get(position).getTimeStamp().getMonth()+"/"+bikeDataModels.get(position).getTimeStamp().getYear()+" At "+ bikeDataModels.get(position).getTimeStamp().getHours()+":"+bikeDataModels.get(position).getTimeStamp().getMinute();
        holder.tripStartsEnd.setText(placeHolder);
        holder.typeBid.setVisibility(View.GONE);
        placeHolder="Required For " + bikeDataModels.get(position).getHoursType();
        holder.itemLabourBid.setText(placeHolder);
        placeHolder="Total "+bikeDataModels.get(position).getBikeRequired()+" "+driverProfile.getDriverType()+" Required";
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
    }

    @Override
    public int getItemCount() {
        return bikeDataModels.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder
    {
        TextView tripStartsEnd,typeBid,itemLabourBid,totalVehicleBid,customerRequirementBid,startPoint,endPoint;
        CardView bidCard;
        HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tripStartsEnd=itemView.findViewById(R.id.tripStartsEnd);
            typeBid=itemView.findViewById(R.id.typeBid);
            itemLabourBid=itemView.findViewById(R.id.itemLabourBid);
            totalVehicleBid=itemView.findViewById(R.id.totalVehicleBid);
            customerRequirementBid=itemView.findViewById(R.id.customerRequirementBid);
            startPoint=itemView.findViewById(R.id.startPoint);
            endPoint=itemView.findViewById(R.id.endPoint);
            bidCard=itemView.findViewById(R.id.bidCard);
        }

    }
}
