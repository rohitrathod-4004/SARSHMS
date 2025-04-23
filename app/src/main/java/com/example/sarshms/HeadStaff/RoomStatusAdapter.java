package com.example.sarshms.HeadStaff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sarshms.R;
import java.util.List;
import java.util.Map;

public class RoomStatusAdapter extends RecyclerView.Adapter<RoomStatusAdapter.RoomViewHolder> {

    private Context context;
    private List<RoomModel> roomList;

    public RoomStatusAdapter(Context context, List<RoomModel> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_status, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomModel room = roomList.get(position);
        holder.tvRoom.setText("Room: " + room.getRoomNumber());

        StringBuilder bedsInfo = new StringBuilder();
        for (Map.Entry<String, String> entry : room.getBeds().entrySet()) {
            bedsInfo.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        holder.tvBeds.setText(bedsInfo.toString().trim());
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoom, tvBeds;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoom = itemView.findViewById(R.id.tv_room_name);
            tvBeds = itemView.findViewById(R.id.tv_beds_info);
        }
    }
}
