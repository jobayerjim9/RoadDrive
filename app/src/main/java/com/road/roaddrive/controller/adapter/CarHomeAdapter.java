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
import com.road.roaddrive.model.CarDataModel;
import com.road.roaddrive.model.TruckDataModel;
import com.road.roaddrive.ui.activity.BidDetailsActivity;

import java.util.ArrayList;

public class CarHomeAdapter extends RecyclerView.Adapter<CarHomeAdapter.HomeViewHolder> {
    private Context context;
    private ArrayList <CarDataModel> carDataModels;

    public CarHomeAdapter(Context context, ArrayList<CarDataModel> carDataModels) {
        this.context = context;
        this.carDataModels = carDataModels;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(context).inflate(R.layout.bid_item_card,parent,false));
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
        holder.bidCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppData.setCarDataModel(carDataModels.get(position));
                context.startActivity(new Intent(context, BidDetailsActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return carDataModels.size();
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
}
