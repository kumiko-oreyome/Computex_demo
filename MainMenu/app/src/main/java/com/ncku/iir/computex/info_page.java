package com.ncku.iir.computex;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ncku.iir.computex.ALS.RecommendationObject;

import java.util.ArrayList;
import java.util.List;

public class info_page extends AppCompatActivity {
    private ViewPager mViewPager;
    private List<PageView> pageList;
    private boolean isInitial = true ;
    private List<RecommendationObject> recmdList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);

        //取得上一頁傳過來的 Key為Bundle的 Value
        Bundle bundle = getIntent().getBundleExtra("Bundle");

        ArrayList list = bundle.getParcelableArrayList("recmdList");
        this.recmdList = (List<RecommendationObject>) list.get(0);

        initData();
        initView();
        int idx = bundle.getInt("idx", 0);
        mViewPager.setCurrentItem(idx);
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
                    item.getCommentList(), item.getChineseType(), item.getPicture(), item.getResKey(), item.getAddress()));
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
