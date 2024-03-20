package com.example.notificationapp;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notificationapp.models.Model_ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

public class Adapter_tickets extends RecyclerView.Adapter<Adapter_tickets.ViewHolder>{

    private List<Model_ticket> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;

    Adapter_tickets(Context context, List<Model_ticket> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recu_tenant_history_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Model_ticket ticket = mData.get(position);
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

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView  date1,s_chiffre1,caution1,avance1,debut1,type1,montant1,number1,userPrenom1,userNom1,date2;
        ImageView qrImageView1;
        ViewHolder(View itemView) {
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

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;


    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);

    }
}
