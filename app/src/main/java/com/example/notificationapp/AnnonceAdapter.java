package com.example.notificationapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.notificationapp.models.Annonce;

import java.util.List;

public class AnnonceAdapter extends RecyclerView.Adapter<AnnonceAdapter.AnnonceViewHolder> {
    private List<Annonce> annonces;
    private Context context;

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

    @Override
    public void onBindViewHolder(@NonNull AnnonceViewHolder holder, int position) {
        Annonce annonce = annonces.get(position);
        holder.titreTextView.setText(annonce.getTitre());
        holder.descriptionTextView.setText(annonce.getDescription());

        // Clear the image container first
        holder.imageContainer.removeAllViews();

        // Dynamically add ImageViews to the LinearLayout
        for (String imageUrl : annonce.getImageUris()) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(8, 0, 8, 0); // Set margin if needed
            imageView.setLayoutParams(layoutParams);

            // Use Glide or any other image loading library to load the image
            Glide.with(context).load(imageUrl).into(imageView);

            holder.imageContainer.addView(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return annonces.size();
    }

    public static class AnnonceViewHolder extends RecyclerView.ViewHolder {
        TextView titreTextView;
        TextView descriptionTextView;
        LinearLayout imageContainer;
        public AnnonceViewHolder(@NonNull View itemView) {
            super(itemView);
            titreTextView = itemView.findViewById(R.id.titreTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            imageContainer = itemView.findViewById(R.id.imageContainer);

        }
    }
}

