package com.firstapp.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class register extends AppCompatActivity {
    TextInputLayout useremail, userpassword, userphone, username;
    Button add, next;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    /*private String normalemail(String email) {
        return email.replace(".", ",").replace("@", "_");
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        add = findViewById(R.id.adduser);
        next = findViewById(R.id.nextbtn);
        username = findViewById(R.id.name);
        useremail = findViewById(R.id.email);
        userpassword = findViewById(R.id.password);
        userphone = findViewById(R.id.phone);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("User");

                String email = useremail.getEditText().getText().toString();
                String password = userpassword.getEditText().getText().toString();
                String phone = userphone.getEditText().getText().toString();
                String name = username.getEditText().getText().toString();
                userdata helperClass = new userdata(email, password, phone, name);


                if(email.isEmpty()||password.isEmpty()||phone.isEmpty()||name.isEmpty()){
                    Toast.makeText(register.this, "Please fill all fields",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    reference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(name)){
                                Toast.makeText(register.this, "Username taken",Toast.LENGTH_SHORT).show();
                            }

                            else{

                                reference.child(name).setValue(helperClass);
                                Toast.makeText(register.this,"Registered successfully",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }


                //String emailwork = normalemail(email);
            }
        });

        /*next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, displaying.class);

                String name = username.getEditText().getText().toString();
                intent.putExtra("username", name);
                startActivity(intent);
            }
        });*/

    }

}