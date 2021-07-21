package com.example.testchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.testchatapp.fragment.ChatsFragment;
import com.example.testchatapp.fragment.MyViewPagerAdapter;
import com.example.testchatapp.fragment.ProfileFragment;
import com.example.testchatapp.fragment.UsersFragment;
import com.example.testchatapp.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ActionBar actionBar;
    private DatabaseReference reference;
    private TabLayout tabLayout;
    private MyViewPagerAdapter myViewPagerAdapter;
    private ViewPager viewPager;
    private String myId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addControls();
    }

    private void addControls() {
        actionBar = getSupportActionBar();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
//
//                String username = user != null ? user.getUsername() : null;
//                actionBar.setTitle(username);
                

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });


        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);

        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        myViewPagerAdapter.addFragment(new ChatsFragment(),"Chat");
        myViewPagerAdapter.addFragment(new UsersFragment(),"Users");
        myViewPagerAdapter.addFragment(new ProfileFragment(),"Profile");
        viewPager.setAdapter(myViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myId = currentUser.getUid();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_logout){
            new AlertDialog.Builder(this)
                    .setTitle("Log out")
                    .setMessage("Do you want to log out app")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(currentUser != null){
                                mAuth.signOut();
                                Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setStatus(String status){


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(myId);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("TAG", "onPause: "  );
        setStatus("offline");
    }

}