package dao;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demosqlite.R;

import java.util.ArrayList;

import model.UserModel;

// Kết nối dữ liệu (userList) với giao diện (RecyclerView)
// Tạo, cập nhật & quản lý các item trong danh sách
// Xử lý sự kiện click vào từng item
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final ArrayList<UserModel> userList;
    private OnItemClickListener listener;
    private int selectedPosition = -1; // Vị trí item đang được chọn

    // Interface xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(UserModel user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public UserAdapter(ArrayList<UserModel> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.tvIndex.setText(String.valueOf(position + 1));
        holder.tvUserName.setText(user.getUserName());
        holder.tvPhone.setText(user.getPhone());
        holder.tvEmail.setText(user.getEmail());

        // Đổi màu nền nếu item được chọn
        if (holder.getAdapterPosition() == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.LTGRAY); // Màu xám nhạt khi được chọn
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE); // Màu trắng khi không được chọn
        }

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                selectedPosition = holder.getAdapterPosition(); // Lấy vị trí chính xác
                notifyDataSetChanged(); // Cập nhật lại giao diện
                listener.onItemClick(user);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex, tvUserName, tvPhone, tvEmail;

        public ViewHolder(View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }
}

