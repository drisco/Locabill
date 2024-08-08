package com.example.notificationapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

import com.example.notificationapp.models.Model_ticket;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


import java.util.List;

public class Adapter_historique extends RecyclerView.Adapter<Adapter_historique.ViewHolder> {
    private List<Model_ticket> cityItems;
    private static ItemClickListener mClickListener;
    Context context;
    public Adapter_historique(Context context, List<Model_ticket> tenantList) {
        this.cityItems = tenantList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_historique, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model_ticket ticket = cityItems.get(position);
        holder.date2.setText(ticket.getDate());
        holder.userNom1.setText(ticket.getNom());
        holder.userPrenom1.setText(ticket.getPrenom());
        holder.montant1.setText(ticket.getMontant());

    }

    @Override
    public int getItemCount() {
        return cityItems != null ? cityItems.size() : 0;
    }

    public void setFilterUser(List<Model_ticket> filterUser) {
        this.cityItems=filterUser;
        notifyDataSetChanged();
    }

    public void userNotFound(List<Model_ticket> filterUser) {
        this.cityItems=filterUser;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView  montant1,userPrenom1,userNom1,date2;
        RelativeLayout ritem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            montant1 = itemView.findViewById(R.id.montant1);
            userPrenom1 = itemView.findViewById(R.id.userPrenom1);
            userNom1 = itemView.findViewById(R.id.userNom1);
            date2 = itemView.findViewById(R.id.date2);
            ritem = itemView.findViewById(R.id.ritem);
            ritem.setOnClickListener((View.OnClickListener) this);
        }
        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());

        }
    }
    public interface ItemClickListener {
        void onItemClick(View view, int position);

    }
    public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;

    }
}
