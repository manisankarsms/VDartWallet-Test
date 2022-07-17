package com.techbros.mycoins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference myRef = database.getReference("userDetails");

    CircularProgressIndicator progressIndicator;
    TextInputLayout et1, et2;
    Button b1;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et1 = findViewById(R.id.empId);
        et2 = findViewById(R.id.password);
        iv = findViewById(R.id.imageView3);
        progressIndicator = findViewById(R.id.progress_circular);
        progressIndicator.setVisibility(View.INVISIBLE);
        b1 = findViewById(R.id.login);

        b1.setOnClickListener(v -> {
            String user = et1.getEditText().getText().toString();
            String password = et2.getEditText().getText().toString();
            progressIndicator.setVisibility(View.VISIBLE);

            Query query = myRef
                    .orderByChild("id")
                    .equalTo(user);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount()>0) {
                        et1.setError(null);
                        //username found
                        myRef.child(user).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String dbPass = String.valueOf(dataSnapshot.child("password").getValue());
                                boolean verify = verify(dbPass,password);
                                if(!verify){
                                    progressIndicator.setVisibility(View.INVISIBLE);
                                    et2.setError("Incorrect Password");
                                    //et2.setHelperText("Incorrect Password");
                                    //Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    et2.setError(null);
                                    String userType = String.valueOf(dataSnapshot.child("userType").getValue());
                                    switch (userType) {
                                        case "employee": {
                                            Intent intent = new Intent(getApplicationContext(), Home.class);
                                            intent.putExtra("userId", user);
                                            finish();
                                            startActivity(intent);
                                            break;
                                        }
                                        case "superadmin": {
                                            Intent intent = new Intent(getApplicationContext(), SuperAdminDashboard.class);
                                            startActivity(intent);
                                            finish();
                                            break;
                                        }
                                        case "store": {
                                            Intent intent = new Intent(getApplicationContext(), HomeVendor.class);
                                            intent.putExtra("userId", user);
                                            finish();
                                            startActivity(intent);
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                            }
                        });

                    }else{
                        // username not found
                        progressIndicator.setVisibility(View.INVISIBLE);
                        et1.setError("User Invalid");
//                        et1.setHelperText("User Invalid");
                        Toast.makeText(getApplicationContext(), "User Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

    }

    private boolean verify(String dbPass, String password) {
        return dbPass.equals(password);
    }
}