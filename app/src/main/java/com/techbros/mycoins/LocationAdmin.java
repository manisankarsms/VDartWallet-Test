package com.techbros.mycoins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class LocationAdmin extends AppCompatActivity {

    ListView listView,listView1;
    String fromUserBalance,fromUserBalance1;
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    ArrayList<Transaction> coinsRequestList = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference gRefLA = database.getReference("transactions");
    DatabaseReference myRef2,myRef3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_admin);
        gRefLA.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionArrayList.clear();
                coinsRequestList.clear();
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
                    if (!tToLocation.equalsIgnoreCase(Login.location))
                        continue;
                    else if (tType.equalsIgnoreCase("CoinRequest")) {
                        coinsRequestList.add(new Transaction(Integer.valueOf(tCoins), tDate, tFrom, tFromName, tTo, tToName, tId, tType, tToLocation));
                        continue;}
                    else if (!(tType.equalsIgnoreCase("EncashRequest")))
                        continue;
                    transactionArrayList.add(new Transaction(Integer.valueOf(tCoins), tDate, tFrom, tFromName, tTo, tToName, tId, tType, tToLocation));
                }
                Collections.reverse(transactionArrayList);
                setListView(transactionArrayList);
                Collections.reverse(coinsRequestList);
                setListView1(coinsRequestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });
    }



    private void setListView(ArrayList<Transaction> transactionArrayList) {
        listView = findViewById(R.id.lvLoc);
        TransactionAdapter adapter = new TransactionAdapter(this, transactionArrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                String fromStore = transactionArrayList.get(position).gettFrom();
                int fromValue = transactionArrayList.get(position).gettCoins();
                String tId = transactionArrayList.get(position).gettId();
                myRef2 = database.getReference();
                myRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        fromUserBalance = dataSnapshot.child("userDetails").child(fromStore).child("balance").getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });

                new MaterialAlertDialogBuilder(LocationAdmin.this)
                        .setTitle("APPROVAL")
                        .setMessage("Are you sure to approve request from " + fromStore + " the total of " + fromValue + " Coins")
                        .setPositiveButton("APPROVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int fromBalance = Integer.valueOf(fromUserBalance) - Integer.valueOf(fromValue);
                                myRef2.child("userDetails").child(fromStore).child("balance").setValue(fromBalance);
                                myRef2.child("transactions").child(tId).child("tType").setValue("Encashed");
                                myRef2.child("transactions").child(tId).child("tToName").setValue(Login.uName);
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
        });
    }

    private void setListView1(ArrayList<Transaction> transactionArrayList) {
        listView1 = findViewById(R.id.lv11);
        TransactionAdapter adapter = new TransactionAdapter(this, transactionArrayList);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                String fromStore = transactionArrayList.get(position).gettFrom();
                int fromValue = transactionArrayList.get(position).gettCoins();
                String tId = transactionArrayList.get(position).gettId();
                myRef3 = database.getReference();
                myRef3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        fromUserBalance1 = dataSnapshot.child("userDetails").child(fromStore).child("balance").getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });

                new MaterialAlertDialogBuilder(LocationAdmin.this)
                        .setTitle("APPROVAL")
                        .setMessage("Are you sure to approve request from " + fromStore + " the total of " + fromValue + " Coins")
                        .setPositiveButton("APPROVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int fromBalance = Integer.valueOf(fromUserBalance1) + Integer.valueOf(fromValue);
                                myRef3.child("userDetails").child(fromStore).child("balance").setValue(fromBalance);
                                myRef3.child("transactions").child(tId).child("tType").setValue("Credit");
                                myRef3.child("transactions").child(tId).child("tToName").setValue(Login.uName);
                            }
                        })
                        .setNegativeButton("DENY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                myRef3.child("transactions").child(tId).child("tType").setValue("CreditRequestDenied");
                                myRef3.child("transactions").child(tId).child("tToName").setValue(Login.uName);
                            }
                        })
                        .show();
            }
        });
    }

}