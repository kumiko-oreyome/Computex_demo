package com.ncku.iir.computex.poi;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ncku.iir.computex.R;
import com.squareup.picasso.Picasso;


/**
 * Created by veron on 2018/5/24.
 */

public class PoiSingleBriefInfo extends LinearLayout {
    private static final String TAG = "SingleBriefInfo";
    public PoiSingleBriefInfo(Context context) {
        super(context);
    }

    public PoiSingleBriefInfo(final Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.single_brief_info_without_rating_layout, this);
    }

    public void setInfo(String title, String img_str, String brief_info, String domain) {
        setName(title);
        setImage(img_str);
        setBriefInfo(brief_info, domain);
    }

    public void setName(String title) {
        TextView tv = findViewById(R.id.tvPoiSingleInfoTitle);
        tv.setText(title);
    }
    public void setImage(String img_str) {
        ImageView iv = findViewById(R.id.ivPoiSingleInfo);
        Picasso.with(PoiSingleBriefInfo.this.getContext()).load(img_str).into(iv);
    }
    public void setBriefInfo(String brief_info, String domain) {
        TextView tv = findViewById(R.id.tvPoiSingleInfoBriefInfo);
        if (brief_info.equals("---") || brief_info.equals("None")) {
            brief_info = "";
        }
        tv.setText(brief_info);

        // activity: 靠左, news: 靠右
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        params.weight = 1.0f;
        if (domain.equals("news")) {
            params.gravity = Gravity.RIGHT;
            tv.setGravity(Gravity.RIGHT);
        } else if (domain.equals("activity")) {
            Log.d("CHECK!!!", "domain="+ domain);
            params.gravity = Gravity.LEFT;
            tv.setGravity(Gravity.LEFT);
        }
        tv.setLayoutParams(params);


    }

}
