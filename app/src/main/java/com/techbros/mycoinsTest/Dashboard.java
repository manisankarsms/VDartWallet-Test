package com.techbros.mycoinsTest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Dashboard extends AppCompatActivity {

    int credit =0, payment=0, unUtilized=0;
    int credit1 =0, payment1=0, unUtilized1=0;

    TextView tv1,tv2,tv3,tv4;
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    ArrayList<Transaction> transactionArrayListPrevious = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference gRefD = database.getReference("transactions");
    DatabaseReference gRefD2 = database.getReference("transactions");

    PieChart pieChart,pieChart2;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        pieChart = findViewById(R.id.activity_main_piechart);
        pieChart2 = findViewById(R.id.activity_main_piechart2);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.dashTotalCredit);
        tv3 = findViewById(R.id.tv2);
        tv4 = findViewById(R.id.dashTotalCredit2);
        loadData();
        pieChart.animateY(1400, Easing.EaseInOutQuad);
        pieChart2.animateY(1400, Easing.EaseInOutQuad);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadData() {

        LocalDateTime datetime1 = LocalDateTime.now();
        int Year1 = datetime1.getYear();
        tv1.setText(datetime1.getMonth().toString()+ " "+datetime1.getYear());
        LocalDateTime datetime2 = datetime1.minusDays(datetime1.getDayOfMonth()-1);

        LocalDateTime datetime3 = datetime1.minusDays(datetime1.getDayOfMonth());//last month last date
        LocalDateTime datetime4 = datetime3.minusDays(datetime3.getDayOfMonth()-1);// last month first date
        int Year2 = datetime4.getYear();
        tv3.setText(datetime3.getMonth().toString()+ " "+datetime3.getYear());



        DateTimeFormatter df = DateTimeFormatter.ofPattern("EEE MMM dd");
        String startDateVal = datetime2.format(df);
        String endDateVal = datetime1.format(df);

        String startDateValPre = datetime4.format(df);
        String endDateValPre = datetime3.format(df);


        gRefD.orderByKey().addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                credit =0; payment=0; unUtilized=0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String tCoins  = dataSnapshot.child(key).child("tCoins").getValue().toString();
                    String tDate  = dataSnapshot.child(key).child("tDate").getValue().toString();
                    if(startDateVal!=null && endDateVal!=null){
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                        try {
                            Calendar cal = Calendar.getInstance();
                            Date current = cal.getTime();
                            cal.setTime(formatter.parse(startDateVal+" 00:00:00 GMT+05:30 "+Year1));
                            Date start = cal.getTime();
                            cal.setTime(formatter.parse(endDateVal+" 23:59:59 GMT+05:30 "+Year1));
                            Date end = cal.getTime();
                            Date trDate = formatter.parse(tDate);
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
        gRefD2.orderByKey().addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                credit1 =0; payment1=0; unUtilized1=0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String tCoins  = dataSnapshot.child(key).child("tCoins").getValue().toString();
                    String tDate  = dataSnapshot.child(key).child("tDate").getValue().toString();
                    if(startDateValPre!=null && endDateValPre!=null){
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                        try {
                            Calendar cal = Calendar.getInstance();
                            Date current = cal.getTime();
                            cal.setTime(formatter.parse(startDateValPre+" 00:00:00 GMT+05:30 "+Year1));
                            Date start = cal.getTime();
                            cal.setTime(formatter.parse(endDateValPre+" 23:59:59 GMT+05:30 "+Year1));
                            Date end = cal.getTime();
                            Date trDate = formatter.parse(tDate);
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
                        credit1=credit1+Integer.valueOf(tCoins);
                    else if(tType.equalsIgnoreCase("Payment"))
                        payment1 = payment1 + Integer.valueOf(tCoins);
                }
                tv4.setText("Credit - "+credit1+" Coins \nUtilised - "+payment1+" Coins \nNot Utilised - "+(credit1-payment1)+" Coins");
                setupPieChart1();
                loadPieChartData1();
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
    private void setupPieChart1() {
        pieChart2.setDrawHoleEnabled(true);
        pieChart2.setUsePercentValues(true);
        pieChart2.setEntryLabelTextSize(10);
        pieChart2.setEntryLabelColor(Color.BLACK);
        pieChart2.setCenterText("Coins Utilisation");
        pieChart2.setCenterTextSize(20);
        pieChart2.getDescription().setEnabled(false);

        Legend l = pieChart2.getLegend();
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

//        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }
    private void loadPieChartData1() {

        float creditval = credit1;
        float utilised = payment1/creditval;
        float notUtil = (credit1 - payment1)/creditval;
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

        pieChart2.setData(data);
        pieChart2.invalidate();

//        pieChart2.animateY(1400, Easing.EaseInOutQuad);
    }

}