package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

import com.example.notificationapp.models.CityItem;
import com.example.notificationapp.models.Model_tenant;


import java.util.List;

public class Adapter_historique extends RecyclerView.Adapter<Adapter_historique.ViewHolder> {
    private List<Model_tenant> cityItems;

    public Adapter_historique(List<Model_tenant> tenantList) {
        this.cityItems = tenantList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_historique, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model_tenant cityItem = cityItems.get(position);
        holder.textCityName2.setText(cityItem.getNom());
        holder.textCityName.setText(cityItem.getPrenom());
        holder.textCityName1.setText(cityItem.getPrix());

    }

    @Override
    public int getItemCount() {
        return cityItems != null ? cityItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCityName,textCityName1,textCityName2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCityName = itemView.findViewById(R.id.textCityName0);
            textCityName1 = itemView.findViewById(R.id.textCityName1);
            textCityName2 = itemView.findViewById(R.id.textCityName2);
        }
    }
}
