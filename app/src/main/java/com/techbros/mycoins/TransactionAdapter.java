package com.techbros.mycoins;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
        textView1.setText("FROM: "+transactionDetails.gettFrom());

        TextView textView2 = (TextView) listItemView.findViewById(R.id.to);
        textView2.setText("TO: "+transactionDetails.gettTo());

        TextView textView3 = (TextView) listItemView.findViewById(R.id.coins);

        if(Login.userType.equals("store")){
            if(transactionDetails.gettType().equals("Payment"))
                textView3.setTextColor(Color.parseColor("#a7c44c"));
            else if(transactionDetails.gettType().equals("EncashRequest")) {
                textView3.setTextColor(Color.RED);
                listItemView.setBackgroundColor(Color.parseColor("#a7c44c"));
            }
        }
        else{
            if(transactionDetails.gettType().equals("Payment"))
                textView3.setTextColor(Color.RED);
            else if(transactionDetails.gettType().equals("Credit")) {
                listItemView.setBackgroundColor(Color.parseColor("#a7c44c"));
                textView3.setTextColor(Color.RED);
            }
        }

        textView3.setText(String.valueOf(transactionDetails.gettCoins()));

        TextView textView4 = (TextView) listItemView.findViewById(R.id.date);
        textView4.setText(transactionDetails.gettDate());

        return listItemView;
    }
}
