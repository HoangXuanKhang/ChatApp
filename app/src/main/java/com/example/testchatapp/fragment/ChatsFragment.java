package com.example.testchatapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testchatapp.R;
import com.example.testchatapp.model.ListChat;
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
import java.util.List;


public class ChatsFragment extends Fragment {

    private List<User> userList;
    private List<ListChat> listChatList;
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        listChatList = new ArrayList<>();
        userList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.chat_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("ListChat").child(currentUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listChatList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ListChat listChat = dataSnapshot.getValue(ListChat.class);
                    listChatList.add(listChat);
                }


                getUserChated();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    private void getUserChated() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    for(ListChat listChat : listChatList){
                        if(user.getId().equals(listChat.getId())){
                            userList.add(user);
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(),userList,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}