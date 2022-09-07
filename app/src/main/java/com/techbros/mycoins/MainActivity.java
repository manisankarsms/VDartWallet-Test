package com.techbros.mycoins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    Handler mHandler = new Handler();
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.imageView2);
        Animation myAnim = AnimationUtils.loadAnimation(this,R.anim.splashanim);
        iv.startAnimation(myAnim);
        mHandler.postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, Login.class);
            finish();
            startActivity(intent);
        }, 1500);
        }
    }