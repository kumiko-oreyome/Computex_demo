package com.ncku.iir.computex.restaurant;

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

import com.asus.robotframework.API.RobotAPI;
import com.ncku.iir.computex.ALS.RecommendationObject;
import com.ncku.iir.computex.MainActivity;
import com.ncku.iir.computex.PageView;
import com.ncku.iir.computex.R;

import java.util.ArrayList;
import java.util.List;

public class info_page extends AppCompatActivity {
    private ViewPager mViewPager;
    private List<PageView> pageList;
    private boolean isInitial = true ;
    private List<RecommendationObject> recmdList ;
    private String mode;
    private ImageButton homeBtn;
    private RobotAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);

        api = new RobotAPI(getApplicationContext());
        api.robot.stopSpeak();
        api.robot.speak("點擊喜歡或不喜歡，我可以記住您的喜好");
        homeBtn = findViewById(R.id.imageButton7);

        //取得上一頁傳過來的 Key為Bundle的 Value
        Bundle bundle = getIntent().getBundleExtra("Bundle");
        mode = bundle.getString("mode");

        ArrayList list = bundle.getParcelableArrayList("recmdList");
        this.recmdList = (List<RecommendationObject>) list.get(0);

        initData();
        initView();
        int idx = bundle.getInt("idx", 0);
        mViewPager.setCurrentItem(idx);

        homeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

                Intent intent = new Intent();
                intent.setClass(info_page.this, MainActivity.class);
                //切換Activity
                startActivity(intent);
            }
        });
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new SamplePagerAdapter());

    }

    private void initData() {
        pageList = new ArrayList<>();
        for(int i=0;i<recmdList.size();i++) {
            RecommendationObject item = recmdList.get(i) ;
            pageList.add(new NewPageView(info_page.this, item.getTitle(),
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
}
