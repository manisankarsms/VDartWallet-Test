package com.techbros.mycoins;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class VendorMain extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference myRefVM;
    Button btnEncash,export;
    String uId,uBalance,uName,uLocation,ownerPass;
    DatabaseReference gRefVM = database.getReference("transactions");
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_main);
        TextView tv = findViewById(R.id.textView15);
        btnEncash = findViewById(R.id.button3);
        export = findViewById(R.id.button4);

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

        gRefVM.orderByKey().addValueEventListener(new ValueEventListener() {
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

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    exportIntoExcel(transactionArrayList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        }

    private void exportIntoExcel(ArrayList<Transaction> transactionArrayList) throws IOException {
        // Blank workbook
        HSSFWorkbook workbook = new HSSFWorkbook();

        // Creating a blank Excel sheet
        HSSFSheet sheet = workbook.createSheet("Report");
        int rowCount = 1;
        Row row;
        row = sheet.createRow(0);
        Cell cell;
        cell = row.createCell(0);
        cell.setCellValue("TRANSACTION ID");
        row.createCell(1).setCellValue("FROM");
        row.createCell(2).setCellValue("FROM NAME");
        row.createCell(3).setCellValue("TO");
        row.createCell(4).setCellValue("TO NAME");
        row.createCell(5).setCellValue("TRANSACTION TYPE");
        row.createCell(6).setCellValue("LOCATION");
        row.createCell(7).setCellValue("DATE");
        row.createCell(8).setCellValue("COINS");


        for (Transaction t : transactionArrayList) {
            row = sheet.createRow(rowCount++);
            writeBook(t, row);
        }
        sheet.setDefaultColumnWidth(20);

        String fileName = Transaction.generateTId();
        verifyStoragePermissions(VendorMain.this);
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Store_Transactions_"+fileName+".xls");
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Uri uri = Uri.parse(String.valueOf(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS)));
            new MaterialAlertDialogBuilder(this)
                    .setTitle("DOWNLOAD")
                    .setMessage("Report Downloaded Successfully!")
                    .setCancelable(false)
                    .setPositiveButton("OPEN LOCATION",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openDirectory(uri);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    })
                    .show();
            Log.e(TAG, "Writing file" + file);
        } catch (IOException e) {
            Log.e(TAG, "Error writing Exception: ", e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to Exception: ", e);
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void writeBook(Transaction t, Row row) {
        Cell cell = row.createCell(0);
        cell.setCellValue(t.gettId());
        row.createCell(1).setCellValue(t.tFrom);
        row.createCell(2).setCellValue(t.tFromName);
        row.createCell(3).setCellValue(t.tTo);
        row.createCell(4).setCellValue(t.tToName);
        row.createCell(5).setCellValue(t.tType);
        row.createCell(6).setCellValue(t.tLoc);
        row.createCell(7).setCellValue(t.tDate);
        row.createCell(8).setCellValue(t.tCoins);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We dont have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    private void setListView(ArrayList<Transaction> transactionArrayList) {

        ListView listView = findViewById(R.id.lv1);
        TransactionAdapter adapter = new TransactionAdapter(this, transactionArrayList);
        listView.setAdapter(adapter);
    }
    public void openDirectory(Uri uriToLoad) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setDataAndType(uriToLoad,  "application/vnd.ms-excel");
        startActivity(intent);
    }
}