package com.ncku.iir.computex.ALS;

import android.util.Log;


public class PrintCallback  implements IALSCallback {
    private String TAG = "PrintCallback";

    public PrintCallback(){

    }


    @Override
    public void onALSResponse(String s) {

        Log.d(TAG,s);

    }
}
