package com.example.notificationapp;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.notificationapp.models.Model_ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("recu");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("localites");
        Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.applyPattern("dd-MM-yyyy HH:mm");
        String heureActuelle = sdf.format(heure);
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
        String dateFormatted = sdf2.format(heure);
        Model_tenant user = mData.get(position);
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa "+dateFormatted);
        if (user.getNom().length() > 8) {
            holder.nom.setText("Nom : " + user.getNom().substring(0, user.getNom().offsetByCodePoints(0, 8)) + "...");
        } else {
            holder.nom.setText("Nom : " + user.getNom());
        }
        if (user.getPrenom().length() > 8) {
            holder.prenom.setText("Prénom : " + user.getPrenom().substring(0, user.getPrenom().offsetByCodePoints(0, 8)) + "...");
        } else {
            holder.prenom.setText("Prénom : " + user.getPrenom());
        }
        holder.numero.setText("Numéro : " + user.getNumero());
        holder.caution.setText("Caution : " + user.getCaution());
        holder.avance.setText("Avance : " + user.getAvance());
        holder.type.setText("Type : " + user.getType_de_maison());
        holder.date.setText("Date : " + user.getDebut_de_loca());
        holder.adresse.setText("Prix : " + user.getPrix());

        databaseReference.child(user.getIdProprie()).child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean dateExists = false;
                System.out.println("DHUSIHGISLGLHZDQHKHSGSSDHHSHQ.HDSHGJSGH "+"BAMBA"+snapshot);

                for (DataSnapshot data : snapshot.getChildren()) {
                    System.out.println("DHUSIHGISLGLHZDQHKHSGSSDHHSHQ.HDSHGJSGH "+"FOR"+data);
                    Model_ticket tenant = data.getValue(Model_ticket.class);
                    if (tenant != null &&  tenant.getDate().equals(dateFormatted)) {

                        dateExists = true;
                        break;
                    }
                }

                if (dateExists) {
                    System.out.println("DHUSIHGISLGLHZDQHKHSGSSDHHSHQ.HDSHGJSGH "+"BAMBA");
                    databaseReference1.child(user.getIdProprie()).child(user.getLocalite()).child(user.getId()).child("statut").setValue("payé");
                } else {
                    System.out.println("DHUSIHGISLGLHZDQHKHSGSSDHHSHQ.HDSHGJSGH "+"DRESSA");
                    databaseReference1.child(user.getIdProprie()).child(user.getLocalite()).child(user.getId()).child("statut").setValue("impayé");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gérer les erreurs
            }
        });

        if ( user.getStatut().equals("payé")){
            holder.statut.setText(user.getStatut());
            holder.statut.setTextColor(Color.GREEN);
        }else {
            holder.statut.setText(user.getStatut());
            holder.statut.setTextColor(Color.RED);
        }
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nom;
        TextView prenom,statut;
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
            statut = itemView.findViewById(R.id.statut);
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

