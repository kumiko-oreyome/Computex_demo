package com.ncku.iir.computex.poi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;
import com.ncku.iir.computex.MainActivity;
import com.ncku.iir.computex.PageObj;
import com.ncku.iir.computex.PageView;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PoiDetailInfoActivity extends AppCompatActivity  {
    private static String TAG = "PoiDetailInfoActivity";

    private ViewPager mViewPager;
    private List<PageView> pageList;
    private ImageButton btnHome;

    private PageObj[] myObjs;
    private int num_of_data = 10;
    private String domain;
    public String mode;
    private RobotAPI api;
    private Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_detail_info);

        api = new RobotAPI(getApplicationContext());


        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                //TODO: 首頁
                Global.api.robot.stopSpeak();
                goToHomePage();
            }
        });


        //取得上一頁傳過來的 Key為Bundle的 Value
        Bundle bundle = getIntent().getBundleExtra("Bundle");
        int idx = bundle.getInt("idx", 0);
        num_of_data = bundle.getInt("num_of_data", 0);
        domain = bundle.getString("domain");
        mode = bundle.getString("mode");

        Log.d(TAG, "num of data: " + num_of_data);
        Log.d(TAG, mode);

        //重新命名TITLE
        setDomainTitle();
//        TextView tvTtile = findViewById(R.id.tvDetailInfoDomainTitle);
//        String chi_domain = "";
//        if (domain.equals("news")) {
//            chi_domain = "新聞";
//        } else if (domain.equals("activity")) {
//            chi_domain = "活動";
//        }
//        tvTtile.setText(chi_domain + "推薦清單");

        initMyObjs();
        initData();
        initView();

        mViewPager.setCurrentItem(idx);
        String str_speak = "";
        PageObj obj = myObjs[idx];
        if (domain.equals("news")) {
            str_speak = obj.getTitle() +", 你可以按更多資訊, 把他帶回家慢慢看喔";
        } else if (this.domain.equals("activity")) {
            str_speak = "想去"+obj.getTitle() +"的話請按更多資訊, 我可以告訴你怎麼去喔";
        }
        Global.api.robot.stopSpeak();
        Global.api.robot.speak(str_speak);

    }

    private void goToHomePage() {
        Intent intent = new Intent();
        intent.setClass(PoiDetailInfoActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();

        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setDomainTitle() {
        TextView tvTitle = findViewById(R.id.tvDetailInfoDomainTitle);
        String chi_domain = "";
        if (this.domain.equals("news")) {
            chi_domain = "新聞";
        } else if (this.domain.equals("activity")) {
            chi_domain = "活動";
        }
        tvTitle.setText(chi_domain + "推薦清單");
    }

    private void initMyObjs () {
        myObjs = PoiBriefInfoActivity.myObjs;//直接使用A的static物件
        Log.d(TAG, "got PageObjs from previous page, len = "+ myObjs.length);

    }

    private void initView() {
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(new SamplePagerAdapter());

    }

    private void initData() {
        pageList = new ArrayList<>();
        for (int i=0;i<num_of_data;i++) {
            pageList.add(new NewPoiPageView(PoiDetailInfoActivity.this, myObjs[i], domain, mode));
        }

    }



    private class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pageList.get(position));
//            Log.d("hii", ""+ position);
            return pageList.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

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
