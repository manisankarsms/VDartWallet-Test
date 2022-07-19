package com.techbros.mycoins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Reports extends AppCompatActivity {

    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    ArrayList<String> transactionTypes = new ArrayList<>();

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference gRef = database.getReference("transactions");
    DatabaseReference ref = database.getReference("transactionType");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String tType = dataSnapshot.child(key).getValue().toString();
                    transactionTypes.add(tType);
                }
                Spinner spinner = findViewById(R.id.spinner);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,transactionTypes.toArray());
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                Toast.makeText(Reports.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                               String checkType = parent.getItemAtPosition(position).toString();
                                transactionArrayList.clear();
                                gRef.orderByKey().addValueEventListener(new ValueEventListener() {
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
                                            if(tType.equalsIgnoreCase(checkType))
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

                            public void onNothingSelected(AdapterView<?> parent) {
//                        showToast("Spinner1: unselected");
                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });



        gRef.orderByKey().addValueEventListener(new ValueEventListener() {
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

        ListView listView = findViewById(R.id.lv1);
        TransactionAdapter adapter = new TransactionAdapter(this, transactionArrayList);
        listView.setAdapter(adapter);
    }
}