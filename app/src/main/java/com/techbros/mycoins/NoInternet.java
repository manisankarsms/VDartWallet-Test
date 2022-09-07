package com.techbros.mycoins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class NoInternet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        boolean connectionCheck = ConnectionReceiver.isConnectedToInternet(getApplicationContext());
        if(connectionCheck){
            finish();
        }
    }
}