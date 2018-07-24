package com.ncku.iir.computex.poi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;

import com.ncku.iir.computex.PageObj;
import com.ncku.iir.computex.PageView;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.MainActivityFragment;
import com.ncku.iir.computex.weather.WeatherFragment;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class NewPoiPageView extends PageView {
    private static String TAG = "NewPoiPageView";
    private LinearLayout layoutRating;
    private TextView tvRating;
    private ImageView ivItem;
    private TextView tvTitle, tvDetailInfo;
    private TextView[] tvTypes = new TextView[3];
    private String domain;
    private String mode;
    private Timer timer;
    private RobotAPI api;

    private Button btnMoreInfo, btnExit;

    private int[] type_ids = {R.id.tvPageViewType1, R.id.tvPageViewType2, R.id.tvPageViewType3};


    public NewPoiPageView(Context context, final PageObj obj, final String domain, final String mode) {

        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.poi_page_content, null);
        api = Global.api;

        this.domain = domain;
        this.mode = mode;

        ivItem = view.findViewById(R.id.ivPageViewItem);
        layoutRating = view.findViewById(R.id.layoutPageViewRating);
        tvRating = view.findViewById(R.id.tvPageViewRating);
        tvTitle = view.findViewById(R.id.tvPageViewTitle);
        tvDetailInfo = view.findViewById(R.id.tvPageViewDetailInfo);
        tvDetailInfo.setMovementMethod(new ScrollingMovementMethod());

        btnMoreInfo = view.findViewById(R.id.btnMoreInfo);
        btnMoreInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                String qrcode_link = obj.getQrcodeLink();

                goToNextPage(qrcode_link);
            }
        });

        btnExit = view.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {

                if(mode.equals("demo")){
                    if(domain.equals("activity") || domain.equals("restaurant")) {
                        goToWeatherPage();
                    }else{
                        goToHomePage();
                    }
                }else {
                    goToHomePage();
                }
            }
        });

        for (int i=0;i<type_ids.length;i++) {
            tvTypes[i] = view.findViewById(type_ids[i]);
        }

        setRating(obj.getRating());
        setTitle(obj.getTitle());
        setDetailInfo(obj.getDetailInfo());
        setTypes(obj.getTypes());
        setImage(obj.getImgStr());

        addView(view);



    }
    private void goToHomePage() {
         Global.ma.changeFragment(new MainActivityFragment());
    }

    private void goToWeatherPage() {
        speakWithFace(4000,"我還可以提供你天氣的資訊");
        Global.ma.changeFragment(WeatherFragment.newInstance(mode));
    }

    private void goToNextPage(String qrcode_link) {
        Bundle bundle = new Bundle();
        bundle.putString("link", qrcode_link);
        bundle.putString("domain", this.domain);
        bundle.putString("mode", mode);
        Global.ma.changeFragment(PoiQRCodeFragment.newInstance(bundle));
    }

    public void setRating(String rating) {
        Log.d(TAG, "rating: "+ rating);
        if (rating.equals("None") || rating.equals("")) {
            layoutRating.setVisibility(View.GONE); // INVISIBILITY:4 意思是不可見的，但還佔著原來的空間
        } else {
            tvRating.setText(rating);
            layoutRating.setVisibility(View.VISIBLE);
        }
    }
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setDetailInfo(String detailInfo) {
        tvDetailInfo.setText(detailInfo);
    }

    public void setImage(String img_str) {
        try {
            Picasso.with(this.getContext()).load(img_str).into(ivItem);
        } catch (Exception e) {
            Log.d(TAG, "cannot set image");
        }
    }

    public void setTypes(String[] types) {
        Log.d(TAG, "types: "+ Arrays.toString(types));
        for (int i=0;i<type_ids.length;i++) {

            if (types[i].equals("None") || types[i].equals("")) {
                tvTypes[i].setVisibility(View.INVISIBLE); // INVISIBILITY:4 意思是不可見的，但還佔著原來的空間
            } else {
                tvTypes[i].setText(types[i]);
                tvTypes[i].setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void refreshView() {

    }

    private void speakWithFace(int milisec, String text){
        api.robot.stopSpeak();
        api.robot.setExpression(RobotFace.PLEASED);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                api.robot.setExpression(RobotFace.HIDEFACE);
            }
        }, milisec);
        api.robot.speak(text) ;

    }

}
