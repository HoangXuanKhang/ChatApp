package com.example.testchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.testchatapp.model.Message;
import com.example.testchatapp.model.MessageAdapter;
import com.example.testchatapp.model.User;
import com.example.testchatapp.model.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private String friendId,message,myId;
    private ImageButton sendBtn,galleryBtn,backBtn;
    private EditText messageEt;
    private CircleImageView imageViewToolbar;
    private TextView userNameTv;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        addControls();
        addEvents();
    }

    private void addEvents() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = messageEt.getText().toString().trim();
                sendMessage(myId,friendId,message);
            }
        });
    }

    private void sendMessage(String myId, String friendId, String message) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("sender",myId);
        hashMap.put("receiver",friendId);
        hashMap.put("content",message);

        databaseReference.child("Messages").push().setValue(hashMap);

        messageEt.setText("");
        messageEt.requestFocus();


        databaseReference = FirebaseDatabase.getInstance().getReference("ListChat").child(myId).child(friendId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    databaseReference.child("id").setValue(friendId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addControls() {
        sendBtn = findViewById(R.id.message_send_btn);
        galleryBtn = findViewById(R.id.message_gallery_btn);
        messageEt = findViewById(R.id.message_content_et);
        backBtn = findViewById(R.id.toolbar_back_btn);
        imageViewToolbar = findViewById(R.id.toolbar_image);
        userNameTv = findViewById(R.id.toolbar_username_tv);




        // receiver friend id
        friendId = getIntent().getStringExtra(UserAdapter.FRIEND_ID);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(friendId);

        // get my id
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myId = currentUser.getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userNameTv.setText(user.getUsername());

                if(user.getImageURL().equals("default")){
                    imageViewToolbar.setImageResource(R.drawable.girl);
                }
                else {
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(imageViewToolbar);
                }
//                Log.e("TAG", "myId " + myId );
//                Log.e("TAG", "friendId " + friendId );
//                Log.e("TAG", "URL " + user.getImageURL() );
                readMessage(myId,friendId,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recyclerView = findViewById(R.id.message_rcv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(true);

    }

    private void readMessage(String myId, String friendId, String imageURL) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Messages");
        messageList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    assert message != null;
                    if(message.getSender().equals(myId) && message.getReceiver().equals(friendId)
                            || message.getSender().equals(friendId) && message.getReceiver().equals(myId)){
                        messageList.add(message);
                    }
                }

                messageAdapter = new MessageAdapter(MessageActivity.this,messageList,imageURL);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}