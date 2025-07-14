package com.example.waterguard.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterguard.R;
import com.example.waterguard.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments = new ArrayList<>();

    public void addComment(Comment comment) {
        comments.add(comment);
        notifyItemInserted(comments.size() - 1);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment, position % 2 == 0);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final LinearLayout container;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_comment);
            container = (LinearLayout) itemView;
        }

        void bind(Comment comment, boolean isRight) {
            textView.setText(comment.getText());
            
            // Set gravity and background based on position
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
            if (isRight) {
                container.setGravity(Gravity.END);
                textView.setBackgroundResource(R.drawable.bg_chat_user);
                params.gravity = Gravity.END;
            } else {
                container.setGravity(Gravity.START);
                textView.setBackgroundResource(R.drawable.bg_chat_bot);
                params.gravity = Gravity.START;
            }
            textView.setLayoutParams(params);
        }
    }
} 