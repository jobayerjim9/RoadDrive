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
import com.road.roaddrive.model.MicroDataModel;
import com.road.roaddrive.model.TruckDataModel;
import com.road.roaddrive.ui.activity.BidDetailsActivity;

import java.util.ArrayList;

public class MicroHomeAdapter extends RecyclerView.Adapter<MicroHomeAdapter.HomeViewHolder> {
    private Context context;
    private ArrayList <MicroDataModel> microDataModels;

    public MicroHomeAdapter(Context context, ArrayList<MicroDataModel> microDataModels) {
        this.context = context;
        this.microDataModels = microDataModels;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(context).inflate(R.layout.bid_item_card,parent,false));
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
        holder.bidCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppData.setMicroDataModel(microDataModels.get(position));
                context.startActivity(new Intent(context, BidDetailsActivity.class));
            }
        });

        holder.startPoint.setText(microDataModels.get(position).getSourceName());
        holder.endPoint.setText(microDataModels.get(position).getDestinationName());
    }

    @Override
    public int getItemCount() {
        return microDataModels.size();
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
