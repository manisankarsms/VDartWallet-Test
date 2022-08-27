package com.techbros.mycoins;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.mbms.StreamingServiceInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

public class Dashboard extends AppCompatActivity {

    int credit =0, payment=0, unUtilized=0;
    TextView tv1,tv2;
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference gRef = database.getReference("transactions");
    PieChart pieChart;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        pieChart = findViewById(R.id.activity_main_piechart);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.dashTotalCredit);
        loadData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadData() {

        LocalDateTime datetime1 = LocalDateTime.now();
        tv1.setText(datetime1.getMonth().toString()+ " "+datetime1.getYear());
        LocalDateTime datetime2 = datetime1.minusDays(datetime1.getDayOfMonth()-1);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yy");
        String startDateVal = datetime2.format(format);
        String endDateVal = datetime1.format(format);

        gRef.orderByKey().addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                credit =0; payment=0; unUtilized=0;
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
                    String tType  = dataSnapshot.child(key).child("tType").getValue().toString();
                    if(tType.equalsIgnoreCase("Credit"))
                        credit=credit+Integer.valueOf(tCoins);
                    else if(tType.equalsIgnoreCase("Payment"))
                        payment = payment + Integer.valueOf(tCoins);
                }
                tv2.setText("Credit - "+credit+" Coins \nUtilised - "+payment+" Coins \nNot Utilised - "+(credit-payment)+" Coins");
                setupPieChart();
                loadPieChartData();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(10);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Coins Utilisation");
        pieChart.setCenterTextSize(20);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData() {

        float creditval = credit;
        float utilised = payment/creditval;
        float notUtil = (credit - payment)/creditval;
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(utilised, "Utilised"));
        entries.add(new PieEntry(notUtil, "Not Utilised"));

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Category");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }
}