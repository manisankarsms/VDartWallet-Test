package com.techbros.mycoins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.droidsonroids.gif.GifImageView;

public class Payment extends AppCompatActivity {

    Button send;
    TextInputLayout tVal;
    TextView tv1;
    String uId = Home.uId;
    String uName = Home.uName;
    String uBalance;
    String toUserBalance,toUser,toUserLocation;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference myRef1,myRef2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        send = findViewById(R.id.send);
        tVal = findViewById(R.id.tCoins);
        tv1 = findViewById(R.id.textView9);
        Bundle bundle = getIntent().getExtras();
        String toId = bundle.getString("payerId");

        myRef2 = database.getReference();
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 uBalance = dataSnapshot.child("userDetails").child(uId).child("balance").getValue().toString();
                 toUserBalance = dataSnapshot.child("userDetails").child(toId).child("balance").getValue().toString();
                 toUser = dataSnapshot.child("userDetails").child(toId).child("userName").getValue().toString();
                 toUserLocation = dataSnapshot.child("userDetails").child(toId).child("location").getValue().toString();
                 tv1.setText(toUser);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tValue = tVal.getEditText().getText().toString();
                myRef1 = database.getReference("userDetails");
                myRef2 = database.getReference();

                try{
                    int val = Integer.parseInt(tValue);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                    tVal.setError("Enter a Valid Value");
                    return;
                }
                if (Home.utilizable < Integer.valueOf(tValue)) {
                    tVal.setError("Maximum Utilizable Amount: "+Home.utilizable);
                }

                else if (Integer.valueOf(tValue) > Integer.valueOf(uBalance)) {
                    tVal.setError("Maximum Transferable Amount: "+uBalance);
                }
                else{
                    tVal.setError(null);
                    new MaterialAlertDialogBuilder(Payment.this)
                            .setTitle("CONFIRMATION")
                            .setMessage("You are paying "+toUser+" "+tValue+" Coins")
                            .setPositiveButton("PAY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    int toBalance = Integer.valueOf(toUserBalance) + Integer.valueOf(tValue);
                                    int fromBalance = Integer.valueOf(uBalance) - Integer.valueOf(tValue);
                                    myRef1.child(toId).child("balance").setValue(toBalance);
                                    myRef1.child(uId).child("balance").setValue(fromBalance);
                                    String tDate = Transaction.getDate();
                                    String tId = Transaction.generateTId();
                                    Transaction t = new Transaction(Integer.valueOf(tValue),tDate,uId,uName,toId,toUser,tId,"Payment",toUserLocation);
                                    myRef2.child("transactions").child(tId).setValue(t);
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    intent.putExtra("userId", uId);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                        return;
                                }
                            })
                            .show();


                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Home.class));
        finish();
    }
}