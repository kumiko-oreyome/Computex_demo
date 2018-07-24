package com.ncku.iir.computex.ALS;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by IIR on 2018/2/3.
 */

public class StoreComment implements Serializable {
    private String text;
    private int  rating;
    private String TAG = "StoreComment";
    public StoreComment(String json){
        try {
            JSONObject obj = new JSONObject(json);
            rating = obj.getInt("rating");
            text = obj.getString("text");
        } catch (JSONException e) {
            Log.e(TAG,"StoreComment convert",e);
        }
    }

    public void print(){
        Log.d(TAG,"StoreComment");
        Log.d(TAG,"Text :"+text);
        Log.d(TAG,"Rating :"+rating);
        Log.d(TAG,"- - - - - - -");
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }
}
