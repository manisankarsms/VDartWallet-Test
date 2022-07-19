package com.techbros.mycoins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.Collections;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class HomeVendor extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference myRef;
    TextView tv1,tv2;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    ImageView qrCodeIV;
    Button btnEncash;
    String uId,uBalance;
    DatabaseReference gRef = database.getReference("transactions");
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_vendor);
        tv1 = findViewById(R.id.username);
        tv2 = findViewById(R.id.textView2);
        btnEncash = findViewById(R.id.materialButton);
        qrCodeIV = findViewById(R.id.imageView);

        Bundle bundle = getIntent().getExtras();
        uId = bundle.getString("userId");
        myRef = database.getReference("userDetails/"+uId);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uName = String.valueOf(dataSnapshot.child("userName").getValue());
                uBalance = String.valueOf(dataSnapshot.child("balance").getValue());
                tv1.setText(uName);
                tv2.setText(uBalance);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        gRef.orderByKey().limitToLast(50).addValueEventListener(new ValueEventListener() {
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
                    if(!(tFrom.equals(uId) || tTo.equals(uId)))
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

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        String id = encode(uId);
        qrgEncoder = new QRGEncoder(id, null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using setimagebitmap method.
            qrCodeIV.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }


        btnEncash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.valueOf(uBalance) > 0) {
                new MaterialAlertDialogBuilder(HomeVendor.this)
                        .setTitle("CONFIRM")
                        .setMessage("Are you sure to send the total of " + uBalance + " Coins")
                        .setPositiveButton("APPROVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String tDate = Transaction.getDate();
                                String tId = Transaction.generateTId();
                                Transaction t = new Transaction(Integer.valueOf(uBalance),tDate,uId,"superadmin",tId,"EncashRequest");
                                gRef.child(tId).setValue(t);
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
                else{
                    new MaterialAlertDialogBuilder(HomeVendor.this)
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

    private String encode(String uId){
        return Base64.encodeToString(uId.getBytes(),0);
    }

    private void setListView(ArrayList<Transaction> transactionArrayList) {

        ListView listView = findViewById(R.id.lv1);
        TransactionAdapter adapter = new TransactionAdapter(this, transactionArrayList);
        listView.setAdapter(adapter);
    }
}