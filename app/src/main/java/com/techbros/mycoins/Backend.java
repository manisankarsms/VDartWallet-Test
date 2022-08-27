package com.techbros.mycoins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Backend extends AppCompatActivity {

    Map<String,String> passcodeList = new HashMap<>();
    Map<String,String> locationList = new HashMap<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref1 = database.getReference("storeEncashPasscode");
    DatabaseReference ref2 = database.getReference("locationDetails");
    DatabaseReference ref3 = database.getReference();
    TextInputLayout empLimit,guestLimit,storePass,locAdd;
    ListView listView,listView2;
    Button update,update1,update2;
    String eDaily,gDaily;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backend);
        init();
        initListView();
        initListView2();

    }

    private void initListView() {
        ref1.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                passcodeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String userId = String.valueOf(dataSnapshot.child(key).getKey());
                    String password = String.valueOf(dataSnapshot.child(key).getValue());
                    passcodeList.put(userId,password);
                }
                setListView(passcodeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });
    }
    private void initListView2() {
        ref2.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String no = String.valueOf(dataSnapshot.child(key).getKey());
                    String password = String.valueOf(dataSnapshot.child(key).getValue());
                    locationList.put(no,password);
                }
                setListView2(locationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });
    }



    private void setListView(Map<String,String> passcodeList) {
        storePass = findViewById(R.id.lvStorePass);
        update1 = findViewById(R.id.updateB2);
        update1.setEnabled(false);
        storePass.setEnabled(false);
        listView = findViewById(R.id.lvStorePasscode);
        Set<Map.Entry<String, String>> entrySet = passcodeList.entrySet();
        ArrayList<Map.Entry<String, String> > listOfEntry
                = new ArrayList<Map.Entry<String, String>>(entrySet);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listOfEntry.toArray());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                storePass.setHint(listOfEntry.get(position).getKey());
                storePass.getEditText().setText(listOfEntry.get(position).getValue());
            }
        });
        storePass.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0) {
                    storePass.setEnabled(true);
                    storePass.setError("Passcode cannot be empty");
                    update1.setEnabled(false);
                }
                else {
                    storePass.setEnabled(true);
                    storePass.setError(null);
                    update1.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        update1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref1.child(String.valueOf(storePass.getHint())).setValue(storePass.getEditText().getText().toString());
                Toast.makeText(getApplicationContext(), "Passcode Updated...", Toast.LENGTH_SHORT).show();
                update1.setEnabled(false);
                storePass.setEnabled(false);
                storePass.getEditText().setText(null);
                storePass.setError(null);

            }
        });

    }

    private void setListView2(Map<String,String> passcodeList) {
        locAdd = findViewById(R.id.lvLocAdd);
        update2 = findViewById(R.id.updateB3);
        update2.setEnabled(false);
        listView2 = findViewById(R.id.lvLoc);
        Set<Map.Entry<String, String>> entrySet = passcodeList.entrySet();
        ArrayList<Map.Entry<String, String> > listOfEntry
                = new ArrayList<Map.Entry<String, String>>(entrySet);
        ArrayAdapter adapter1 = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listOfEntry.toArray());
        listView2.setAdapter(adapter1);
        locAdd.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0) {
                    update2.setEnabled(false);
                }
                else {
                    update2.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        update2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref2.child(String.valueOf(passcodeList.size()+1)).setValue(locAdd.getEditText().getText().toString());
                Toast.makeText(getApplicationContext(), "Location Added...", Toast.LENGTH_SHORT).show();
                update2.setEnabled(false);
                locAdd.getEditText().setText(null);
            }
        });

    }

    void init(){

        empLimit = findViewById(R.id.empLimit);
        guestLimit = findViewById(R.id.gustLimit);
        update = findViewById(R.id.updateB1);
        update.setEnabled(false);
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eDaily = dataSnapshot.child("coinsLimitEmp").getValue().toString();
                gDaily = dataSnapshot.child("coinsLimitGuest").getValue().toString();

                empLimit.getEditText().setText(eDaily);
                guestLimit.getEditText().setText(gDaily);
                update.setEnabled(false);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        empLimit.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    int val = Integer.parseInt(s.toString());
                    empLimit.setError(null);
                    if(guestLimit.getError()!=null){
                        return;
                    }
                    update.setEnabled(true);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                    update.setEnabled(false);
                    empLimit.setError("Please enter a valid Value");
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        guestLimit.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    int val = Integer.parseInt(s.toString());
                    guestLimit.setError(null);
                    if(empLimit.getError()!=null){
                        return;
                    }
                    update.setEnabled(true);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                    update.setEnabled(false);
                    guestLimit.setError("Please enter a valid Value");
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref3.child("coinsLimitEmp").setValue(empLimit.getEditText().getText().toString());
                ref3.child("coinsLimitGuest").setValue(guestLimit.getEditText().getText().toString());
                update.setFocusable(true);
                update.setEnabled(false);
            }
        });

    }

}