package com.panagou.pms.android.project.pharmacyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VH> {

    public interface OnClick {
        void onClick(String orderId);
    }

    private final List<OrderRow> data = new ArrayList<>();
    private final OnClick click;

    public OrderAdapter(OnClick click) {
        this.click = click;
    }

    public void setItems(List<OrderRow> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        OrderRow row = data.get(pos);
        h.tvOrderTitle.setText("Order #" + row.idShort());
        int count = row.itemsCount;
        String dateStr = row.dateStr();
        h.tvOrderSub.setText(String.format(Locale.getDefault(),
                "%d %s • €%.2f • %s",
                count, count==1?"item":"items", row.total, dateStr));
        h.tvOrderStatus.setText("Status: " + (row.status == null ? "-" : row.status));

        h.itemView.setOnClickListener(v -> {
            if (click != null) click.onClick(row.id);
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvOrderTitle, tvOrderSub, tvOrderStatus;
        VH(@NonNull View v) {
            super(v);
            tvOrderTitle = v.findViewById(R.id.tvOrderTitle);
            tvOrderSub   = v.findViewById(R.id.tvOrderSub);
            tvOrderStatus= v.findViewById(R.id.tvOrderStatus);
        }
    }


    public static class OrderRow {
        public String id;          // document id
        public double total;
        public String status;
        public int itemsCount;
        public java.util.Date createdTime;

        public String idShort() {
            if (id == null) return "—";
            return id.length() > 6 ? id.substring(0,6) : id;
        }
        public String dateStr() {
            if (createdTime == null) return "—";
            return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(createdTime);
        }
    }
}
