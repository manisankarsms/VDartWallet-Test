package com.techbros.mycoins;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class SuperAdminDashboard extends AppCompatActivity{


    CardView c1, c2, c3, c4, c5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_dashboard);

        c1 = findViewById(R.id.cv1);
        c2 = findViewById(R.id.cv2);
        c3 = findViewById(R.id.cv3);
        c4 = findViewById(R.id.cv4);
        c5 = findViewById(R.id.cv5);


        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmployeeAddDeleteUpdate.class);
                startActivity(intent);
            }
        });

        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Backend.class);
                startActivity(intent);
            }
        });

        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Encash.class);
                startActivity(intent);
            }
        });

        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Reports.class);
                startActivity(intent);
            }
        });

        c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(SuperAdminDashboard.this)
                .setTitle("LOGOUT")
                .setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Login.class));
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

//    private void checkConnection() {
//
//        // initialize intent filter
//        IntentFilter intentFilter = new IntentFilter();
//
//        // add action
//        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");
//
//        // register receiver
//        registerReceiver(new ConnectionReceiver(), intentFilter);
//
//        // Initialize listener
//        ConnectionReceiver.Listener = this;
//
//        // Initialize connectivity manager
//        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        // Initialize network info
//        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
//
//        // get connection status
//        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
//
//        // display snack bar
//        showSnackBar(isConnected);
//    }
//
//    private void showSnackBar(boolean isConnected) {
//
//        // initialize color and message
//        String message;
//        int color;
//
//        // check condition
//        if (isConnected) {
//
//            // when internet is connected
//            // set message
//            message = "Connected to Internet";
//
//            // set text color
//            color = Color.WHITE;
//
//        } else {
//
//            // when internet
//            // is disconnected
//            // set message
//            message = "Not Connected to Internet";
//
//            // set text color
//            color = Color.RED;
//        }
//
//        // initialize snack bar
//        Snackbar snackbar = Snackbar.make(findViewById(R.id.cv5), message, Snackbar.LENGTH_LONG);
//
//        // initialize view
//        View view = snackbar.getView();
//
//        // Assign variable
//        TextView textView = view.findViewById(R.id.snackbar_text);
//
//        // set text color
//        textView.setTextColor(color);
//
//        // show snack bar
//        snackbar.show();
//    }
//
//    @Override
//    public void onNetworkChange(boolean isConnected) {
//        // display snack bar
//        showSnackBar(isConnected);
//    }

}