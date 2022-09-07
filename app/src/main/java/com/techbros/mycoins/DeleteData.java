package com.techbros.mycoins;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DeleteData extends AppCompatActivity {

    String dateRange;
    String startDateVal;
    String endDateVal;
    Pair<Long,Long> p;
    Button selectDate, delTrans, delFeed;
    TextView dateText,tv1,tv2;
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    ArrayList<FeedbackModel> feedbackArrayList = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference gRefDD = database.getReference("transactions");
    DatabaseReference refDD = database.getReference("feedbacks");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_data);
        selectDate = findViewById(R.id.dateButton);
        delTrans = findViewById(R.id.delete1);
        delFeed = findViewById(R.id.delete2);
        dateText = findViewById(R.id.textView11);
        tv1 = findViewById(R.id.dDTV);
        tv2 = findViewById(R.id.dDTV2);

        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        // now define the properties of the
        // materialDateBuilder
        materialDateBuilder.setTitleText("SELECT A DATE");
        // now create the instance of the material date
        // picker
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        // handle select date button which opens the
        // material design date picker

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        // if the user clicks on the positive
                        // button that is ok button update the
                        // selected date
                        dateRange  = materialDatePicker.getHeaderText();
                        dateText.setText(materialDatePicker.getHeaderText());
                        p = (Pair<Long, Long>) materialDatePicker.getSelection();
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd 00:00:00 z yyyy", Locale.ENGLISH);
                        startDateVal = formatter.format(p.first);
                        SimpleDateFormat formatter2 = new SimpleDateFormat("EEE MMM dd 23:59:59 z yyyy", Locale.ENGLISH);
                        endDateVal = formatter2.format(p.second);
                        loadData();
                        // in the above statement, getHeaderText
                        // will return selected date preview from the
                        // dialog
                    }
                });

        delTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deteteTransactions(transactionArrayList);
            }
        });
        delFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<feedbackArrayList.size();i++){
                    String fid = feedbackArrayList.get(i).getFid();
                    refDD.child(feedbackArrayList.get(i).getFid()).removeValue();
                }
            }
        });
    }

    private void deteteTransactions(ArrayList<Transaction> transactionArrayList) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference refdelete = database.getReference("transactions");
        Map<String,Object> map = new HashMap<>();
        for(int i=0;i<transactionArrayList.size();i++){
            map.put(transactionArrayList.get(i).gettId(),null);
        }
        // do something in the loop
        refdelete.updateChildren(map);
    }

    private void loadData() {
        gRefDD.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionArrayList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String tCoins  = dataSnapshot.child(key).child("tCoins").getValue().toString();
                    String tDate  = dataSnapshot.child(key).child("tDate").getValue().toString();
                    if(startDateVal!=null && endDateVal!=null){
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                        try {
                            Date start = formatter.parse(startDateVal);
                            Date end = formatter.parse(endDateVal);
                            Date trDate = formatter.parse(tDate);
                            if(!((trDate.after(start) && trDate.before(end)) ||
                                    (trDate.equals(start)) || trDate.equals(end))){
                                continue;
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    String tFrom  = dataSnapshot.child(key).child("tFrom").getValue().toString();
                    String tFromName = dataSnapshot.child(key).child("tFromName").getValue().toString();
                    String tTo  = dataSnapshot.child(key).child("tTo").getValue().toString();
                    String tToName = dataSnapshot.child(key).child("tFromName").getValue().toString();
                    String tId  = dataSnapshot.child(key).child("tId").getValue().toString();
                    String tType  = dataSnapshot.child(key).child("tType").getValue().toString();
                    String tToLocation = dataSnapshot.child(key).child("tLoc").getValue().toString();
                    transactionArrayList.add(new Transaction(Integer.valueOf(tCoins),tDate,tFrom,tFromName,tTo,tToName,tId,tType,tToLocation));
                }
                tv1.setText(transactionArrayList.size()+" transactions found for the time frame "+dateRange);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        refDD.orderByKey().addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feedbackArrayList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String id  = dataSnapshot.child(key).child("userId").getValue().toString();
                    String name  = dataSnapshot.child(key).child("userName").getValue().toString();
                    String comment  = dataSnapshot.child(key).child("feedback").getValue().toString();
                    String date = dataSnapshot.child(key).child("datetime").getValue().toString();
                    String fId = dataSnapshot.child(key).child("fId").getValue().toString();
                    if(startDateVal!=null && endDateVal!=null){
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                        try {
                            Date start = formatter.parse(startDateVal);
                            Date end = formatter.parse(endDateVal);
                            Date trDate = formatter.parse(date);
                            if(!((trDate.after(start) && trDate.before(end)) ||
                                    (trDate.equals(start)) || trDate.equals(end))){
                                continue;
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    feedbackArrayList.add(new FeedbackModel(comment,id,name,date,fId));
                }
                tv2.setText(feedbackArrayList.size()+" feedbacks found for the time frame "+dateRange);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });
    }
}