package com.techbros.mycoins;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.cert.PolicyNode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Home extends AppCompatActivity {

    ValueEventListener valueEventListener;
    TabLayout tabLayout;
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference myRef,ref2,myRef1;
    DatabaseReference gRef = database.getReference("transactions");
    static String uName,uBalance;
    String tTo,tValue;
    static String uId;
    static int utilized,maxLimitV,utilizable;
    String tCountDay,tCountTotal,lastTransactedDate;
    String fromUserBalance,toUserBalance;
    TextView tv1,tv2,maxLimit,utilizedTV;
    EditText et1,et2;
    Button btnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tv1 = findViewById(R.id.username);
        tv2 = findViewById(R.id.textView2);
        maxLimit = findViewById(R.id.maxLimit);
        utilizedTV = findViewById(R.id.utilized);
        btnSend = findViewById(R.id.button);
        Bundle bundle = getIntent().getExtras();
        uId = bundle.getString("userId");
//        uId = "12345";
        myRef = database.getReference("userDetails/"+uId);
        //setListView();

        ref2 = database.getReference();

        gRef.orderByKey().limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionArrayList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String tCoins  = dataSnapshot.child(key).child("tCoins").getValue().toString();
                    String tDate  = dataSnapshot.child(key).child("tDate").getValue().toString();
                    String tFrom  = dataSnapshot.child(key).child("tFrom").getValue().toString();
                    String tTo  = dataSnapshot.child(key).child("tTo").getValue().toString();
                    String tId  = dataSnapshot.child(key).child("tId").getValue().toString();
                    String tType  = dataSnapshot.child(key).child("tType").getValue().toString();
                    if(!(tFrom.equals(uId) || tTo.equals(uId)))
                        continue;
                    transactionArrayList.add(new Transaction(Integer.valueOf(tCoins),tDate,tFrom,tTo,tId,tType));
                }
                Collections.reverse(transactionArrayList);
                setListView(transactionArrayList);
                utilized = 0;
                for(int i=0;i<transactionArrayList.size();i++){
                    if(transactionArrayList.get(i).gettType().equalsIgnoreCase("payment")) {
                        String todayDate = Transaction.getDate();
                        String tDate = transactionArrayList.get(i).gettDate();
                        SimpleDateFormat formatr = new SimpleDateFormat("dd/MM/yy");
                        try {
                            Date today = formatr.parse(todayDate);
                            Date trDate = formatr.parse(tDate);
                            if (!trDate.equals(today)) {
                                continue;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        utilized = utilized+Integer.valueOf(transactionArrayList.get(i).gettCoins());
                    }
                }
                utilizedTV.setText("Coins Spent Today "+utilized);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        valueEventListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                uName = dataSnapshot.child("userName").getValue().toString();
                uBalance = dataSnapshot.child("balance").getValue().toString();
                tv1.setText("Hi, "+uName);
                tv2.setText(uBalance);

                String uType = dataSnapshot.child("userType").getValue().toString();
                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(uType.equalsIgnoreCase("employee"))
                        maxLimitV = Integer.valueOf(dataSnapshot.child("coinsLimitEmp").getValue().toString());
                        else
                            maxLimitV = Integer.valueOf(dataSnapshot.child("coinsLimitGuest").getValue().toString());
                        maxLimit.setText("Max Limit/Day "+maxLimitV+" coins");
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                myRef.removeEventListener(valueEventListener);
                utilizable = maxLimitV - utilized;
                if(maxLimitV<=utilized){
                    new MaterialAlertDialogBuilder(Home.this)
                            .setTitle("ALERT")
                            .setMessage("MAXIMUM LIMIT REACHED")
                            .setCancelable(true)
                            .show();

                    return;
                }
                else {
                    startActivity(new Intent(getApplicationContext(), ScanBarCodeActivity.class));
                }
            }
        });

        //updateCoins();
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(Home.this)
                .setTitle("ALERT")
                .setMessage("Are you sure to logout")
                .setCancelable(false)
                .setPositiveButton("LOGOUT",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();
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

    private void setListView(ArrayList<Transaction> transactionArrayList) {

        ListView listView = findViewById(R.id.lv1);
        TransactionAdapter adapter = new TransactionAdapter(this, transactionArrayList);
        listView.setAdapter(adapter);
    }
}