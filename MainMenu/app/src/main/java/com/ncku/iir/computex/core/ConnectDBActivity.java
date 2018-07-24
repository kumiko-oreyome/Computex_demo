package com.ncku.iir.computex.core;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ncku.iir.computex.R;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;


public class ConnectDBActivity extends AppCompatActivity implements IConnectDB {

    // db
    public Connect_db connect_db = new Connect_db(this);
//    private String domain = "news";

    private String TAG = "ConnectDBActivity";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

    }


    @Override
    public Connect_db getConnectDB() {
        return null;
    }

    /* Database */
    public void onGetMessage_ques(HashMap<String, String[]> results) {

        // results ["question", "ans_1", "ans_2", "type"]

        for(String s:results.get("question")){
            Log.d(TAG, s);
        }
    }

    public void onGetMessage_ques2(HashMap<String, String[]> results) {

        // results ["question", "ans_1", "ans_2", "type"]

        for(String s:results.get("question")){
            Log.d(TAG, s);
        }
    }

    public void onGetMessage_items(HashMap<String, String[]> results) throws UnsupportedEncodingException {

        // results ["img_url", "title", "brief_info", "detailed_info", "tag_1"]

        for(String s:results.get("title")){
            Log.d(TAG, s);
        }
    }


}
