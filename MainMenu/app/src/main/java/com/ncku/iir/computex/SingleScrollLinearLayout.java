package com.ncku.iir.computex;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingleScrollLinearLayout extends LinearLayout {
    private static final String TAG = "SingleScroll";

    private TextView tv_date;
    private ImageView iv_icon;
    private TextView tv_temp;


    public SingleScrollLinearLayout(Context context) {
        super(context);

    }

    public SingleScrollLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Log.d(TAG, "LLLLLLLLKKKKKL");

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.single_scroll_item, this);

        initSetting();
        tv_date = findViewById(R.id.scroll_tv_date);
        iv_icon = findViewById(R.id.scroll_iv_icon);
        tv_temp = findViewById(R.id.scroll_tv_temp);


    }

    private void initSetting() {
        //Log.d(TAG, "initial setting of scroll layout");
        this.setPadding(30, 0, 30, 30);
    }

    public void setIcon(int icon_id) {
        // set icon by icon_id
        iv_icon.setImageResource(icon_id);
    }

    public void setDate(String date) {
        tv_date.setText(date);
    }

    public void setTemp(String temp) {
        String text = temp + getResources().getString((R.string.unit_temp));
        tv_temp.setText(text);
    }

}
