package com.ncku.iir.computex;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SingleFeatureBoardLinearLayout extends LinearLayout {
    private static final String TAG = "Single Feature";

    private TextView tv_data;

    public SingleFeatureBoardLinearLayout(Context context) {
        super(context);

    }

    public SingleFeatureBoardLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Log.d(TAG, "LLLLLLLLKKKKKL");

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.single_feature_board, this);

        tv_data = findViewById(R.id.feature_board_tv_data);


    }


    public void setIcon(int bkgd) {
        // set icon by icon_id
        this.setBackgroundResource(bkgd);
    }

    public void setDataText(String data) {
        tv_data.setText(data);
    }


}
//
//class LastTime extends android.support.v7.widget.AppCompatButton {
//    private static final String TAG = "SingleFeatureBoardButton";
//
//    public SingleFeatureBoardButton(Context context) {
//        super(context);
//    }
//
//    public SingleFeatureBoardButton(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//        Log.d("DDDD", "LLLLLLLLKKKKKL");
//
//        this.setBackgroundColor(Color.WHITE);
//        //Drawable drawableTop = this.getContext().getResources().getDrawable(R.drawable.sun);
//        //this.setCompoundDrawables(null, drawableTop, null, null);
//        this.setText("HEYHEY");
//    }
//
//
//}
