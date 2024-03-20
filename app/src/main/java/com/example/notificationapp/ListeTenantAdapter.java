package com.example.notificationapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notificationapp.models.CityItem;
import com.example.notificationapp.models.Model_tenant;

import java.util.List;

public class ListeTenantAdapter extends RecyclerView.Adapter<ListeTenantAdapter.ViewHolder> {
    private List<CityItem> cityItems;
    TextView empty;
    private ItemClickListener mClickListener;

    public ListeTenantAdapter(List<CityItem> classes) {
        this.cityItems = classes;
    }

    public void setCityItems(List<CityItem> cityItems) {
        this.cityItems = cityItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tenat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CityItem cityItem = cityItems.get(position);

        holder.cityNameTextView1.setText(cityItem.getCityName());
        holder.cityNameTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.llayout.setVisibility(View.VISIBLE);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext());
        holder.recyclerview1.setLayoutManager(layoutManager);
        Adapter_tenants studentAdapter = new Adapter_tenants(holder.itemView.getContext(), cityItem.getTenantList(), empty);

        studentAdapter.setClickListener(new Adapter_tenants.ItemClickListener() {
            @Override
            public void onItemClick(View view, int tenantPosition) {
                if (mClickListener != null) {
                    int cityItemPosition = holder.getAdapterPosition();
                    mClickListener.onItemClick(view,cityItemPosition, tenantPosition);
                }
            }
        });
        holder.recyclerview1.setAdapter(studentAdapter);
    }

    @Override
    public int getItemCount() {
        return cityItems != null ? cityItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityNameTextView1;
        RecyclerView recyclerview1;
        LinearLayout llayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityNameTextView1 = itemView.findViewById(R.id.textCityName01);
            recyclerview1 = itemView.findViewById(R.id.recyclerview01);
            llayout = itemView.findViewById(R.id.llayout);
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view ,int cityItemPosition, int tenantPosition);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}

