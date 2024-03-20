package com.example.notificationapp;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notificationapp.models.Model_tenant;
import java.util.List;

public class Adapter_tenants extends RecyclerView.Adapter<Adapter_tenants.ViewHolder>{


    private List<Model_tenant> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    Context context;
    TextView empty;

    Adapter_tenants(Context context, List<Model_tenant> data, TextView empty) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.empty=empty;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.tenants_adapter_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Model_tenant user = mData.get(position);

        if(user.getNom().length()>8){
            holder.nom.setText("Nom : "+user.getNom().substring(0,user.getNom().offsetByCodePoints(0,8))+"...");
        }else{
            holder.nom.setText("Nom : "+user.getNom());
        }
        if(user.getPrenom().length()>8){
            holder.prenom.setText("Prénom : "+user.getPrenom().substring(0,user.getPrenom().offsetByCodePoints(0,8))+"...");
        }else{
            holder.prenom.setText("Prénom : "+user.getPrenom());
        }
        holder.numero.setText("Numéro : "+user.getNumero());
        holder.caution.setText("Caution : "+user.getCaution());
        holder.avance.setText("Avance : "+user.getAvance());
        holder.type.setText("Type : "+user.getType_de_maison());
        holder.date.setText("Date : "+user.getDebut_de_loca());
        holder.adresse.setText("Prix : "+user.getPrix());

/*
        holder.relative_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MoreTenant.class);
                Bundle bundle = new Bundle();
                bundle.putString("nom", user.getNom());
                bundle.putString("prenom", user.getPrenom());
                bundle.putString("avance", user.getAvance());
                bundle.putString("caution", user.getCaution());
                bundle.putString("numero", user.getNumero());
                bundle.putString("debut", user.getDebut_de_loca());
                bundle.putString("type", user.getType_de_maison());
                bundle.putString("prix", user.getPrix());
                intent.putExtras(bundle);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
*/



    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nom;
        TextView prenom;
        TextView numero,caution,avance,date,type,adresse;
        RelativeLayout relative_view;
        ImageView option;
        ViewHolder(View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.userNom);
            prenom = itemView.findViewById(R.id.userPrenom);
            numero = itemView.findViewById(R.id.number);
            caution = itemView.findViewById(R.id.caution);
            avance = itemView.findViewById(R.id.avance);
            adresse = itemView.findViewById(R.id.adresse);
            date = itemView.findViewById(R.id.date);
            type = itemView.findViewById(R.id.type);
            option = itemView.findViewById(R.id.option);

            option.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());

        }

    }

    // convenience method for getting data at click position
    Model_tenant getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);

    }
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;

    }



}

