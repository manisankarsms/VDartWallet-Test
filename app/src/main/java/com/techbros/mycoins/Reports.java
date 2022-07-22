package com.techbros.mycoins;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Reports extends AppCompatActivity {
    String startDateVal;
    String endDateVal;
    Pair<Long,Long> p;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Button exportExcel,selectDate;
    TextView dateText;
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    ArrayList<String> transactionTypes = new ArrayList<>();

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference gRef = database.getReference("transactions");
    DatabaseReference ref = database.getReference("transactionType");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Spinner spinner = findViewById(R.id.spinner);
        exportExcel = findViewById(R.id.export);
        selectDate = findViewById(R.id.dateButton);
        dateText = findViewById(R.id.textView11);
        // now create instance of the material date picker
        // builder make sure to add the "dateRangePicker"
        // which is material date range picker which is the
        // second type of the date picker in material design
        // date picker we need to pass the pair of Long
        // Long, because the start date and end date is
        // store as "Long" type value
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        // now define the properties of the
        // materialDateBuilder
        materialDateBuilder.setTitleText("SELECT A DATE");
        // now create the instance of the material date
        // picker
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        exportExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    exportIntoExcel(transactionArrayList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
                        dateText.setText(materialDatePicker.getHeaderText());
                        p = (Pair<Long, Long>) materialDatePicker.getSelection();
                        SimpleDateFormat formatter = new SimpleDateFormat();
                        startDateVal = formatter.format(p.first);
                        endDateVal = formatter.format(p.second);
                        if(spinner.getSelectedItemPosition()==4)
                            spinner.setSelection(0);
                        else
                            spinner.setSelection(4);
                        // in the above statement, getHeaderText
                        // will return selected date preview from the
                        // dialog
                    }
                });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String tType = dataSnapshot.child(key).getValue().toString();
                    transactionTypes.add(tType);
                }
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner_text,transactionTypes.toArray());
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
                                            if(startDateVal!=null && endDateVal!=null){
                                                SimpleDateFormat formatr = new SimpleDateFormat("dd/MM/yy");

                                                SimpleDateFormat formatr2 = new SimpleDateFormat("dd/MM/yy");
                                                try {
                                                    Date start = formatr.parse(startDateVal);
                                                    Date end = formatr.parse(endDateVal);
                                                    Date trDate = formatr2.parse(tDate);
                                                    if(!((trDate.after(start) && trDate.before(end)) ||
                                                            (trDate.equals(start)) || trDate.equals(end))){
                                                        continue;
                                                    }

                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            String tFrom  = dataSnapshot.child(key).child("tFrom").getValue().toString();
                                            String tTo  = dataSnapshot.child(key).child("tTo").getValue().toString();
                                            String tId  = dataSnapshot.child(key).child("tId").getValue().toString();
                                            String tType  = dataSnapshot.child(key).child("tType").getValue().toString();
                                            if(tType.equalsIgnoreCase(checkType))
                                                transactionArrayList.add(new Transaction(Integer.valueOf(tCoins),tDate,tFrom,tTo,tId,tType));
                                            else if(checkType.equalsIgnoreCase("All"))
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
        row.createCell(2).setCellValue("TO");
        row.createCell(3).setCellValue("COINS");
        row.createCell(4).setCellValue("DATE");
        row.createCell(5).setCellValue("TRANSACTION TYPE");

        for (Transaction t : transactionArrayList) {
            row = sheet.createRow(rowCount++);
            writeBook(t, row);
        }
        sheet.setDefaultColumnWidth(20);

        String fileName = Transaction.generateTId();
        verifyStoragePermissions(Reports.this);
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Transactions_"+fileName+".xls");
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
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
            row.createCell(2).setCellValue(t.tTo);
            row.createCell(3).setCellValue(t.tCoins);
            row.createCell(4).setCellValue(t.tDate);
            row.createCell(5).setCellValue(t.tType);
    }

    private void setListView(ArrayList<Transaction> transactionArrayList) {

        ListView listView = findViewById(R.id.lv1);
        TransactionAdapter adapter = new TransactionAdapter(this, transactionArrayList);
        listView.setAdapter(adapter);
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

}