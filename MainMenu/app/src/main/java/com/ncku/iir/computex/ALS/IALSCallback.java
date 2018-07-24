package com.ncku.iir.computex.ALS;

/*
當ALS server 回傳response 用 implements 這個interface的類別 call onALSResponse的method做更新的動作
 */

public interface IALSCallback {
    public void onALSResponse(String s);

}
