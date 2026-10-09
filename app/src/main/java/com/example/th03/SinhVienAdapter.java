package com.example.th03;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SinhVienAdapter extends RecyclerView.Adapter<SinhVienAdapter.ViewHolder> {

    private List<SinhVien> danhSachSV;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SinhVien sv);
        void onDeleteClick(SinhVien sv, int position);
    }

    public SinhVienAdapter(List<SinhVien> danhSachSV, OnItemClickListener listener) {
        this.danhSachSV = danhSachSV;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sinh_vien, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SinhVien sv = danhSachSV.get(position);
        holder.tvHoTen.setText(sv.getHoTen());
        holder.tvMaSV.setText("MSV: " + sv.getMaSV());
        holder.tvLop.setText("Lớp: " + sv.getLop());

        // Set avatar letter (first character of name)
        String hoTen = sv.getHoTen();
        if (hoTen != null && !hoTen.isEmpty()) {
            // Get the first letter of the last word (Vietnamese naming)
            String[] parts = hoTen.trim().split("\\s+");
            String lastPart = parts[parts.length - 1];
            holder.tvAvatar.setText(String.valueOf(lastPart.charAt(0)).toUpperCase());
        } else {
            holder.tvAvatar.setText("?");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(sv);
        });

        holder.btnXoa.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(sv, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return danhSachSV != null ? danhSachSV.size() : 0;
    }

    public void updateList(List<SinhVien> newList) {
        this.danhSachSV = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvHoTen, tvMaSV, tvLop;
        ImageButton btnXoa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvHoTen = itemView.findViewById(R.id.tvHoTen);
            tvMaSV = itemView.findViewById(R.id.tvMaSV);
            tvLop = itemView.findViewById(R.id.tvLop);
            btnXoa = itemView.findViewById(R.id.btnXoa);
        }
    }
}
