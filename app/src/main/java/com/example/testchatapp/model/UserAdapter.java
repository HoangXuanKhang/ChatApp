package com.example.testchatapp.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testchatapp.MessageActivity;
import com.example.testchatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    public static final String FRIEND_ID = "friend_id";
    private Context mContext;
    private List<User> userList;
    private boolean isChat;

    private String lastMessage;
    private DatabaseReference reference;
    private FirebaseUser currentUser;

    public UserAdapter(Context mContext, List<User> userList, boolean isChat) {
        this.mContext = mContext;
        this.userList = userList;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  UserAdapter.UserViewHolder holder, int position) {
        User user = userList.get(position);
        if(user == null){
            return;
        }

        if(user.getImageURL().equals("default")){
            holder.imageView.setImageResource(R.drawable.account_circle_32);
        }
        else{
            Glide.with(mContext).load(user.getImageURL()).into(holder.imageView);
        }

        if(user.getStatus().equals("online")){
            holder.imageOnline.setVisibility(View.VISIBLE);
            holder.imageOffline.setVisibility(View.GONE);
        }
        else{
            holder.imageOnline.setVisibility(View.GONE);
            holder.imageOffline.setVisibility(View.VISIBLE);
        }
        if(isChat){
            handleLastMessage(user.getId(),holder.lastMessageTv);
        }
        else{
            holder.lastMessageTv.setVisibility(View.GONE);
        }

        holder.userNameTv.setText(user.getUsername());
    }

    @Override
    public int getItemCount() {
        if(userList != null){
            return userList.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView,imageOnline,imageOffline;
        private TextView userNameTv, lastMessageTv;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.user_image);
            userNameTv = itemView.findViewById(R.id.user_username);
            lastMessageTv = itemView.findViewById(R.id.user_last_message);
            imageOnline = itemView.findViewById(R.id.image_online);
            imageOffline = itemView.findViewById(R.id.image_offline);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userList.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra(FRIEND_ID,user.getId());
                    mContext.startActivity(intent);
                }
            });
        }


    }

    public void handleLastMessage(String friendId, TextView textView){

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    if(message.getSender().equals(currentUser.getUid()) && message.getReceiver().equals(friendId)
                    || message.getSender().equals(friendId) && message.getReceiver().equals(currentUser.getUid())){
                        if(message.getSender().equals(currentUser.getUid())){
                            lastMessage= "You: " + message.getContent();
                        }
                        else {
                            lastMessage = message.getContent();
                        }

                    }
                }

                textView.setText(lastMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
