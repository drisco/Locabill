package com.example.notificationapp;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;
        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;
        import com.example.notificationapp.models.CityItem;
        import com.example.notificationapp.models.HistItems;

        import java.util.List;

public class AdapterCityGroup extends RecyclerView.Adapter<AdapterCityGroup.ViewHolder> {
    private List<HistItems> cityItems;

    public AdapterCityGroup(List<HistItems> classes) {
        this.cityItems=classes;
    }

    public void setCityItems(List<HistItems> cityItems) {
        this.cityItems = cityItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historique, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistItems cityItem = cityItems.get(position);

        /*holder.cityNameTextView.setText(cityItem.getCityName());
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext());
        holder.recyclerview.setLayoutManager(layoutManager);
        Adapter_historique studentAdapter = new Adapter_historique(cityItem.getTenantList());
        holder.recyclerview.setAdapter(studentAdapter);*/

    }

    @Override
    public int getItemCount() {
        return cityItems != null ? cityItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityNameTextView;
        RecyclerView recyclerview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityNameTextView = itemView.findViewById(R.id.textCityName);
            recyclerview = itemView.findViewById(R.id.recyclerview);
        }
    }
}
