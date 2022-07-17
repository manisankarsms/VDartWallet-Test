package com.techbros.mycoins;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.Base64;

public class ScanBarCodeActivity extends AppCompatActivity {


    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAction;
    String intentData = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bar_code);
        initViews();
    }
    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
//        btnAction = findViewById(R.id.btnAction);
//        btnAction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (intentData.length() > 0) {
//                    startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(intentData)));
//                }
//            }
//        });
    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext() , "Barcode scanner started" , Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this , barcodeDetector)
                .setRequestedPreviewSize(1920 , 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanBarCodeActivity.this , Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScanBarCodeActivity.this , new
                                String[]{Manifest.permission.CAMERA} , REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder , int format , int width , int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext() , "To prevent memory leaks barcode scanner has been stopped" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            intentData = barcodes.valueAt(0).displayValue;
                            String pId = decode(intentData);
                            txtBarcodeValue.setText(pId);
                            final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            final VibrationEffect vibrationEffect1;

                            // this is the only type of the vibration which requires system version Oreo (API 26)
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                                // this effect creates the vibration of default amplitude for 1000ms(1 sec)
                                vibrationEffect1 = VibrationEffect.createOneShot(250, VibrationEffect.EFFECT_TICK);

                                // it is safe to cancel other vibrations currently taking place
                                vibrator.cancel();
                                vibrator.vibrate(vibrationEffect1);
                            }
                            Intent intent = new Intent(getApplicationContext(), Payment.class);
                            intent.putExtra("payerId", pId);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String decode(String intentData) {
        byte[] decodedBytes = Base64.getDecoder().decode(intentData.trim());
        return new String(decodedBytes);
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}