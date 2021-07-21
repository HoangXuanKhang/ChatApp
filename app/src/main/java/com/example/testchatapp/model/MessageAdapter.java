package com.example.testchatapp.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testchatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final Context mContext;
    private final List<Message> messageList;
    private final String imageURL;
    private static final int LAYOUT_RIGHT = 0;
    private static final int LAYOUT_LEFT = 1;
    private boolean isLayoutLeft = false;

    public MessageAdapter(Context mContext, List<Message> messageList, String imageURL) {
        this.mContext = mContext;
        this.messageList = messageList;
        this.imageURL = imageURL;
    }

    @NonNull

    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == LAYOUT_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
        } else {
            isLayoutLeft = true;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
        }
        return new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        Log.e("TAG", "onBindViewHolder: " + imageURL );

        holder.messageContentTv.setText(message.getContent());

        if(isLayoutLeft){
            if (imageURL.equals("default")) {
                holder.imageView.setImageResource(R.drawable.account_circle_32);
            } else {
                Glide.with(mContext).load(imageURL).into(holder.imageView);
            }
        }

    }

    @Override
    public int getItemCount() {
//        Log.e("SIZE", "Size" + messageList.size());
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView imageView;
        private final TextView messageContentTv;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.chat_image);
            messageContentTv = itemView.findViewById(R.id.chat_content);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String currentUserId = firebaseUser.getUid();
//        Log.e("TAG", "getItemViewType: " + currentUserId);
//        Log.e("TAG", "getItemViewType: " + messageList.get(position).getSender());
//        Log.e("TAG", "getItemViewType: " + position);
        if (messageList.get(position).getSender().equals(currentUserId)) {
            return LAYOUT_RIGHT;
        } else {
            return LAYOUT_LEFT;
        }
    }
}
