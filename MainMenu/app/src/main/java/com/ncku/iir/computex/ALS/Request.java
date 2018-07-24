package com.ncku.iir.computex.ALS;


import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.android.volley.Request.Method.POST;

//json_dict['type'] = conn_type
//json_dict['user'] = recipient_id
//json_dict['user'] = recipient_id
//if location : json_dict['location'] = location
//if restaurant_id : json_dict['restaurant_id'] = restaurant_id
// if record : json_dict['record'] = record Y or N ,當更新的時候會用到,只要列出推薦清單不會用到
// if situation : json_dict['situation'] = situation
public class Request {
    private final String TAG = "ALS_REQUEST";
    private Context context;
    private  RequestQueue mQueue ;
    private String url ;

    private String generateRequestJson(String type,String userId, String situation){
        try {
            JSONObject json_obj = new JSONObject();
            json_obj.put("type",type);
            json_obj.put("user",userId);
            json_obj.put("situation",situation);
            return json_obj.toString();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    //for update
    private String generateRequestJson(String type,String userId,String restaurantId,String record){
        try {
            JSONObject json_obj = new JSONObject();
            json_obj.put("type",type);
            json_obj.put("user",userId);
            json_obj.put("restaurant_id",restaurantId);
            json_obj.put("record",record);
            return json_obj.toString();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Request(Context context,String url){
        this.context = context;
        this.url  = url;
        this.mQueue = Volley.newRequestQueue(context);
    }
    public void getRecommendationList(String userId, String situation, final IALSCallback callback){
        String body = generateRequestJson("R",userId, situation);
        makeStringRequest(body,callback);
    }
    public void updateUserPreference(String userId,String restaurantId,String like, final IALSCallback callback){
        String body = generateRequestJson("U" ,userId,restaurantId,like);
        makeStringRequest(body,callback);
    }


    public void makeStringRequest(final String body,final IALSCallback callback){
        StringRequest stringRequest = new StringRequest(POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"success");
                Log.d(TAG,response.toString());
                String s = response.toString();
                try {
                    callback.onALSResponse(new String(s.getBytes("UTF-8"),"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG," makeStringRequest",e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"onErrorResponse",error);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return body.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG,"getbody",e);
                }
                return null;
            }
        };
        this.mQueue.add(stringRequest);
    }
}
