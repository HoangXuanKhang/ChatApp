package com.example.testchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.testchatapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText usernameEt,emailEt,passwordEt;
    private TextInputLayout usernameLayout,emailLayout,passwordLayout;
    private Button registerBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        addControls();
        addEvents();
    }

    private void addEvents() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Objects.requireNonNull(usernameEt.getText()).toString().trim();
                String email = Objects.requireNonNull(emailEt.getText()).toString().trim();
                String password = Objects.requireNonNull(passwordEt.getText()).toString().trim();
                if(checkValidInput(username,email,password)){
                    registerAccount(username,email,password);
                }
            }
        });

        emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Patterns.EMAIL_ADDRESS.matcher(s.toString().trim()).matches()){
                    emailLayout.setError("Email invalid");
                }
                else {
                    emailLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() < 6){
                    passwordLayout.setError("Length must be 6 or more");
                }
                else{
                    passwordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void registerAccount(String username, String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    currentUser = mAuth.getCurrentUser();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("id",currentUser.getUid());
                    hashMap.put("username",username);
                    hashMap.put("email",email);
                    hashMap.put("imageURL","default");
                    hashMap.put("status","offline");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean checkValidInput(String username, String email, String password) {
        if(TextUtils.isEmpty(username)){
            usernameEt.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(email)){
            emailEt.requestFocus();
            return false;
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEt.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(password)){
            passwordEt.requestFocus();
            return false;
        }
        else if(password.length() < 6){
            passwordEt.requestFocus();
            return false;
        }
        else{
            usernameLayout.setError(null);
        }


        return true;
    }

    private void addControls() {
        usernameEt = findViewById(R.id.register_username_et);
        emailEt = findViewById(R.id.register_email_et);
        passwordEt = findViewById(R.id.register_password_et);
        registerBtn = findViewById(R.id.register_register_btn);
        progressBar = findViewById(R.id.register_pb);

        usernameLayout = findViewById(R.id.user_name_input);
        emailLayout = findViewById(R.id.email_input);
        passwordLayout = findViewById(R.id.password_input);

//        getSupportActionBar().setTitle("Register");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();


    }
}