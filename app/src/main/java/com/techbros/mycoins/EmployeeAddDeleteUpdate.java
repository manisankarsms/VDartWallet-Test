package com.techbros.mycoins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class EmployeeAddDeleteUpdate extends AppCompatActivity {

    UserAdapter adapter;
    ListView listView;
    SearchView search;
    ArrayList<UserModel> userArrayList = new ArrayList<>();
    ArrayList<UserModel> userArrayListSearch = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref1 = database.getReference("userDetails");
    DatabaseReference ref2 = database.getReference("locationDetails");
    DatabaseReference ref3 = database.getReference("userDetails");
    DatabaseReference ref4 = database.getReference("transactions");
    ArrayList<String> locationDetails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_add_delete_update);
        Spinner spinner = findViewById(R.id.spinner);
        TextInputLayout creditCoins = findViewById(R.id.creditCoins);
        Button credit = findViewById(R.id.materialButton5);
        RadioGroup rGrp = findViewById(R.id.rGroup);
        RadioButton rAll = findViewById(R.id.radioAll);
        RadioButton rLoc = findViewById(R.id.radioLoc);


        adapter = new UserAdapter(this, null);
        search = findViewById(R.id.search);
        listView = findViewById(R.id.lv2);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                userArrayListSearch.clear();
                for (UserModel user : userArrayList) {
                    if (user.getUserName().toLowerCase().contains(newText.toLowerCase()) || user.getId().toLowerCase().contains(newText))
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
                    String userId = dataSnapshot.child(key).child("id").getValue().toString();
                    String userName = dataSnapshot.child(key).child("userName").getValue().toString();
                    String userBalance = dataSnapshot.child(key).child("balance").getValue().toString();
                    String userPassword = dataSnapshot.child(key).child("password").getValue().toString();
                    String userType = dataSnapshot.child(key).child("userType").getValue().toString();
                    if(!(userType.equalsIgnoreCase("employee")||userType.equalsIgnoreCase("guest")))
                        continue;
                    String userLocation = dataSnapshot.child(key).child("location").getValue().toString();
                    userArrayList.add(new UserModel(userId, userName, userBalance, userPassword, userType, userLocation));
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
                    final TextInputLayout passwordEt = dialog.findViewById(R.id.userPassword);
                    final TextInputLayout locationEt = dialog.findViewById(R.id.userLocation);
                    Button updateButton = dialog.findViewById(R.id.materialButton2);
                    Button cancelButton = dialog.findViewById(R.id.materialButton3);
                    Button add = dialog.findViewById(R.id.materialButton6);

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
                                    String loc = locationEt.getEditText().getText().toString();
                                    String pass = passwordEt.getEditText().getText().toString();
                                    String type = typeEt.getSelectedItem().toString();
                                    String bal = balanceEt.getEditText().getText().toString();
                                    int updatedBalance = Integer.valueOf(bal)+Integer.valueOf(coins);
                                    balanceEt.getEditText().setText(String.valueOf(updatedBalance));
                                    updateUserDetails(id,name,loc,pass,type,String.valueOf(updatedBalance));
                                    String tId = Transaction.generateTId();
                                    ref4.child(tId).setValue(new Transaction(Integer.valueOf(coins),Transaction.getDate(),"admin",id,tId,"credit"));
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

                    String[] arrayString = {"employee","guest"};
                    ArrayAdapter<String> adapter = new ArrayAdapter(EmployeeAddDeleteUpdate.this, android.R.layout.simple_spinner_item, arrayString);

                //Set adapter
                    typeEt.setAdapter(adapter);
                    if(userArrayListSearch.size()==0)
                        userArrayListSearch = userArrayList;
                    idTv.setText(userArrayListSearch.get(position).getId());
                    nameEt.getEditText().setText(userArrayListSearch.get(position).getUserName());
                    balanceEt.getEditText().setText(userArrayListSearch.get(position).getBalance());
                    balanceEt.setEnabled(false);
                    if(userArrayListSearch.get(position).getUserType().equals("employee"))
                    typeEt.setSelection(0);
                    else
                        typeEt.setSelection(1);
                    passwordEt.getEditText().setText(userArrayListSearch.get(position).getPassword());
                    locationEt.getEditText().setText(userArrayListSearch.get(position).getLocation());
                    updateButton.setEnabled(false);

//                    typeEt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            updateButton.setEnabled(true);
//                        }
//                    });
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

                locationEt.getEditText().addTextChangedListener(new TextWatcher() {
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
                            String loc = locationEt.getEditText().getText().toString();
                            String pass = passwordEt.getEditText().getText().toString();
                            String type = typeEt.getSelectedItem().toString();
                            String bal = balanceEt.getEditText().getText().toString();
                            updateUserDetails(id,name,loc,pass,type,bal);
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
                String cValue = creditCoins.getEditText().getText().toString();
                try {
                    int val = Integer.parseInt(cValue);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    creditCoins.setError("Enter a Valid Value");
                    return;
                }
                int rId=rGrp.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(rId);
                String cFor = radioButton.getText().toString();

                for(int i=0;i<userArrayList.size();i++){
                    ref3.child(userArrayList.get(i).getId()).child("balance").setValue(cValue);
                    String tId = Transaction.generateTId();
                    ref4.child(tId).setValue(new Transaction(Integer.valueOf(cValue),Transaction.getDate(),"admin",userArrayList.get(i).getId(),tId,"credit"));
                }
            }

        });
    }


            private void updateUserDetails(String id, String name, String loc, String pass, String type, String bal) {

        ref1.child(id).child("userName").setValue(name);
        ref1.child(id).child("location").setValue(loc);
        ref1.child(id).child("password").setValue(pass);
        ref1.child(id).child("userType").setValue(type);
        ref1.child(id).child("balance").setValue(bal);

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
}
