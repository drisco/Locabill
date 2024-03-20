package com.example.notificationapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notificationapp.models.Model_ticket;
import java.util.List;

public class Adapter_tickets extends RecyclerView.Adapter<Adapter_tickets.ViewHolder>{

    private List<Model_ticket> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;
    TextView empty;

    Adapter_tickets(Context context, List<Model_ticket> data, TextView empty) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.empty=empty;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_view_ticket, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Model_ticket animal = mData.get(position);

/*

        if(animal.getImage() == null){
            holder.image_profil.setImageResource(R.drawable.ima);
        }else{
            byte [] encodeByte = Base64.decode(animal.getImage(),Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,encodeByte.length);
            holder.image_profil.setImageBitmap(bitmap);
        }
        holder.myTextView1.setText(animal.getCartes().getType_register());
        if(animal.getFirst_name().length()>8){
            holder.myTextView.setText(animal.getFirst_name().substring(0,animal.getFirst_name().offsetByCodePoints(0,8))+"...");
        }else{
            holder.myTextView.setText(animal.getFirst_name());
        }

        if (animal.getLast_name().length()>8){
            holder.myTextView2.setText(animal.getLast_name().substring(0,animal.getLast_name().offsetByCodePoints(0,8))+"...");
        }else{
            holder.myTextView2.setText(animal.getLast_name());
        }

        if (animal.getCartes().getType_carte().equals("Carte residence")){
            holder.myTextView4.setText("Carte.R");
        }else{holder.myTextView4.setText(animal.getCartes().getType_carte()); }

        if(animal.getFonction().equals("Visite")){
            holder.myTextView5.setText("Visiteur");
        }else{
            holder.myTextView5.setText(animal.getFonction());
            holder.myTextView5.setText("Employé");
            holder.myTextView5.setTextColor(context.getResources().getColor(R.color.blue));
        }

        holder.myTextView0.setText(animal.getPassage().getDate_entrer());
        holder.myTextView6.setText(animal.getPassage().getDate_sortir());



        holder.myTextView7.setText("Motif: "+animal.getFonction());
        holder.myTextView8.setText(animal.getPassage().getNumber_visite());*/
        holder.relative_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ((listView)context).other();
                /*Intent intent = new Intent(context,UserView.class);
                intent.putExtra("name",animal.getFirst_name());
                intent.putExtra("im",animal.getImage());
                intent.putExtra("user",animal.getLast_name());
                intent.putExtra("fonction",animal.getFonction());
                intent.putExtra("piece",animal.getCartes().getType_carte());
                intent.putExtra("num",animal.getNum_tel());
                intent.putExtra("number",animal.getPassage().getNumber_visite());
                context.startActivity(intent);
                ((Activity)context).finish();*/
            }


        });



    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView myTextView2;
        ImageView myTextView3;
        RelativeLayout relative_view;
        ImageView image_profil;
        TextView myTextView4;
        TextView myTextView5;
        TextView myTextView6;
        TextView myTextView7;
        TextView myTextView8;
        TextView myTextView0;
        TextView myTextView1;
        ViewHolder(View itemView) {
            super(itemView);
            /*myTextView = itemView.findViewById(R.id.list_person);
            myTextView2 = itemView.findViewById(R.id.list_persone);
            myTextView3 = itemView.findViewById(R.id.show);
            image_profil = itemView.findViewById(R.id.image_profil);
            relative_view=itemView.findViewById(R.id.relative_view);
            myTextView4 = itemView.findViewById(R.id.piece);
            myTextView5 = itemView.findViewById(R.id.statut);
            myTextView6 = itemView.findViewById(R.id.heure_sortie);
            myTextView7 = itemView.findViewById(R.id.motif);
            myTextView8 = itemView.findViewById(R.id.visited);
            myTextView0 = itemView.findViewById(R.id.heure_entrer);
            myTextView1 = itemView.findViewById(R.id.mode_enregist);
            myTextView3.setOnClickListener(this);*/
            //image_profil.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    // convenience method for getting data at click position
    Model_ticket getItem(int id) {
        return mData.get(id);
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
