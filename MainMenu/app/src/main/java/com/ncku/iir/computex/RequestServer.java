package com.ncku.iir.computex;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by chester on 2018/1/22.
 */

public class RequestServer {

    private RequestActivity ma;
    public RequestQueue mQueue ;
    private Context context ;
    public String result;
    private  final String TAG = "RequestServer" ;
    private ArrayList replyList;

    public RequestServer(RequestActivity ma) {
        this.context = ma.getApplicationContext(); //getBaseContext() ;
//        Log.d(TAG, this.context.toString());
        this.ma = ma;
        this.mQueue = Volley.newRequestQueue(context);
        this.result = null;
    }



    public String getResult(){
        return result;
    }

    public String send_text(String text) {
        String command = null;
        try {
            command = "http://140.116.247.165:8787/serve_text/?text=" + toUtf8(text);
            Log.d(TAG,toUtf8(text));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.mQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, command, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"get json object");
                        Log.d(TAG,response.toString());
                        result = response.toString();
                        Log.d(TAG,"----------------------------");
                        Log.d(TAG, result);
                        Log.d(TAG,"----------------------------");
                        if(response.has("reply_text")) {
                            ma.onGetMessage(response.toString());
                        }
//                        if(response.has("recommend_info")){
//                            try{
//                                Log.d(TAG,"recommend_info");
//                                String s = response.getString("recommend_info");
//                                Log.d(TAG,s);
//                                JSONArray arr = new JSONArray(s);
////                                ma.onGetRecommendInfo(arr);
//                            } catch (JSONException e) {
//                               Log.e(TAG,"69",e);
//                            }
//                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,"onErrorResponse 8787");
                        Log.d(TAG,error.toString());
                    }
                });
        Log.d(TAG, String.format("send %s",text));

        mQueue.add(jsObjRequest);
        return result;
    }

    public void send_text_teach(String text , final VolleyCallback callback) {
        String command = null;
        try {
            command = "http://140.116.247.163:8000/test/main/" + toUtf8(text);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.mQueue = Volley.newRequestQueue(context);
        this.replyList = new ArrayList<String>();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, command, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            replyList.add(response.getString("state"));
                            if(response.getString("state").equals("show_question")){
                                replyList.add(response.getString("index"));
                                replyList.add(response.getString("question"));
                                replyList.add(response.getString("A"));
                                replyList.add(response.getString("B"));
                                replyList.add(response.getString("answer"));
                            }
                            else if(response.getString("state").equals("show_result")){
                                Log.d("final","final");
                            }




                            callback.onSuccess(replyList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        mQueue.add(jsObjRequest);
    }

    public void send_text_teach(String text ,String answer , final VolleyCallback callback) {
        String command = null;
        try {
            command = "http://140.116.247.163:8000/test/main/" + toUtf8(text)+"/"+ toUtf8(answer);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.mQueue = Volley.newRequestQueue(context);
        this.replyList = new ArrayList<>();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, command, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            replyList.add(response.getString("state"));
                            replyList.add(response.getString("correct"));
                            replyList.add(response.getString("image"));
                            replyList.add(response.getString("text"));
                            callback.onSuccess(replyList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                    }
                });

        mQueue.add(jsObjRequest);
    }


    public RequestQueue getQueue(){
        return this.mQueue ;
    }
    /*


    [03/Feb/2018 21:04:13] code 400, message Bad request syntax ('GET /serve_text/?text=æ¸\x85æ·¡ä¸\x80é»\x9e HTTP/1.1')
[03/Feb/2018 21:04:13] "GET /serve_text/?text=æ¸æ·¡ä¸é» HTTP/1.1" 400 -
     */
    public static String toUtf8(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str,"UTF-8");
        //return new String(str.getBytes("UTF-8"), "UTF-8");
    }


}
