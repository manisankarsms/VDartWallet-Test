package com.techbros.mycoins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class VendorMain extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference myRefVM;
    Button btnEncash;
    String uId,uBalance,uName,uLocation,ownerPass;
    DatabaseReference gRefVM = database.getReference("transactions");
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_main);
        TextView tv = findViewById(R.id.textView15);
        btnEncash = findViewById(R.id.button3);

        uId = Login.uId;
        myRefVM = database.getReference("userDetails/" + uId);

        myRefVM.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uName = String.valueOf(dataSnapshot.child("userName").getValue());
                uBalance = String.valueOf(dataSnapshot.child("balance").getValue());
                uLocation = String.valueOf(dataSnapshot.child("location").getValue());
                tv.setText(uBalance+" COINS");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        gRefVM.orderByKey().limitToLast(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String tCoins = dataSnapshot.child(key).child("tCoins").getValue().toString();
                    String tDate = dataSnapshot.child(key).child("tDate").getValue().toString();
                    String tFrom = dataSnapshot.child(key).child("tFrom").getValue().toString();
                    String tFromName = dataSnapshot.child(key).child("tFromName").getValue().toString();
                    String tTo = dataSnapshot.child(key).child("tTo").getValue().toString();
                    String tToName = dataSnapshot.child(key).child("tToName").getValue().toString();
                    String tId = dataSnapshot.child(key).child("tId").getValue().toString();
                    String tType = dataSnapshot.child(key).child("tType").getValue().toString();
                    String tToLocation = dataSnapshot.child(key).child("tLoc").getValue().toString();
                    if (!(tFrom.equals(uId) || tTo.equals(uId)))
                        continue;
                    transactionArrayList.add(new Transaction(Integer.valueOf(tCoins), tDate, tFrom, tFromName, tTo, tToName, tId, tType, tToLocation));
                }
                Collections.reverse(transactionArrayList);
                setListView(transactionArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        btnEncash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag=false;
                for(int i=0;i<transactionArrayList.size();i++){
                    if(transactionArrayList.get(i).gettType().equalsIgnoreCase("EncashRequest")){
                        flag = true;
                    }
                }
                if(flag){
                    new MaterialAlertDialogBuilder(VendorMain.this)
                            .setTitle("ALERT...PLEASE WAIT!")
                            .setMessage("Already Request Submitted or Waiting of approval of Old Request")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            })
                            .show();
                }
                else if (Integer.valueOf(uBalance) > 0) {
                new MaterialAlertDialogBuilder(VendorMain.this)
                        .setTitle("CONFIRM")
                        .setMessage("Are you sure to send the total of " + uBalance + " Coins")
                        .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String tDate = Transaction.getDate();
                                String tId = Transaction.generateTId();
                                Transaction t = new Transaction(Integer.valueOf(uBalance),tDate,uId,uName,"superadmin","VDART",tId,"EncashRequest",uLocation);
                                gRefVM.child(tId).setValue(t);
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
                else{
                    new MaterialAlertDialogBuilder(VendorMain.this)
                            .setTitle("ALERT")
                            .setMessage("Low Balance")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
    private void setListView(ArrayList<Transaction> transactionArrayList) {

        ListView listView = findViewById(R.id.lv1);
        TransactionAdapter adapter = new TransactionAdapter(this, transactionArrayList);
        listView.setAdapter(adapter);
    }
}