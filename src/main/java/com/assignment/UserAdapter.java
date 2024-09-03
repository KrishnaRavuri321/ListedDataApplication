package com.assignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;
    private OnImageClickListener onImageClickListener;

    public interface OnImageClickListener {
        void onImageClick(User user);
    }

    public UserAdapter(List<User> users, OnImageClickListener onImageClickListener) {
        this.users = users;
        this.onImageClickListener = onImageClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position));


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textViewName;
        private TextView textViewEmail;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewAvatar);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);

            itemView.findViewById(R.id.imageUploadIcon).setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onImageClickListener != null) {
                    onImageClickListener.onImageClick(users.get(position));
                }
            });
        }

        void bind(User user) {
            textViewName.setText(user.getFirst_name() + " " + user.getLast_name());
            textViewEmail.setText(user.getEmail());

            Glide.with(itemView.getContext())
                    .load(user.getAvatar())
                    .into(imageView);
        }
    }
}
