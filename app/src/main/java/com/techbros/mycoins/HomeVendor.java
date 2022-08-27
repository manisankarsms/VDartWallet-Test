package com.techbros.mycoins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class HomeVendor extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference myRefHV;
    DatabaseReference myRefPass = database.getReference("storeEncashPasscode");
    TextView tv1,tv2;
    Bitmap bitmap,bmp,scaledbmp;
    QRGEncoder qrgEncoder;
    ImageView qrCodeIV;
    Button btnEncash,downQR;
    String uId,uBalance,uName,uLocation,ownerPass;
    DatabaseReference gRef = database.getReference("transactions");
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    int pageHeight = 1120;
    int pagewidth = 792;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_vendor);
        tv1 = findViewById(R.id.username);
       // tv2 = findViewById(R.id.textView2);
        btnEncash = findViewById(R.id.materialButton);
        downQR = findViewById(R.id.downQR);
        qrCodeIV = findViewById(R.id.imageView);

        uId = Login.uId;

        init();

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


//        btnEncash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Integer.valueOf(uBalance) > 0) {
//                new MaterialAlertDialogBuilder(HomeVendor.this)
//                        .setTitle("CONFIRM")
//                        .setMessage("Are you sure to send the total of " + uBalance + " Coins")
//                        .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                String tDate = Transaction.getDate();
//                                String tId = Transaction.generateTId();
//                                Transaction t = new Transaction(Integer.valueOf(uBalance),tDate,uId,uName,"superadmin","VDART",tId,"EncashRequest",uLocation);
//                                gRef.child(tId).setValue(t);
//                                return;
//                            }
//                        })
//                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                return;
//                            }
//                        })
//                        .show();
//                }
//                else{
//                    new MaterialAlertDialogBuilder(HomeVendor.this)
//                            .setTitle("ALERT")
//                            .setMessage("Low Balance")
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    return;
//                                }
//                            })
//                            .show();
//                }
//            }
//        });

        btnEncash.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             final Dialog dialog = new Dialog(HomeVendor.this);
                                             dialog.setContentView(R.layout.passcode_verify);
                                             //Initializing the views of the dialog.
                                             Button verify = dialog.findViewById(R.id.materialButton11);
                                             final TextInputLayout passEt = dialog.findViewById(R.id.passcode);

                                             passEt.getEditText().addTextChangedListener(new TextWatcher() {
                                                 @Override
                                                 public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                 }

                                                 @Override
                                                 public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                     if (s.length() > 0) {
                                                         verify.setEnabled(true);
                                                     } else
                                                         verify.setEnabled(false);
                                                 }

                                                 @Override
                                                 public void afterTextChanged(Editable s) {

                                                 }
                                             });

                                             verify.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     uId = Login.uId;
                                                     String password = passEt.getEditText().getText().toString();
                                                     if(password.equals(ownerPass)){
                                                         passEt.setError(null);
                                                         dialog.cancel();
                                                         Intent intent = new Intent(getApplicationContext(), VendorMain.class);
                                                         startActivity(intent);
                                                     }
                                                     else{
                                                         passEt.setError("Incorrect Passcode");
                                                     }
                                                 }
                                             });
                                             dialog.show();
                                         }
        });

                downQR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.vdart_logo);
                        scaledbmp = Bitmap.createScaledBitmap(bmp, 250, 250, false);
                        generatePDF();
                    }
                });
            }
                private void generatePDF () {
                    PdfDocument pdfDocument = new PdfDocument();

                    // two variables for paint "paint" is used
                    // for drawing shapes and we will use "title"
                    // for adding text in our PDF file.
                    Paint paint = new Paint();
                    Paint title = new Paint();

                    // we are adding page info to our PDF file
                    // in which we will be passing our pageWidth,
                    // pageHeight and number of pages and after that
                    // we are calling it to create our PDF.
                    PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

                    // below line is used for setting
                    // start page for our PDF file.
                    PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

                    // creating a variable for canvas
                    // from our page of PDF.
                    Canvas canvas = myPage.getCanvas();

                    // below line is used to draw our image on our PDF file.
                    // the first parameter of our drawbitmap method is
                    // our bitmap
                    // second parameter is position from left
                    // third parameter is position from top and last
                    // one is our variable for paint.
                    //canvas.drawBitmap(scaledbmp, 312, 80, paint);
                    scaledbmp = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                    canvas.drawBitmap(scaledbmp, 150, 250, paint);

                    // below line is used for adding typeface for
                    // our text which we will be adding in our PDF file.
                    title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

                    // below line is used for setting text size
                    // which we will be displaying in our PDF file.
                    title.setTextSize(50);

                    // below line is sued for setting color
                    // of our text inside our PDF file.
                    title.setColor(ContextCompat.getColor(this, R.color.black));

                    // below line is used to draw text in our PDF file.
                    // the first parameter is our text, second parameter
                    // is position from start, third parameter is position from top
                    // and then we are passing our variable of paint which is title.
                    ((Canvas) canvas).drawText("VDart Wallet", 250, 100, title);


                    title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    title.setColor(ContextCompat.getColor(this, R.color.black));
                    title.setTextSize(50);
                    canvas.drawText(uName, 280, 200, title);
                    // similarly we are creating another text and in this
                    // we are aligning this text to center of our PDF file.
                    title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                    title.setColor(ContextCompat.getColor(this, R.color.black));
                    title.setTextSize(50);

                    // below line is used for setting
                    // our text to center of PDF.
                    title.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText("Scan QR to Pay", 400, 1000, title);

                    // after adding all attributes to our
                    // PDF file we will be finishing our page.
                    pdfDocument.finishPage(myPage);

                    // below line is used to set the name of
                    // our PDF file and its path.
                    File file = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS), "QR_" + uName + ".pdf");

                    try {
                        // after creating a file name we will
                        // write our PDF file to that location.
                        pdfDocument.writeTo(new FileOutputStream(file));

                        // below line is to print toast message
                        // on completion of PDF generation.
                        Toast.makeText(HomeVendor.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        // below line is used
                        // to handle error
                        e.printStackTrace();
                    }
                    // after storing our pdf to that
                    // location we are closing our PDF file.
                    pdfDocument.close();
                }

    private String encode(String uId){
        return Base64.encodeToString(uId.getBytes(),0);
    }

    private void setListView(ArrayList<Transaction> transactionArrayList) {

        ListView listView = findViewById(R.id.lv1);
        TransactionAdapter adapter = new TransactionAdapter(this, transactionArrayList);
        listView.setAdapter(adapter);
    }

    private boolean verify(String dbPass, String password) {
        return dbPass.equals(password);
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(HomeVendor.this)
                .setTitle("LOGOUT")
                .setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(getApplicationContext(),Login.class));
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
    void init(){
        myRefHV = database.getReference("userDetails/" + uId);

        myRefHV.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uName = String.valueOf(dataSnapshot.child("userName").getValue());
                uBalance = String.valueOf(dataSnapshot.child("balance").getValue());
                uLocation = String.valueOf(dataSnapshot.child("location").getValue());
                tv1.setText(uName);
                //tv2.setText(uBalance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        myRefPass.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ownerPass = String.valueOf(dataSnapshot.child(Login.uId).getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        gRef.orderByKey().limitToLast(50).addValueEventListener(new ValueEventListener() {
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
                    if(tType.equalsIgnoreCase("EncashRequest")||tType.equalsIgnoreCase("Encashed"))
                        continue;
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
    }
}