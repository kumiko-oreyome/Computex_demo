package com.ncku.iir.computex;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * Created by veron on 2018/5/24.
 */

public class SingleBriefInfo extends LinearLayout {
    private static final String TAG = "SingleBriefInfo";
    public SingleBriefInfo(Context context) {
        super(context);
    }

    public SingleBriefInfo(final Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.single_brief_info_layout, this);
    }

    public void setInfo(String title, String img_str) {
//        TextView tv = findViewById(R.id.tvSingleInfo);
//        tv.setText(title);
        setName(title);
        setImage(img_str);
//        ImageView iv = findViewById(R.id.ivSingleInfo);
//        Picasso.with(SingleBriefInfo.this.getContext()).load(img_str).into(iv);

    }

    public void setName(String title) {
        TextView tv = findViewById(R.id.tvSingleInfo);
        tv.setText(title);
    }
    public void setImage(String img_str) {
        ImageView iv = findViewById(R.id.ivSingleInfo);
        if ( !img_str.isEmpty() ) {
            Picasso.with(SingleBriefInfo.this.getContext()).load(img_str).into(iv);
        }
    }

}
