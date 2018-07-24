package com.ncku.iir.computex.poi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;
import com.ncku.iir.computex.PageObj;
import com.ncku.iir.computex.PageView;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.MainActivityFragment;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;
import com.ncku.iir.computex.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class PoiDetailInfoFragment extends SpeechFragment {
    private static String TAG = "PoiDetailInfoActivity";

    private ViewPager mViewPager;
    private List<PageView> pageList;
    private ImageButton btnHome;

    private PageObj[] myObjs;
    private int num_of_data ;
    private String domain;
    public String mode;
    private Timer timer;
    private  View view;
    private int idx;

    public PoiDetailInfoFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PoiDetailInfoFragment newInstance(Bundle args) {
        PoiDetailInfoFragment fragment = new PoiDetailInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            idx = getArguments().getInt("idx", 0);
            num_of_data = getArguments().getInt("num_of_data", 0);
            domain = getArguments().getString("domain");
            mode = getArguments().getString("mode");

        }

//        idx = 0;
//        domain ="activity";
//        mode = "demo";

        Log.d(TAG, "num of data: " + num_of_data);
        Log.d(TAG, mode);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_poi_detail_info, container, false);
        this.view = view;
        btnHome = view.findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                Global.api.robot.stopSpeak();
                goToHomePage();
            }
        });

        //重新命名TITLE
        setDomainTitle(view);

        initMyObjs();
        initData();
        initView(view);

        mViewPager.setCurrentItem(idx);
        String str_speak = "";
        PageObj obj = myObjs[idx];
        if (domain.equals("news")) {
            str_speak = obj.getTitle() +", 你可以按更多資訊, 把他帶回家慢慢看喔";
        } else if (this.domain.equals("activity")) {
            str_speak = "想去"+obj.getTitle() +"的話請按更多資訊, 我可以告訴你怎麼去喔";
        }
        Global.api.robot.stopSpeak();
        Global.speak(str_speak);
        return view;
    }

    private void goToHomePage() {
        jumpNextFragment(new MainActivityFragment());
    }

    private void setDomainTitle(View view) {
        TextView tvTitle = view.findViewById(R.id.tvDetailInfoDomainTitle);
        String chi_domain = "";
        if (this.domain.equals("news")) {
            chi_domain = "新聞";
        } else if (this.domain.equals("activity")) {
            chi_domain = "活動";
        }
        tvTitle.setText(chi_domain + "推薦清單");
    }

    private void initMyObjs () {
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        myObjs = PoiBriefInfoFragment.myObjs;//直接使用A的static物件
        Log.d(TAG, "got PageObjs from previous page, len = "+ myObjs.length);

    }

    private void initView(View view) {
        mViewPager = view.findViewById(R.id.pager);
        mViewPager.setAdapter(new SamplePagerAdapter());

    }

    private void initData() {
        pageList = new ArrayList<>();
        for (int i=0;i<num_of_data;i++) {
            pageList.add(new NewPoiPageView(Global.ma, myObjs[i], domain, mode));
        }

    }

    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {

//                Global.speak("活動3");
            }
        };
    }


    private class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            Log.d(TAG,""+pageList.size());
            return pageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pageList.get(position));
            Log.d("hii", ""+ position);
            return pageList.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    private void speakWithFace(int milisec, String text){
        final RobotAPI api = Global.api;
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
