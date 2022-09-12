package com.techbros.mycoinsTest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetPassword extends AppCompatActivity {

    TextInputLayout newPass,cfmPass;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref1 = database.getReference("userDetails");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        newPass = findViewById(R.id.newPwd);
        cfmPass = findViewById(R.id.cfmPwd);
        Button submit = findViewById(R.id.reset);
        submit.setEnabled(false);

        newPass.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()<8)
                    newPass.setError("Min 8 characters");
                else
                    newPass.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cfmPass.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = newPass.getEditText().getText().toString();
                if(str.equals(s.toString())){
                    cfmPass.setError(null);
                    cfmPass.setHelperText("Password Matches");
                    cfmPass.setBoxStrokeColor(Color.GREEN);
                    submit.setEnabled(true);
                }
                else{
                    submit.setEnabled(false);
                    cfmPass.setHelperText(null);
                    cfmPass.setError("Password not matching");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedPass = cfmPass.getEditText().getText().toString();
                ref1.child(Login.uId).child("password").setValue(updatedPass);
                new MaterialAlertDialogBuilder(ResetPassword.this)
                        .setTitle("SUCCESS")
                        .setMessage("User data updated")
                        .setCancelable(true)
                        .show();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }

        });


    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}