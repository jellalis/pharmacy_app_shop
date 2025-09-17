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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {
    public interface Listener {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
        void onRemove(CartItem item);
    }

    private final List<CartItem> data = new ArrayList<>();
    private final Listener listener;

    public CartAdapter(Listener l) { this.listener = l; }

    public void setItems(List<CartItem> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        CartItem ci = data.get(pos);
        Product p = ci.getProduct();

        h.txtName.setText(p.getName());
        h.txtQty.setText(String.valueOf(ci.getQuantity()));
        h.txtLineTotal.setText(String.format(Locale.getDefault(), "€%.2f", ci.getLineTotal()));

        h.btnPlus.setOnClickListener(v -> listener.onIncrease(ci));
        h.btnMinus.setOnClickListener(v -> listener.onDecrease(ci));
        h.btnRemove.setOnClickListener(v -> listener.onRemove(ci));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtName, txtQty, txtLineTotal;
        View btnPlus, btnMinus, btnRemove;
        VH(@NonNull View v) {
            super(v);
            txtName = v.findViewById(R.id.txtName);
            txtQty = v.findViewById(R.id.txtQty);
            txtLineTotal = v.findViewById(R.id.txtLineTotal);
            btnPlus = v.findViewById(R.id.btnPlus);
            btnMinus = v.findViewById(R.id.btnMinus);
            btnRemove = v.findViewById(R.id.btnRemove);
        }
    }
}
