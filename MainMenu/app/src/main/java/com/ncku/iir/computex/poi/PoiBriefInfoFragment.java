package com.ncku.iir.computex.poi;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.ncku.iir.computex.core.Connect_db;
import com.ncku.iir.computex.core.IConnectDB;
import com.ncku.iir.computex.PageObj;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.MainActivityFragment;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class PoiBriefInfoFragment extends SpeechFragment implements IConnectDB {
    private static final String TAG = "PoiBriefInfoActivity";




    private int num_of_data = 0;
    private static int[] brief_info_ids =  {R.id.poi_brief_info_0, R.id.poi_brief_info_1, R.id.poi_brief_info_2, R.id.poi_brief_info_3, R.id.poi_brief_info_4, R.id.poi_brief_info_5, R.id.poi_brief_info_6, R.id.poi_brief_info_7, R.id.poi_brief_info_8, R.id.poi_brief_info_9};

    // responses from previous page
    private int[] responses;
    private String[] qs_types;
    private String poi_response;

    // Data cols for PageObjs
    public static PageObj[] myObjs;
    private String[] titles, img_urls, brief_infos, detailed_infos, tag_1s, tag_2s, tag_3s, ratings, links;

    // db
    private String domain, chi_domain;
    private static Context context;

    private String mode;

    private View view;
    private ImageButton btnHome;


    public PoiBriefInfoFragment() {
        // Required empty public constructor
    }


    public static PoiBriefInfoFragment newInstance(Bundle args) {
        PoiBriefInfoFragment fragment = new PoiBriefInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //接換頁訊息
        if (getArguments() != null) {
            mode = getArguments().getString("mode");
            responses = getArguments().getIntArray("responses"); //取得上頁的答案
            qs_types = getArguments().getStringArray("qs_types");
            domain = getArguments().getString("domain");
            poi_response = getArguments().getString("poi_response");
        }

        //取得上一頁傳過來的 Key為Bundle的 Value



        Log.d(TAG, "domain:"+domain);
        Log.d(TAG, mode);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_poi_brief_info, container, false);
        this.view = view;
        Log.d(TAG, "into poi brief info page");
        btnHome = view.findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                //TODO: 首頁
                Global.api.robot.stopSpeak();
               jumpNextFragment(new MainActivityFragment());
            }
        });

        //重新命名TITLE
        setDomainTitle(view);
        getConnectDB().get_items(domain, Global.ma, responses, qs_types, poi_response);
        Global.speak("我覺得你對這些"+chi_domain+"有興趣喔") ;

        return view;
    }

    private void goToHomePage() {
        jumpNextFragment(new MainActivityFragment());

    }

    private void setDomainTitle(View view) {
        TextView tvTitle = view.findViewById(R.id.tvPoiBriefInfoDomainTitle);
        if (this.domain.equals("news")) {
            this.chi_domain = "新聞";
        } else if (this.domain.equals("activity")) {
            this.chi_domain = "地點";
        }
        tvTitle.setText(this.chi_domain + "推薦清單");
    }

    private void fillData( HashMap<String, String[]> results) throws UnsupportedEncodingException {
        try {
            titles = results.get("title");
            img_urls = results.get("img_url");
            brief_infos = results.get("brief_info");
            detailed_infos = results.get("detailed_info");

            tag_1s = results.get("tag_1");
            tag_2s = results.get("tag_2");
            tag_3s = results.get("tag_3");
            ratings = results.get("rating");
            links = results.get("link");

        } catch (Exception e) {
            Log.d("ERROR in initial data", e.toString());
        }
        saveAsPageObjs();   // save as PageObjs, 用作傳值至下一頁
        setInfos(view);         // 設定每個info資料

    }

    private void saveAsPageObjs() throws UnsupportedEncodingException {

        num_of_data = titles.length;
        Log.d(TAG, "num_of_data = "+num_of_data);
        myObjs = new PageObj[num_of_data];

        for(int i=0;i< num_of_data;i++) {
            // title, brief_info, detail_info, types, img_url
            myObjs[i] = new PageObj(titles[i],
                    brief_infos[i],
                    detailed_infos[i],
                    new String[] {tag_1s[i], tag_2s[i], tag_3s[i]},
                    img_urls[i],
                    ratings[i],
                    links[i],
                    domain
            );
        }
    }

    private void goToNextPage(int idx) {
        Bundle bundle = new Bundle();
        bundle.putInt("idx" , idx);
        bundle.putInt("num_of_data", num_of_data);
        bundle.putString("domain", domain);
        bundle.putString("mode", mode);
        jumpNextFragment( PoiDetailInfoFragment.newInstance(bundle));

    }

    private void setInfos(View view) {
        for(int i=0; i< num_of_data; i++) {
            final int finalI = i;
            PoiSingleBriefInfo singleBriefInfo = view.findViewById(brief_info_ids[i]);
//            Log.d("!!!!!!!!!!!!!!", ""+ singleBriefInfo.getHeight());
            singleBriefInfo.setInfo(titles[i], img_urls[i], brief_infos[i], this.domain);
            singleBriefInfo.setVisibility(View.VISIBLE);

           singleBriefInfo.setOnClickListener(new View.OnClickListener() {
                                                                   @Override
                                                                   public void onClick(View view) {
                                                                       Log.d(TAG, "inside click");

                                                                       goToNextPage(finalI);

                                                                   }
                                                               }

            );

        }

        for (int i=num_of_data;i<10;i++) {
            view.findViewById(brief_info_ids[i]).setVisibility(View.GONE); //GONE: 不可見, 不佔用原來的佈局空間
        }
    }



    @Override
    public Connect_db getConnectDB() {
        return new Connect_db(this);
    }

    @Override
    public void onGetMessage_ques(HashMap<String, String[]> results) {

    }

    @Override
    public void onGetMessage_ques2(HashMap<String, String[]> results) {

    }

    // db get data
    @Override
    public void onGetMessage_items(HashMap<String, String[]> results) throws UnsupportedEncodingException {
        Log.d(TAG, "GOT MESSAGE ITEMS");
        fillData(results); // 將database資料填入variables

    }

    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {
                Log.d(TAG,"onSentenceEnd 活動2");
            }
        };
    }
}
