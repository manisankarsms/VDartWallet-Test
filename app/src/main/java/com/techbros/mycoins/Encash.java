package com.techbros.mycoins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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

public class Encash extends AppCompatActivity {
    String fromUserBalance;
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference gRef = database.getReference("transactions");
    DatabaseReference myRef2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encash);

        gRef.orderByKey().limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String tCoins  = dataSnapshot.child(key).child("tCoins").getValue().toString();
                    String tDate  = dataSnapshot.child(key).child("tDate").getValue().toString();
                    String tFrom  = dataSnapshot.child(key).child("tFrom").getValue().toString();
                    String tTo  = dataSnapshot.child(key).child("tTo").getValue().toString();
                    String tId  = dataSnapshot.child(key).child("tId").getValue().toString();
                    String tType  = dataSnapshot.child(key).child("tType").getValue().toString();
                    if(!(tType.equals("EncashRequest")))
                        continue;
                    transactionArrayList.add(new Transaction(Integer.valueOf(tCoins),tDate,tFrom,tTo,tId,tType));
                }
                Collections.reverse(transactionArrayList);
                setListView(transactionArrayList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });
    }
    private void setListView(ArrayList<Transaction> transactionArrayList) {
        ListView listView = findViewById(R.id.eRLv1);
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

                //Toast.makeText(getApplicationContext(),"Item"+arrayList.get(position).getBlock(), Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(getApplicationContext(), UserDetailsDisplay.class);
//                i.putExtra("index", arrayList.get(position).getIndex());
//                i.putExtra("block", arrayList.get(position).getBlock());
//                i.putExtra("flatNumber", arrayList.get(position).getFlatNumber());
//                i.putExtra("name", arrayList.get(position).getName());
//                i.putExtra("phone", arrayList.get(position).getPhone());
//                i.putExtra("occupied", arrayList.get(position).getOccupied());
//                i.putExtra("tenantName", arrayList.get(position).getTenantName());
//                startActivity(i);

                new MaterialAlertDialogBuilder(Encash.this)
                        .setTitle("APPROVAL")
                        .setMessage("Are you sure to approve request from " + fromStore + " the total of " + fromValue + " Coins")
                        .setPositiveButton("APPROVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int fromBalance = Integer.valueOf(fromUserBalance) - Integer.valueOf(fromValue);
                                myRef2.child("userDetails").child(fromStore).child("balance").setValue(fromBalance);
                                myRef2.child("transactions").child(tId).child("tType").setValue("Encashed");
                                return;
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
}
