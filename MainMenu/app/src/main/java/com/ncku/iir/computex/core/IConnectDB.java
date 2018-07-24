package com.ncku.iir.computex.core;

import com.ncku.iir.computex.core.Connect_db;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public interface IConnectDB {
    public Connect_db getConnectDB();
    /* Database */
    public void onGetMessage_ques(HashMap<String, String[]> results) ;

    public  void onGetMessage_ques2(HashMap<String, String[]> results) ;

    public  void onGetMessage_items(HashMap<String, String[]> results) throws UnsupportedEncodingException;
}
