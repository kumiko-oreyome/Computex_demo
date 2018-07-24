package com.ncku.iir.computex.ALS;

import android.util.Log;

import com.ncku.iir.computex.restaurant.BriefInfoFragment;
import com.ncku.iir.computex.restaurant.briefInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecommendCallback implements IALSCallback {
    private BriefInfoFragment caller;
    private String TAG = "RecommendCallback";
    public List<RecommendationObject> recmdList;

    public RecommendCallback(BriefInfoFragment caller){
        this.caller = caller ;
        recmdList = new ArrayList<RecommendationObject>();
    }

    @Override
    public void onALSResponse(String s) {
        try {
            JSONArray recommend_info = new JSONArray(s);
            for (int i = 0; i < recommend_info.length(); ++i) {
                JSONObject jb = null;
                try {
                    jb = recommend_info.getJSONObject(i);
                } catch (JSONException e) {
                    Log.e(TAG, "getJSONObject", e);
                }
                RecommendationObject robj = new RecommendationObject(jb.toString());
                robj.print();
                recmdList.add(robj);
            }

            caller.onGetRecmdList(recmdList);

        } catch (Exception e){
            Log.e(TAG," onALSResponse",e);
        }
    }

    public List<RecommendationObject> getResults(){
        return this.recmdList;
    }
}
