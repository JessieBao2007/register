package com.firstapp.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    TextInputLayout username, password;
    Button login, register;
    //DatabaseReference reference;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://apptest-f7922-default-rtdb.firebaseio.com/");
    FirebaseDatabase rootNode;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    public static String currentuser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        username=findViewById(R.id.nameinput);
        password=findViewById(R.id.passwordinput);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        FirebaseApp.initializeApp(this);


        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


                rootNode = FirebaseDatabase.getInstance();
                //reference = rootNode.getReference("User");
                String name = username.getEditText().getText().toString();
                String pass = password.getEditText().getText().toString();



                if(name.isEmpty()||pass.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please fill username or password", Toast.LENGTH_SHORT).show();
                }

                else{
                    reference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.hasChild(name)) {
                                DataSnapshot userSnapshot = snapshot.child(name);
                                Log.d("User", userSnapshot.getValue().toString());
                                String getpass = userSnapshot.child("password").getValue(String.class);
                                if (getpass.equals(pass)) {
                                    Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                                    MainActivity.currentuser = name;
                                    startActivity(new Intent(MainActivity.this, other.class));
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });


        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this,register.class));
            }
        });
    }



}

