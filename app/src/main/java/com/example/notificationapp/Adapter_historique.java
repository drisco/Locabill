package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

import com.example.notificationapp.models.CityItem;
import com.example.notificationapp.models.Model_tenant;
import com.example.notificationapp.models.Model_ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


import java.util.List;

public class Adapter_historique extends RecyclerView.Adapter<Adapter_historique.ViewHolder> {
    private List<Model_ticket> cityItems;
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
        holder.number1.setText(ticket.getNumero());
        holder.type1.setText(ticket.getType());
        holder.date1.setText(ticket.getDate());
        holder.debut1.setText(ticket.getDebutLoca());
        holder.caution1.setText(ticket.getCaution());
        holder.avance1.setText(ticket.getAvance());
        holder.s_chiffre1.setText("Montant en chiffre : "+ticket.getMontantChiffre());

        String qrContent="\nNom: " + ticket.getNom() + "\nPrénom: " + ticket.getPrenom() + "\nPrix: " + ticket.getMontant() +
                "\nNuméro: " + ticket.getNumero() + "\nType de maison: " + ticket.getType() + "\nDébut de location: " + ticket.getDebutLoca() +
                "\nCaution: " + ticket.getCaution() + "\nAvance: " + ticket.getAvance() + "\nDate: " + ticket.getDate();

        try {
            MultiFormatWriter formatWriter = new MultiFormatWriter();
            BitMatrix matrix = formatWriter.encode(qrContent, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcode = new BarcodeEncoder();
            Bitmap bitmap = barcode.createBitmap(matrix);
            holder.qrImageView1.setImageBitmap(bitmap);
            // Vous pouvez ajouter ici d'autres actions si nécessaire
        } catch (WriterException e) {
            e.printStackTrace();
            // Gérez les exceptions ici
        }

    }

    @Override
    public int getItemCount() {
        return cityItems != null ? cityItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView  date1,s_chiffre1,caution1,avance1,debut1,type1,montant1,number1,userPrenom1,userNom1,date2;
        ImageView qrImageView1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date1 = itemView.findViewById(R.id.date1);
            s_chiffre1 = itemView.findViewById(R.id.s_chiffre1);
            caution1 = itemView.findViewById(R.id.caution1);
            avance1 = itemView.findViewById(R.id.avance1);
            debut1=itemView.findViewById(R.id.debut1);
            type1 = itemView.findViewById(R.id.type1);
            montant1 = itemView.findViewById(R.id.montant1);
            number1 = itemView.findViewById(R.id.number1);
            userPrenom1 = itemView.findViewById(R.id.userPrenom1);
            userNom1 = itemView.findViewById(R.id.userNom1);
            qrImageView1 = itemView.findViewById(R.id.qrImageView1);
            date2 = itemView.findViewById(R.id.date2);
        }
    }
}
