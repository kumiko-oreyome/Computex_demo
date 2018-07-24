package com.ncku.iir.computex.restaurant;


import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.asus.robotframework.API.RobotAPI;
import com.ncku.iir.computex.ALS.RecommendationObject;
import com.ncku.iir.computex.speech.MainActivityFragment;
import com.ncku.iir.computex.PageView;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;
import com.ncku.iir.computex.util.TextUtil;

import java.util.ArrayList;
import java.util.List;


public class InfoPageFragment extends SpeechFragment {

    private static final String ARG_IDX = "param1";
    private static final String ARG_MODE = "param2";
    private static final String ARG_RECLIST = "param3";


    private ViewPager mViewPager;
    private List<PageView> pageList;
    private int mIdx;
    private boolean isInitial = true ;
    private List<RecommendationObject> recmdList ;
    private String mode;
    private ImageButton homeBtn;
    private RobotAPI api;

    private View view;



    public InfoPageFragment() {
        // Required empty public constructor
    }

    public static InfoPageFragment newInstance(int idx, String mode,List<RecommendationObject> recmdList) {
        InfoPageFragment fragment = new InfoPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IDX , idx);
        args.putString(ARG_MODE,mode);

        ArrayList list = new ArrayList();
        list.add(recmdList) ;
        args.putParcelableArrayList( ARG_RECLIST,list) ;

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIdx = getArguments().getInt(ARG_IDX);
            mode = getArguments().getString(ARG_MODE);
            recmdList = (List<RecommendationObject>) getArguments().getParcelableArrayList(ARG_RECLIST).get(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_info_page, container, false);
        this.view = view;

        api = new RobotAPI(Global.ma);
        api.robot.stopSpeak();
        api.robot.speak("點擊喜歡或不喜歡，我可以記住您的喜好");
        homeBtn = view.findViewById(R.id.imageButton7);
        initData();
        initView();
        mViewPager.setCurrentItem(mIdx);
        homeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                    jumpNextFragment(new MainActivityFragment());
            }
        });

        return view;
    }

    private void initView() {
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(new SamplePagerAdapter());

    }

    private void initData() {
        pageList = new ArrayList<>();
        for(int i=0;i<recmdList.size();i++) {
            RecommendationObject item = recmdList.get(i) ;
            pageList.add(new NewPageView(Global.ma, item.getTitle(),
                    item.getCommentList(), item.getChineseType(), item.getPicture(), item.getResKey(), item.getAddress(),mode));
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
            Log.d("hii", ""+position);
            return pageList.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {

            }
        };
    }
}
