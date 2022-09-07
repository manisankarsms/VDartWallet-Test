package com.techbros.mycoins;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity {

    ValueEventListener valueEventListener;
    boolean shift1 = false, shift2=false;
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference myRefH,ref2H;
    DatabaseReference gRefH = database.getReference("transactions");
    DatabaseReference g1RefH = database.getReference("feedbacks");

    static String uName,uBalance,uLoc;
    static String uId;
    static int utilized,maxLimitV,utilizable;
    TextView tv1,tv2,maxLimit,utilizedTV;
    TextInputLayout feedback;
    Button btnSend,btnReq,btnFeedSub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tv1 = findViewById(R.id.username);
        tv2 = findViewById(R.id.textView2);
        feedback = findViewById(R.id.feedback);
        maxLimit = findViewById(R.id.maxLimit);
        utilizedTV = findViewById(R.id.utilized);
        btnSend = findViewById(R.id.button);
        btnReq = findViewById(R.id.materialButton9);
        btnFeedSub = findViewById(R.id.button4);
        uId = Login.uId;
        myRefH = database.getReference("userDetails/"+uId);

        ref2H = database.getReference();

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(Home.this)
                        .setTitle(tv1.getText().toString())
                        .setMessage("Want to Reset Password ?")
                        .setCancelable(false)
                        .setPositiveButton("RESET",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getApplicationContext(),ResetPassword.class));
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
        });

        gRefH.orderByKey().addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionArrayList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String tCoins  = dataSnapshot.child(key).child("tCoins").getValue().toString();
                    String tDate  = dataSnapshot.child(key).child("tDate").getValue().toString();
                    String tFrom  = dataSnapshot.child(key).child("tFrom").getValue().toString();
                    String tFromName = dataSnapshot.child(key).child("tFromName").getValue().toString();
                    String tTo  = dataSnapshot.child(key).child("tTo").getValue().toString();
                    String tToName = dataSnapshot.child(key).child("tToName").getValue().toString();
                    String tId  = dataSnapshot.child(key).child("tId").getValue().toString();
                    String tType  = dataSnapshot.child(key).child("tType").getValue().toString();
                    String tToLocation = dataSnapshot.child(key).child("tLoc").getValue().toString();
                    if(!(tFrom.equals(uId) || tTo.equals(uId)))
                        continue;
                    transactionArrayList.add(new Transaction(Integer.valueOf(tCoins),tDate,tFrom, tFromName,tTo, tToName,tId,tType,tToLocation));
                }
                Collections.reverse(transactionArrayList);
                setListView(transactionArrayList);
                utilized = 0;
                for(int i=0;i<transactionArrayList.size();i++){
                    if(transactionArrayList.get(i).gettType().equalsIgnoreCase("payment")) {

                        //Shift1 - 6AM to 6PM Day 1
                        //Shift2 - 6PM to 6AM Day 1 and 2
                        LocalDateTime datetime1 = LocalDateTime.now();
                        int Year1 = datetime1.getYear();
                        LocalDateTime datetime2 = datetime1.minusDays(1);
                        int Year2 = datetime2.getYear();
                        //Thu Aug 25 10:29:56 IST 2022
                        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("EEE MMM dd"); // Setting date format
                        Date date = new Date();
                        SimpleDateFormat formatter1 = new SimpleDateFormat("EEE MMM dd");
                        String dateVal1 = formatter1.format(date);
                        String dateVal2 = dtFormatter.format(datetime2);
                        try {
                            Calendar cal = Calendar.getInstance();
                            Date current = cal.getTime();
                            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                            cal.setTime(formatter.parse(dateVal1+" 06:00:00 GMT+05:30 "+Year1));
                            Date shift1Start = cal.getTime();
                            cal.setTime(formatter.parse(dateVal1+" 23:59:59 GMT+05:30  "+Year1));
                            Date shift1End = cal.getTime();
                            cal.setTime(formatter.parse(dateVal2+" 18:00:00 GMT+05:30 "+Year2));
                            Date shift2Start = cal.getTime();
                            cal.setTime(formatter.parse(dateVal1+" 06:00:00 GMT+05:30 "+Year1));
                            Date shift2End = cal.getTime();

                            if(current.after(shift1Start) && current.before(shift1End))
                                shift1=true;
                            else
                                shift2=true;

                        //String todayDate = Transaction.getDate();
                        String tDate = transactionArrayList.get(i).gettDate();
                           // Date today = formatr.parse(todayDate);
                            Date trDate = formatter.parse(tDate);
                            if (shift1) {
                                if(trDate.after(shift1Start)&&trDate.before(shift1End))
                                    utilized = utilized+Integer.valueOf(transactionArrayList.get(i).gettCoins());
                            } else{
                                if(trDate.after(shift2Start)&&trDate.before(shift2End))
                                    utilized = utilized+Integer.valueOf(transactionArrayList.get(i).gettCoins());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                utilizedTV.setText("Today used "+utilized+" Coins");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        valueEventListener = myRefH.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                uName = dataSnapshot.child("userName").getValue().toString();
                uLoc = dataSnapshot.child("location").getValue().toString();
                uBalance = dataSnapshot.child("balance").getValue().toString();
                tv1.setText("Hi, "+uName);
                tv2.setText(uBalance);

                String uType = dataSnapshot.child("userType").getValue().toString();
                ref2H.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(uType.equalsIgnoreCase("employee"))
                        maxLimitV = Integer.valueOf(dataSnapshot.child("coinsLimitEmp").getValue().toString());
                        else
                            maxLimitV = Integer.valueOf(dataSnapshot.child("coinsLimitGuest").getValue().toString());
                        maxLimit.setText("Limit/Day "+maxLimitV+" Coins");
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

        btnReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Home.this);
                //We have added a title in the custom layout. So let's disable the default title.
                //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
                //dialog.setCancelable(true);
                //Mention the name of the layout of your custom dialog.
                dialog.setContentView(R.layout.request_coins);
                Button btnReq = dialog.findViewById(R.id.sendCoinReq);
                btnReq.setEnabled(false);
                Button btnCancel = dialog.findViewById(R.id.materialButton10);

                TextInputLayout coinEt = dialog.findViewById(R.id.reqCoins);

                coinEt.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        if(s.length()==0){
                            btnReq.setEnabled(false);
                            coinEt.setError(null);
                        }
                        else {
                            try {
                                int val = Integer.parseInt(s.toString());
                                coinEt.setError(null);
                                btnReq.setEnabled(true);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                btnReq.setEnabled(false);
                                coinEt.setError("Enter a Valid Value");
                                return;
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                //Initializing the views of the dialog.
                dialog.show();

                btnReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int val = Integer.parseInt(coinEt.getEditText().getText().toString());
                        String tId = Transaction.generateTId();
                        Transaction t = new Transaction(val,Transaction.getDate(),Home.uId,Home.uName,"admin","admin",tId,"CoinRequest",uLoc);
                        ref2H.child("transactions").child(tId).setValue(t);
                        dialog.cancel();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

            }
        });

        btnFeedSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = feedback.getEditText().getText().toString();
                if(comment==null || comment.equalsIgnoreCase("")){
                    feedback.setError("Feedback cannot be empty");
                    return;
                }
                feedback.setError(null);
                String tId = Transaction.generateTId();
                g1RefH.child(tId).child("userId").setValue(Login.uId);
                g1RefH.child(tId).child("userName").setValue(Login.uName);
                g1RefH.child(tId).child("feedback").setValue(comment);
                g1RefH.child(tId).child("fId").setValue(tId);
                g1RefH.child(tId).child("datetime").setValue(Transaction.getDate());
                feedback.getEditText().setText(null);
                new MaterialAlertDialogBuilder(Home.this)
                        .setTitle("THANK YOU")
                        .setMessage("Your feedback has been sent")
                        .setCancelable(true)
                        .show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(Home.this)
                .setTitle("LOGOUT")
                .setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
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