package com.techbros.mycoins;

import java.text.SimpleDateFormat;
import java.util.Date;

class Transaction {
    int tCoins;
    String tDate, tFrom, tTo, tId, tType;

    public Transaction(int tCoins, String tDate, String tFrom, String tTo, String tId, String tType) {
        this.tCoins = tCoins;
        this.tDate = tDate;
        this.tFrom = tFrom;
        this.tTo = tTo;
        this.tId = tId;
        this.tType = tType;
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
    static String getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String dateVal = formatter.format(date);
        return dateVal;
    }
}


