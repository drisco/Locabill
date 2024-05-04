package com.example.notificationapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notificationapp.Adapter_tickets;
import com.example.notificationapp.Historique;
import com.example.notificationapp.HistoriqueListeRecuGeneree;
import com.example.notificationapp.PopupRegister;
import com.example.notificationapp.R;
import com.example.notificationapp.models.Model_tenant;
import com.example.notificationapp.models.Model_ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoriqueFragment extends Fragment implements Adapter_tickets.ItemClickListener{
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ImageView retour2;
    RecyclerView recyclerView;
    RelativeLayout log;
    Adapter_tickets adapterTickets;
    PopupRegister popusCostum;
    Model_ticket ticket;

    String idAdm,idLca,ville,numero;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historique, container, false);
        sharedPreferences = getActivity().getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        idAdm = sharedPreferences.getString("idAdmin", "");
        idLca = sharedPreferences.getString("id", "");
        ville = sharedPreferences.getString("ville", "");
        numero = sharedPreferences.getString("numero", "");

        recyclerView =view.findViewById(R.id.recyclerView0);
        log =view.findViewById(R.id.log0);

        popusCostum = new PopupRegister(getActivity());
        popusCostum.setCancelable(false);
        popusCostum.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popusCostum.show();
        List<Model_ticket> ticketsData = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("recu").child(idAdm).child(idLca);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                popusCostum.cancel();
                // Effacer la liste existante avant de la remplir avec de nouvelles données
                ticketsData.clear();

                // Cette méthode est appelée chaque fois que les données changent
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                     ticket = snapshot.getValue(Model_ticket.class);
                    ticketsData.add(ticket);
                }

                if (ticketsData.isEmpty()){
                    popusCostum.cancel();
                    log.setVisibility(View.VISIBLE);
                }else{
                    popusCostum.cancel();
                    // Mettre à jour l'adaptateur avec les nouvelles données
                    adapterTickets = new Adapter_tickets(requireContext(),ticketsData);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapterTickets);
                    //adapterTickets.setClickListener((Adapter_tickets.ItemClickListener) getActivity());
                    adapterTickets.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérer les erreurs ici
            }
        });

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getContext(), position, Toast.LENGTH_SHORT).show();
    }
}