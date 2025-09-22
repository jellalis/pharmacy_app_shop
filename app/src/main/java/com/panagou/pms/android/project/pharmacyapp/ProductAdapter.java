package com.panagou.pms.android.project.pharmacyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    public interface OnProductClick {
        void onClick(Product p);
    }

    private final List<Product> items = new ArrayList<>();
    private OnProductClick onClick;


    public void setOnProductClick(OnProductClick l) { this.onClick = l; }

    public void setItems(List<Product> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Product p = items.get(pos);
        h.txtName.setText(p.getName() != null ? p.getName() : "-");
        double price = p.getPrice() != null ? p.getPrice() : 0.0;
        h.txtPrice.setText(String.format(Locale.getDefault(), "€%.2f", price));


        h.itemView.setOnClickListener(v -> {
            if (onClick != null) onClick.onClick(p);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice;
        VH(@NonNull View itemView) {
            super(itemView);
            txtName  = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}

