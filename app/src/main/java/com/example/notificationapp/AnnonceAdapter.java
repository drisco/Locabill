package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.notificationapp.models.Annonce;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class AnnonceAdapter extends RecyclerView.Adapter<AnnonceAdapter.AnnonceViewHolder> {
    private List<Annonce> annonces;
    private Context context;
    private static AnnonceAdapter.ItemClickListener mClickListener;

    public AnnonceAdapter(List<Annonce> annonces, Context context) {
        this.annonces = annonces;
        this.context = context;
    }

    @NonNull
    @Override
    public AnnonceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_annonce, parent, false);
        return new AnnonceViewHolder(view);
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull AnnonceViewHolder holder, int position) {
        Annonce annonce = annonces.get(position);
        holder.titreTextView.setText("Problèmes : "+annonce.getTitre());
        holder.descriptionTextView.setText(annonce.getDescription());
        holder.cmmune.setText("Ville/Commune : "+annonce.getVille());
        holder.number.setText("Numéro : "+annonce.getNumero());
        holder.netpren.setText("Nom et Prénom : "+annonce.getNom()+" "+annonce.getPrenom());


        // Limiter la description à 100 caractères
        String fullDescription = annonce.getDescription();
        if (fullDescription.length() > 80) {
            String truncatedDescription = fullDescription.substring(0, 80) + "...";
            holder.descriptionTextView.setText(truncatedDescription);
            holder.tvoirplus.setVisibility(View.VISIBLE);
        } else {
            holder.descriptionTextView.setText(fullDescription);
            holder.tvoirplus.setVisibility(View.GONE);
        }

        // Set up the "Voir plus" button
        holder.tvoirplus.setOnClickListener(v -> {
            if (holder.tvoirplus.getText().equals("Voir plus")) {
                holder.descriptionTextView.setText(fullDescription);
                holder.tvoirplus.setText("Voir moins");
            } else {
                String truncatedDescription = fullDescription.substring(0, 80) + "...";
                holder.descriptionTextView.setText(truncatedDescription);
                holder.tvoirplus.setText("Voir plus");
            }
        });

        // Clear the image container first
        holder.imageContainer.removeAllViews();

        // Dynamically add ImageViews to the LinearLayout
        for (String imageUrl : annonce.getImageUris()) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(500, 500);
            layoutParams.setMargins(5, 0, 5, 0); // Set margin if needed
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);
            Glide.with(context).load(imageUrl)
                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(30, 0)))
                    .into(imageView);

            imageView.setOnClickListener(v -> showImageDialog(imageUrl));


            holder.imageContainer.addView(imageView);
        }
    }

    private void showImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image_view);

        ImageView fullImageView = dialog.findViewById(R.id.fullImageView);
        Glide.with(context)
                .load(imageUrl)
                .into(fullImageView);

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return annonces.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void userNotFound(ArrayList<Annonce> filterUser) {
        this.annonces=filterUser;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterUser(ArrayList<Annonce> filterUser) {
        this.annonces=filterUser;
        notifyDataSetChanged();
    }

    public static class AnnonceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView titreTextView,cmmune,number,netpren;
        TextView descriptionTextView,tvoirplus;
        LinearLayout imageContainer;
        ImageView supp;
        public AnnonceViewHolder(@NonNull View itemView) {
            super(itemView);
            titreTextView = itemView.findViewById(R.id.titreTextView);
            number = itemView.findViewById(R.id.number);
            netpren = itemView.findViewById(R.id.netpren);
            cmmune = itemView.findViewById(R.id.cmmune);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            imageContainer = itemView.findViewById(R.id.imageContainer);
            tvoirplus = itemView.findViewById(R.id.tvoirplus);
            supp = itemView.findViewById(R.id.supp);
            supp.setOnClickListener((View.OnClickListener) this);

        }
        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());

        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);

    }
    public void setClickListener(AnnonceAdapter.ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;

    }
}

