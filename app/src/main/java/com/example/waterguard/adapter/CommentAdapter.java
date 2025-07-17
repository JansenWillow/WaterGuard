package com.example.waterguard.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterguard.R;
import com.example.waterguard.model.Comment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final LinearLayout container;
        private final CircleImageView avatarView;
        private final ImageView videoThumbnail;
        private final LinearLayout contentContainer;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_comment);
            container = (LinearLayout) itemView;
            avatarView = itemView.findViewById(R.id.iv_avatar);
            videoThumbnail = itemView.findViewById(R.id.iv_video_thumbnail);
            contentContainer = (LinearLayout) textView.getParent();
        }

        void bind(Comment comment) {
            textView.setText(comment.getText());

            // Handle avatar visibility and position
            if (comment.isRight()) {
                container.setGravity(Gravity.END);
                contentContainer.setGravity(Gravity.END);
                avatarView.setVisibility(View.VISIBLE);
                ((LinearLayout.LayoutParams) avatarView.getLayoutParams()).setMarginEnd(0);
                ((LinearLayout.LayoutParams) avatarView.getLayoutParams()).setMarginStart(8);
                container.removeView(avatarView);
                container.addView(avatarView);
            } else {
                container.setGravity(Gravity.START);
                contentContainer.setGravity(Gravity.START);
                avatarView.setVisibility(View.VISIBLE);
                ((LinearLayout.LayoutParams) avatarView.getLayoutParams()).setMarginEnd(8);
                ((LinearLayout.LayoutParams) avatarView.getLayoutParams()).setMarginStart(0);
                container.removeView(avatarView);
                container.addView(avatarView, 0);
            }

            // Set background based on position
            textView.setBackgroundResource(comment.isRight() ?
                    R.drawable.bg_chat_user : R.drawable.bg_chat_bot);

            // Handle video thumbnail for video type
            if (comment.getType() == Comment.TYPE_VIDEO) {
                videoThumbnail.setVisibility(View.VISIBLE);
                // The actual thumbnail should be set from the activity
            } else {
                videoThumbnail.setVisibility(View.GONE);
            }
        }

        public void setVideoThumbnail(android.graphics.Bitmap thumbnail) {
            if (videoThumbnail != null && thumbnail != null) {
                videoThumbnail.setImageBitmap(thumbnail);
            }
        }
    }
}