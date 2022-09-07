package com.techbros.mycoins;

import static android.content.ContentValues.TAG;
import static android.provider.CalendarContract.CalendarCache.URI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EmployeeAddDeleteUpdate extends AppCompatActivity {

    UserAdapter adapter;
    ListView listView;
    SearchView search;
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    ArrayList<UserModel> userAddArrayList = new ArrayList<>();
    static ArrayList<UserModel> userArrayList = new ArrayList<>();
    ArrayList<UserModel> userArrayListSearch = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref1 = database.getReference("userDetails");
    DatabaseReference ref2 = database.getReference("locationDetails");
    DatabaseReference ref3 = database.getReference("userDetails");
    DatabaseReference ref4 = database.getReference("transactions");
    DatabaseReference ref5 = database.getReference("userType");
    DatabaseReference ref6 = database.getReference("userDetails");
    DatabaseReference ref7 = database.getReference("storeEncashPasscode");
    DatabaseReference ref8 = database.getReference("userStatus");
    DatabaseReference ref9 = database.getReference("userDetails");


    ArrayList<String> locationDetails = new ArrayList<>();
    ArrayList<String> typeDetails = new ArrayList<>();
    ArrayList<String> statusDetails = new ArrayList<>();
    Spinner spinner;
    TextInputLayout creditCoins;
    Button credit,addEmp,btnExport;
    RadioGroup rGrp;
    RadioButton rAll,rLoc;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        setContentView(R.layout.activity_employee_add_delete_update);
        spinner = findViewById(R.id.spinner);
        creditCoins = findViewById(R.id.creditCoins);
         credit = findViewById(R.id.materialButton5);
         addEmp = findViewById(R.id.button2);
         btnExport = findViewById(R.id.button3);
         rGrp = findViewById(R.id.rGroup);
         rAll = findViewById(R.id.radioAll);
         rLoc = findViewById(R.id.radioLoc);


        adapter = new UserAdapter(this, null);
        search = findViewById(R.id.search);
        listView = findViewById(R.id.lv2);

        init();
    }

    void init(){
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                userArrayListSearch.clear();
                for (UserModel user : userArrayList) {
                    if (user.getUserName().toLowerCase().contains(newText.toLowerCase()) || user.getId().toLowerCase().contains(newText.toLowerCase()) || user.getUserType().toLowerCase().contains(newText.toLowerCase()))
                        userArrayListSearch.add(user);
                }
                adapter = new UserAdapter(EmployeeAddDeleteUpdate.this, userArrayListSearch);
                listView.setAdapter(adapter);

                return false;
            }
        });

        ref1.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String userId = String.valueOf(dataSnapshot.child(key).child("id").getValue());
                    String userName = dataSnapshot.child(key).child("userName").getValue().toString();
                    String userBalance = dataSnapshot.child(key).child("balance").getValue().toString();
                    String userPassword = dataSnapshot.child(key).child("password").getValue().toString();
                    String userType = dataSnapshot.child(key).child("userType").getValue().toString();
                    String userLocation = dataSnapshot.child(key).child("location").getValue().toString();
                    String userStatus = dataSnapshot.child(key).child("status").getValue().toString();
                    userArrayList.add(new UserModel(userId, userName, userBalance, userPassword, userType, userLocation, userStatus));
                }
                setListView(userArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });



        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String loc = dataSnapshot.child(key).getValue().toString();
                    locationDetails.add(loc);
                }
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner_text, locationDetails.toArray());
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        creditCoins.getEditText().setText(null);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String uType = dataSnapshot.child(key).getValue().toString();
                    typeDetails.add(uType);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref8.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String sType = dataSnapshot.child(key).getValue().toString();
                    statusDetails.add(sType);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(EmployeeAddDeleteUpdate.this);
                //We have added a title in the custom layout. So let's disable the default title.
                //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
                //dialog.setCancelable(true);
                //Mention the name of the layout of your custom dialog.
                dialog.setContentView(R.layout.user_dialog);
                //Initializing the views of the dialog.
                final TextView idTv = dialog.findViewById(R.id.userId);
                final TextInputLayout nameEt = dialog.findViewById(R.id.userName);
                final TextInputLayout balanceEt = dialog.findViewById(R.id.userBalance);
                final Spinner typeEt = dialog.findViewById(R.id.spinner);
                final Spinner locET = dialog.findViewById(R.id.spinner2);
                final Spinner statusET = dialog.findViewById(R.id.spinner3);
                final TextInputLayout passwordEt = dialog.findViewById(R.id.userPassword);
                //final TextInputLayout locationEt = dialog.findViewById(R.id.userLocation);
                Button updateButton = dialog.findViewById(R.id.materialButton2);
                Button cancelButton = dialog.findViewById(R.id.materialButton3);
                Button resetPass = dialog.findViewById(R.id.rPass);
                Button add = dialog.findViewById(R.id.materialButton6);

                resetPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MaterialAlertDialogBuilder(EmployeeAddDeleteUpdate.this)
                                .setTitle("ALERT...PASSWORD RESET!!")
                                .setMessage("Are you sure to reset the password for "+userArrayListSearch.get(position).getUserName()+" ?")
                                .setCancelable(true)
                                .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ref6.child(userArrayListSearch.get(position).getId())
                                                .child("password")
                                                .setValue(userArrayListSearch.get(position).getId());
                                        Toast.makeText(getApplicationContext(), "RESET SUCCESSFUL!!!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        return;
                                    }
                                })
                                .show();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog1 = new Dialog(EmployeeAddDeleteUpdate.this);
                        dialog1.setContentView(R.layout.add_coins);
                        Button addC = dialog1.findViewById(R.id.materialButton7);
                        Button cancelB = dialog1.findViewById(R.id.materialButton8);
                        final TextInputLayout coinsET = dialog1.findViewById(R.id.coinsValue);

//                            dialog.cancel();
                        dialog1.show();
                        addC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String coins = coinsET.getEditText().getText().toString();
                                String id = idTv.getText().toString();
                                String name = nameEt.getEditText().getText().toString();
                                String loc = locET.getSelectedItem().toString();
                                String pass = passwordEt.getEditText().getText().toString();
                                String type = typeEt.getSelectedItem().toString();
                                String bal = balanceEt.getEditText().getText().toString();
                                String status = statusET.getSelectedItem().toString();
                                int updatedBalance = Integer.valueOf(bal)+Integer.valueOf(coins);
                                balanceEt.getEditText().setText(String.valueOf(updatedBalance));
                                updateUserDetails(id,name,loc,pass,type,String.valueOf(updatedBalance),status);
                                String tId = Transaction.generateTId();
                                ref4.child(tId).setValue(new Transaction(Integer.valueOf(coins),Transaction.getDate(),"admin",Login.uName,id,name,tId,"Credit",loc));
                                dialog1.cancel();
                            }
                        });
                        cancelB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.cancel();
                            }
                        });


                    }
                });

                ArrayAdapter<String> adapter = new ArrayAdapter(EmployeeAddDeleteUpdate.this, android.R.layout.simple_spinner_item, typeDetails.toArray());
                typeEt.setAdapter(adapter);

                ArrayAdapter<String> adapter2 = new ArrayAdapter(EmployeeAddDeleteUpdate.this, android.R.layout.simple_spinner_item, locationDetails.toArray());
                locET.setAdapter(adapter2);

                ArrayAdapter<String> adapter3 = new ArrayAdapter(EmployeeAddDeleteUpdate.this, android.R.layout.simple_spinner_item, statusDetails.toArray());
                statusET.setAdapter(adapter3);
                if(userArrayListSearch.size()==0)
                    userArrayListSearch = userArrayList;
                idTv.setText(userArrayListSearch.get(position).getId());
                nameEt.getEditText().setText(userArrayListSearch.get(position).getUserName());
                balanceEt.getEditText().setText(userArrayListSearch.get(position).getBalance());
                balanceEt.setEnabled(false);
                typeEt.setSelection(typeDetails.indexOf(userArrayListSearch.get(position).getUserType()));
                passwordEt.getEditText().setText(userArrayListSearch.get(position).getPassword());
                locET.setSelection(locationDetails.indexOf(userArrayListSearch.get(position).getLocation()));
                statusET.setSelection(statusDetails.indexOf(userArrayListSearch.get(position).getStatus()));
                updateButton.setEnabled(false);

                typeEt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        updateButton.setEnabled(true);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                nameEt.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        updateButton.setEnabled(true);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

//                locationEt.getEditText().addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        updateButton.setEnabled(true);
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//
//                    }
//                });

                passwordEt.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        updateButton.setEnabled(true);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = idTv.getText().toString();
                        String name = nameEt.getEditText().getText().toString();
                        String loc = locET.getSelectedItem().toString();
                        String pass = passwordEt.getEditText().getText().toString();
                        String type = typeEt.getSelectedItem().toString();
                        String bal = balanceEt.getEditText().getText().toString();
                        String status = statusET.getSelectedItem().toString();
                        updateUserDetails(id,name,loc,pass,type,bal,status);
                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });

        credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactionArrayList.clear();
                String cValue = creditCoins.getEditText().getText().toString();
                try {
                    int val = Integer.parseInt(cValue);
                    creditCoins.setError(null);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    creditCoins.setError("Enter a Valid Value");
                }

                CheckBox checkBox = findViewById(R.id.cB);
                int rId=rGrp.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(rId);
                String cFor = radioButton.getText().toString();

                if(cFor.equals("All Employees"))
                    for(int i=0;i<userArrayList.size();i++){
                        if(!userArrayList.get(i).getUserType().equalsIgnoreCase("employee")){
                            continue;
                        }
                        cValue = creditCoins.getEditText().getText().toString();
                        if(checkBox.isChecked()){
                            cValue = String.valueOf(Integer.valueOf(cValue) + Integer.valueOf(userArrayList.get(i).getBalance()));
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        userArrayList.get(i).setBalance(cValue);
                        //ref3.child(userArrayList.get(i).getId()).child("balance").setValue(cValue);
                        if(Integer.valueOf(creditCoins.getEditText().getText().toString())==0)
                            continue;
                        String tId = Transaction.generateTId();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        transactionArrayList.add(new Transaction(Integer.valueOf(creditCoins.getEditText().getText().toString()),Transaction.getDate(),"admin",Login.uName,userArrayList.get(i).getId(),userArrayList.get(i).getUserName(), tId, "Credit",userArrayList.get(i).getLocation()));
                        //ref4.child(tId).setValue(new Transaction(Integer.valueOf(creditCoins.getEditText().getText().toString()),Transaction.getDate(),"admin",Login.uName,userArrayList.get(i).getId(),userArrayList.get(i).getUserName(), tId, "credit",userArrayList.get(i).getLocation()));
                    }
                else {
                    String loc = spinner.getSelectedItem().toString();
                    for (int i = 0; i < userArrayList.size(); i++) {
                        cValue = creditCoins.getEditText().getText().toString();
                        if(!(userArrayList.get(i).getLocation().equalsIgnoreCase(loc)))
                            continue;
                        if(checkBox.isChecked()){
                            cValue = String.valueOf(Integer.valueOf(cValue) + Integer.valueOf(userArrayList.get(i).getBalance()));
                        }
                        userArrayList.get(i).setBalance(cValue);
                        //ref3.child(userArrayList.get(i).getId()).child("balance").setValue(cValue);
                        if(Integer.valueOf(creditCoins.getEditText().getText().toString())==0)
                            continue;
                        String tId = Transaction.generateTId();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        transactionArrayList.add(new Transaction(Integer.valueOf(creditCoins.getEditText().getText().toString()),Transaction.getDate(),"admin",Login.uName,userArrayList.get(i).getId(),userArrayList.get(i).getUserName(), tId, "Credit",userArrayList.get(i).getLocation()));
                        //uploadTransactionData(transactionArrayList);
                        //ref4.child(tId).setValue(new Transaction(Integer.valueOf(creditCoins.getEditText().getText().toString()), Transaction.getDate(), "admin", Login.uName,userArrayList.get(i).getId(),userArrayList.get(i).getUserName(), tId, "credit",userArrayList.get(i).getLocation()));
                    }

                }
                uploadTransactionData(transactionArrayList);
                uploadUserData(userArrayList);
                creditCoins.getEditText().setText(null);
                new MaterialAlertDialogBuilder(EmployeeAddDeleteUpdate.this)
                        .setTitle("SUCCESS")
                        .setMessage("Coins Credited Successfully!")
                        .setCancelable(true)
                        .show();
            }
        });

        addEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(EmployeeAddDeleteUpdate.this);
                //We have added a title in the custom layout. So let's disable the default title.
                //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
                //dialog.setCancelable(true);
                //Mention the name of the layout of your custom dialog.
                dialog.setContentView(R.layout.add_user_dialog);
                //Initializing the views of the dialog.
                final TextInputLayout nameEt = dialog.findViewById(R.id.userName);
                final TextInputLayout idET = dialog.findViewById(R.id.userId);
                final Spinner typeEt = dialog.findViewById(R.id.spinner);
                final Spinner locEt = dialog.findViewById(R.id.spinner1);
                final TextInputLayout passwordEt = dialog.findViewById(R.id.userPassword);
                final TextInputLayout locationEt = dialog.findViewById(R.id.userLocation);
                Button add = dialog.findViewById(R.id.buttonAdd);
                passwordEt.setEnabled(false);

                ArrayAdapter<String> adapter = new ArrayAdapter(EmployeeAddDeleteUpdate.this, android.R.layout.simple_spinner_item, typeDetails.toArray());
                //Set adapter
                typeEt.setAdapter(adapter);

                ArrayAdapter<String> adapter1 = new ArrayAdapter(EmployeeAddDeleteUpdate.this, android.R.layout.simple_spinner_item, locationDetails.toArray());
                //Set adapter
                locEt.setAdapter(adapter1);

                idET.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        passwordEt.getEditText().setText(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = idET.getEditText().getText().toString();
                        String name = nameEt.getEditText().getText().toString();
                        String type = typeEt.getSelectedItem().toString();
                        String loc = locEt.getSelectedItem().toString();

                        if(id.equals("")){
                            idET.setError("Required");
                            return;
                        }
                        else if(name.equals("")){
                            nameEt.setError("Required");
                            return;
                        }
                        else if(!(id.equals("") && name.equals(""))){
                            idET.setError(null);
                            nameEt.setError(null);
                        }
                        boolean checkId = isIdExist(id);
                        if(checkId){
                            idET.setError("User Id Already Exists!");
                        }
                        else{
                            if(type.equalsIgnoreCase("Store")){
                                ref7.child(id).setValue("null");
                            }
                            ref3.child(id).setValue(new UserModel(id,name,"0",id,type,loc,"Active"));
                            new MaterialAlertDialogBuilder(EmployeeAddDeleteUpdate.this)
                                    .setTitle("SUCCESS")
                                    .setMessage("User data updated")
                                    .setCancelable(true)
                                    .show();
                            dialog.cancel();
                        }

                    }
                });

                dialog.show();
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    exportIntoExcel(userArrayList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void uploadTransactionData(ArrayList<Transaction> transactionArrayList) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference ref9 = database.getReference("transactions");
        Map<String,Object> map = new HashMap<>();
        for(int i=0;i<transactionArrayList.size();i++){
            map.put(transactionArrayList.get(i).gettId(),transactionArrayList.get(i));
        }
                    // do something in the loop
        ref9.updateChildren(map);

    }
    private void uploadUserData(ArrayList<UserModel> userArrayList) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference refUpdate = database.getReference("userDetails");
        Map<String,Object> map = new HashMap<>();
        for(int i=0;i<userArrayList.size();i++){
            map.put(userArrayList.get(i).getId(),userArrayList.get(i));
        }
        // do something in the loop
        refUpdate.updateChildren(map);

    }

    private void exportIntoExcel(ArrayList<UserModel> userArrayList) throws IOException {
        // Blank workbook
        HSSFWorkbook workbook = new HSSFWorkbook();

        // Creating a blank Excel sheet
        HSSFSheet sheet = workbook.createSheet("Report");
        int rowCount = 1;
        Row row;
        row = sheet.createRow(0);
        Cell cell;
        cell = row.createCell(0);
        cell.setCellValue("USER ID");
        row.createCell(1).setCellValue("USER NAME");
        row.createCell(2).setCellValue("BALANCE");
        row.createCell(3).setCellValue("USER TYPE");
        row.createCell(4).setCellValue("LOCATION");
        row.createCell(5).setCellValue("USER STATUS");


        for (UserModel t : userArrayList) {
            row = sheet.createRow(rowCount++);
            writeBook(t, row);
        }
        sheet.setDefaultColumnWidth(20);

        String fileName = Transaction.generateTId();
        verifyStoragePermissions(EmployeeAddDeleteUpdate.this);
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "User_Data"+fileName+".xls");
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

    private void writeBook(UserModel t, Row row) {
        Cell cell = row.createCell(0);
        cell.setCellValue(t.id);
        row.createCell(1).setCellValue(t.userName);
        row.createCell(2).setCellValue(t.balance);
        row.createCell(3).setCellValue(t.userType);
        row.createCell(4).setCellValue(t.location);
        row.createCell(5).setCellValue(t.status);
    }


    private boolean isIdExist(String id) {
        boolean exist = false;
        for(int i=0;i<userArrayList.size();i++){
            if(userArrayList.get(i).getId().equalsIgnoreCase(id))
            {
                exist = true;
                break;
            }
        }
        return exist;
    }


    private void updateUserDetails(String id, String name, String loc, String pass, String type, String bal,String status) {

        ref1.child(id).child("userName").setValue(name);
        ref1.child(id).child("location").setValue(loc);
        ref1.child(id).child("password").setValue(pass);
        ref1.child(id).child("userType").setValue(type);
        ref1.child(id).child("balance").setValue(bal);
        ref1.child(id).child("status").setValue(status);

        new MaterialAlertDialogBuilder(EmployeeAddDeleteUpdate.this)
                .setTitle("SUCCESS")
                .setMessage("User data updated")
                .setCancelable(true)
                .show();

    }

    private void setListView(ArrayList<UserModel> userArrayList) {
        adapter = new UserAdapter(this, userArrayList);
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
    public void openDirectory(Uri uriToLoad) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setDataAndType(uriToLoad,  "application/vnd.ms-excel");
        startActivity(intent);
    }
}