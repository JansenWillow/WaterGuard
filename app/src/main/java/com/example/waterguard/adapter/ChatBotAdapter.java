package com.example.waterguard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.waterguard.R;
import com.example.waterguard.model.ChatMessage;
import java.util.List;

public class ChatBotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> messages;
    private OnOptionClickListener optionClickListener;

    public interface OnOptionClickListener {
        void onOptionClick(int optionIndex);
    }

    public ChatBotAdapter(List<ChatMessage> messages, OnOptionClickListener listener) {
        this.messages = messages;
        this.optionClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ChatMessage.TYPE_BOT:
                return new BotViewHolder(inflater.inflate(R.layout.item_chat_bot, parent, false));
            case ChatMessage.TYPE_USER:
                return new UserViewHolder(inflater.inflate(R.layout.item_chat_user, parent, false));
            case ChatMessage.TYPE_OPTIONS:
                return new OptionsViewHolder(inflater.inflate(R.layout.item_chat_options, parent, false));
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        
        if (holder instanceof BotViewHolder) {
            ((BotViewHolder) holder).bind(message);
        } else if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
        } else if (holder instanceof OptionsViewHolder) {
            ((OptionsViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        BotViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_message);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        UserViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_message);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
        }
    }

    class OptionsViewHolder extends RecyclerView.ViewHolder {
        TextView option1, option2, option3;

        OptionsViewHolder(View itemView) {
            super(itemView);
            option1 = itemView.findViewById(R.id.option_1);
            option2 = itemView.findViewById(R.id.option_2);
            option3 = itemView.findViewById(R.id.option_3);

            option1.setOnClickListener(v -> optionClickListener.onOptionClick(0));
            option2.setOnClickListener(v -> optionClickListener.onOptionClick(1));
            option3.setOnClickListener(v -> optionClickListener.onOptionClick(2));
        }

        void bind(ChatMessage message) {
            String[] options = message.getMessage().split("\n");
            if (options.length >= 3) {
                option1.setText(options[0]);
                option2.setText(options[1]);
                option3.setText(options[2]);
            }
        }
    }
} 