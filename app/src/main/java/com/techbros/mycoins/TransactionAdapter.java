package com.techbros.mycoins;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

class TransactionAdapter extends ArrayAdapter<Transaction> {


    public TransactionAdapter(Activity context, ArrayList<Transaction> transactionDetails){
        super(context, 0, transactionDetails);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.transaction_list, parent, false
            );
        }

        Transaction transactionDetails = getItem(position);

        TextView textView1 = (TextView) listItemView.findViewById(R.id.from);
        textView1.setText("From: "+transactionDetails.gettFrom());

        TextView textView2 = (TextView) listItemView.findViewById(R.id.to);
        textView2.setText("To: "+transactionDetails.gettTo());

        TextView textView3 = (TextView) listItemView.findViewById(R.id.coins);

        if(Login.userType.equals("store")){
            if(transactionDetails.gettType().equalsIgnoreCase("Payment"))
                textView3.setTextColor(Color.parseColor("#a7c44c"));
            else if(transactionDetails.gettType().equalsIgnoreCase("EncashRequest")) {
                textView3.setTextColor(Color.RED);
                listItemView.setBackgroundColor(Color.parseColor("#e7f4d4"));
            }
        }
        else{
            if(transactionDetails.gettType().equalsIgnoreCase("Payment"))
                textView3.setTextColor(Color.RED);
            else if(transactionDetails.gettType().equalsIgnoreCase("Credit")) {
                listItemView.setBackgroundColor(Color.parseColor("#e7f4d4"));
                textView3.setTextColor(Color.parseColor("#a7c44c"));
            }
        }

        textView3.setText(String.valueOf(transactionDetails.gettCoins())+" Coins");

        TextView textView4 = (TextView) listItemView.findViewById(R.id.fromName);
        textView4.setText("("+transactionDetails.gettFromName()+")");

        TextView textView5 = (TextView) listItemView.findViewById(R.id.toName);
        textView5.setText("("+transactionDetails.gettToName()+")");

        TextView textView6 = (TextView) listItemView.findViewById(R.id.loc);
        textView6.setText("Loc: "+transactionDetails.gettLoc());

        TextView textView7 = (TextView) listItemView.findViewById(R.id.tType);
        textView7.setText(transactionDetails.gettType());

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");

        Date trDate=null;
        String date2=null;
        try {
            trDate = sdf.parse(transactionDetails.gettDate());
            date2 = sdf2.format(trDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TextView textView8 = (TextView) listItemView.findViewById(R.id.date);
        textView8.setText(date2);

        return listItemView;
    }
}
