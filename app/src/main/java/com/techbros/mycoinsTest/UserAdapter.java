package com.techbros.mycoinsTest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<UserModel> implements Filterable {


    public UserAdapter(Activity context, ArrayList<UserModel> userDetails){
        super(context, 0, userDetails);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.user_list, parent, false
            );
        }

        UserModel userDetails = getItem(position);

        TextView textView1 = (TextView) listItemView.findViewById(R.id.tv_id);
        textView1.setText(userDetails.getId());

        TextView textView2 = (TextView) listItemView.findViewById(R.id.tv_name);
        textView2.setText(userDetails.getUserName());

        TextView textView3 = (TextView) listItemView.findViewById(R.id.tv_bal);
        textView3.setText(String.valueOf(userDetails.getBalance()));

        TextView textView4 = (TextView) listItemView.findViewById(R.id.tv_type);
        textView4.setText(userDetails.getUserType());

        TextView textView5 = (TextView) listItemView.findViewById(R.id.tv_loc);
        textView5.setText(userDetails.getLocation());

        return listItemView;
    }
}
