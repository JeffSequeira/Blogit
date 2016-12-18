package com.jeff.blogit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText mLoginEmail;
    private EditText mLoginPassword;
    private Button btnSignIn;
    private Button btnGoToRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth= FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);// make sure it stores the data offline
        mProgress= new ProgressDialog(this);

        mLoginEmail=(EditText) findViewById(R.id.etLoginEmail);
        mLoginPassword=(EditText) findViewById(R.id.etLoginPassword);
        btnSignIn=(Button) findViewById(R.id.btnSignIn);
        btnGoToRegister=(Button) findViewById(R.id.btnGoToRegister);


        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent RegisterIntent = new Intent(LoginActivity.this,RegisterActivity.class);

                //User Wont be able to go back
                RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(RegisterIntent);

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
    }

    private void checkLogin() {

        String email = mLoginEmail.getText().toString().trim();
        String password = mLoginPassword.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Signing In...");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        mProgress.dismiss();
                        checkUserExists();

                    }else{
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this,"Error Logging In",Toast.LENGTH_LONG).show();
                    }

                }
            });

        }

    }

    private void checkUserExists() {

        final String user_id = mAuth.getCurrentUser().getUid();

        //addValueEventListener to check if the user exists in the database or not
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)){
                    Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);

                    //User Wont be able to go back
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(mainIntent);

                }else{
                    Intent setupIntent = new Intent(LoginActivity.this,SetupActivity.class);

                    //User Wont be able to go back
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(setupIntent);
                   // Toast.makeText(LoginActivity.this,"You need to setup your account",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
