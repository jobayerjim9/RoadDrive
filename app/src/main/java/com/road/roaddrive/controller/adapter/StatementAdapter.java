package com.road.roaddrive.controller.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.road.roaddrive.R;
import com.road.roaddrive.model.StatementModel;

import java.util.ArrayList;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.StatementViewHolder> {
    private Context context;
    private ArrayList<StatementModel> statementModels;

    public StatementAdapter(Context context, ArrayList<StatementModel> statementModels) {
        this.context = context;
        this.statementModels = statementModels;
    }

    @NonNull
    @Override
    public StatementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StatementViewHolder(LayoutInflater.from(context).inflate(R.layout.statement_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull StatementViewHolder holder, int position) {
        String placeHolder="Invoice "+statementModels.get(position).getId();
        holder.invoiceId.setText(placeHolder);
        placeHolder = statementModels.get(position).getDriverEarn()+"/-";
        holder.myEarn.setText(placeHolder);
        placeHolder= statementModels.get(position).getAgentEarn()+"/-";
        holder.agentEarn.setText(placeHolder);
        placeHolder= statementModels.get(position).getCompanyEarn()+"/-";
        holder.companyEarn.setText(placeHolder);
        if (statementModels.get(position).isPaid())
        {
            placeHolder = "Paid";
            holder.duePaid.setText(placeHolder);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.duePaid.setTextColor(context.getColor(R.color.colorPrimaryDark));
            }
        }
        else
        {
            placeHolder = "Your Due "+(statementModels.get(position).getAgentEarn()+statementModels.get(position).getCompanyEarn())+"/-";
            holder.duePaid.setText(placeHolder);
        }

    }

    @Override
    public int getItemCount() {
        return statementModels.size();
    }

    class StatementViewHolder extends RecyclerView.ViewHolder {
        TextView invoiceId,myEarn,agentEarn,companyEarn,duePaid;
        public StatementViewHolder(@NonNull View itemView) {
            super(itemView);
            invoiceId=itemView.findViewById(R.id.invoiceId);
            myEarn=itemView.findViewById(R.id.myEarn);
            agentEarn=itemView.findViewById(R.id.agentEarn);
            companyEarn=itemView.findViewById(R.id.companyEarn);
            duePaid=itemView.findViewById(R.id.duePaid);
        }
    }
}
