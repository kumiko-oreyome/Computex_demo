package com.ncku.iir.computex.ALS;

import android.util.Log;


public class UpdateCallback implements IALSCallback {
    private String TAG = "UpdateCallback";

    public UpdateCallback(){

    }


    @Override
    public void onALSResponse(String s) {

        Log.d(TAG,s);

    }
}
