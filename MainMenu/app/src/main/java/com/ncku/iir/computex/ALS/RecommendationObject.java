package com.ncku.iir.computex.ALS;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by IIR on 2018/2/3.
 */

public class RecommendationObject implements Serializable {
    private String title;
    private String resKey;
    private String chineseType;
    private String picture = null;
    private String address;
    private ArrayList<StoreComment> commentList = new ArrayList<>();
    private static String TAG = "RecommendationObject";


    public RecommendationObject(String json){
        try {
            JSONObject obj  = new JSONObject(json);
            resKey = obj.getString("res_key");
            title = obj.getString("title");
            picture = obj.getString("picture");
            chineseType = obj.getString("chinese_type");
            address = obj.getString("address");
            JSONArray arr = obj.getJSONArray("recommendContent");
            for(int i=0;i<arr.length();++i){
                JSONObject comment = arr.getJSONObject(i);
                StoreComment storeComment = new StoreComment(comment.toString());
                commentList.add(storeComment);
            }
        } catch (JSONException e) {
            Log.e(TAG,"convert json error",e);
        }
    }

    public void print(){
        Log.d(TAG,String.format("title %s",title));
        Log.d(TAG,String.format("chinese type %s",chineseType));
        Log.d(TAG,String.format("picture %s",picture));
        Log.d(TAG,String.format("comment list:"));
        for(StoreComment comment : commentList){
            comment.print();
        }
        Log.d(TAG,String.format("#########################"));
        Log.d(TAG,String.format("res_key %s",resKey));
    }

    public String getTitle() {
        return title;
    }

    public String getResKey() {
        return resKey;
    }

    public String getAddress() {
        return address;
    }

    public String getChineseType() {
        return chineseType;
    }

    public String getPicture() {
        return picture;
    }

    public ArrayList<StoreComment> getCommentList() {
        return commentList;
    }

}
