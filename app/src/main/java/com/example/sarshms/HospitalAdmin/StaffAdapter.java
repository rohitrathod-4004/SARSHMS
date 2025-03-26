package com.example.sarshms.HospitalAdmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;

import java.util.ArrayList;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {

    private ArrayList<String> staffList;
    private OnStaffClickListener onStaffClickListener;

    public StaffAdapter(ArrayList<String> staffList, OnStaffClickListener listener) {
        this.staffList = staffList;
        this.onStaffClickListener = listener;
    }

    @NonNull
    @Override
    public StaffAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffAdapter.ViewHolder holder, int position) {
        String email = staffList.get(position);
        holder.tvEmail.setText(email);
        holder.itemView.setOnClickListener(v -> onStaffClickListener.onStaffClick(email));
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_staff_email);
        }
    }

    public interface OnStaffClickListener {
        void onStaffClick(String email);
    }
}
