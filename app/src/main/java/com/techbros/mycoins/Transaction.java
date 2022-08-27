package com.techbros.mycoins;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

class Transaction {
    int tCoins;
    String tDate, tFrom, tFromName, tTo, tToName, tId, tType, tLoc;

    public Transaction(int tCoins, String tDate, String tFrom, String tFromName, String tTo, String tToName, String tId, String tType, String tLoc) {
        this.tCoins = tCoins;
        this.tDate = tDate;
        this.tFrom = tFrom;
        this.tFromName = tFromName;
        this.tTo = tTo;
        this.tToName = tToName;
        this.tId = tId;
        this.tType = tType;
        this.tLoc = tLoc;
    }

    public String gettFromName() {
        return tFromName;
    }

    public void settFromName(String tFromName) {
        this.tFromName = tFromName;
    }

    public String gettToName() {
        return tToName;
    }

    public void settToName(String tToName) {
        this.tToName = tToName;
    }

    public String gettLoc() {
        return tLoc;
    }

    public void settLoc(String tLoc) {
        this.tLoc = tLoc;
    }

    public int gettCoins() {
        return tCoins;
    }

    public void settCoins(int tCoins) {
        this.tCoins = tCoins;
    }

    public String gettDate() {
        return tDate;
    }

    public void settDate(String tDate) {
        this.tDate = tDate;
    }

    public String gettFrom() {
        return tFrom;
    }

    public void settFrom(String tFrom) {
        this.tFrom = tFrom;
    }

    public String gettTo() {
        return tTo;
    }

    public void settTo(String tTo) {
        this.tTo = tTo;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String gettType() {
        return tType;
    }

    public void settType(String tType) {
        this.tType = tType;
    }

    static String generateTId() {
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
        Date date = new Date();
        String dateVal = formatter.format(date);
//        String tCountt = String.valueOf(tCount);
//        if(tCountt.length()==1)
//            tCountt = "0000"+tCountt;
//        else if(tCountt.length()==2)
//            tCountt = "000"+tCountt;
//        else if(tCountt.length()==3)
//            tCountt = "00"+tCountt;
//        else if(tCountt.length()==4)
//            tCountt = "0"+tCountt;;
        return dateVal;
    }
    @NonNull
    static String getDate(){
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime().toString();
    }
}


