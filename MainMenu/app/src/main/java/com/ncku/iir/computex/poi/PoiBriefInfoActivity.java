package com.ncku.iir.computex.poi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ncku.iir.computex.core.Connect_db;
import com.ncku.iir.computex.core.IConnectDB;
import com.ncku.iir.computex.MainActivity;
import com.ncku.iir.computex.PageObj;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;


public class PoiBriefInfoActivity  extends AppCompatActivity implements IConnectDB  {
    private static final String TAG = "PoiBriefInfoActivity";
    private ImageButton btnHome;

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



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_brief_info);

        Log.d(TAG, "into poi brief info page");
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
        mode = bundle.getString("mode");
        responses = bundle.getIntArray("responses"); //取得上頁的答案
        qs_types = bundle.getStringArray("qs_types");
        domain = bundle.getString("domain");
        poi_response = bundle.getString("poi_response");
        Log.d(TAG, "domain:"+domain);
        Log.d(TAG, mode);
        setDomainTitle();

        /* Database setup */
        context = getApplicationContext();
        getConnectDB().get_items(domain, context, responses, qs_types, poi_response);
        /* End of Database setup  */
        Global.api.robot.stopSpeak();
        Global.api.robot.speak("我覺得你對這些"+chi_domain+"有興趣喔") ;
    }

    private void goToHomePage() {
        Intent intent = new Intent();

        intent.setClass(PoiBriefInfoActivity.this, MainActivity.class);
        //new一個Bundle物件，並將要傳遞的資料傳入
        Bundle bundle = new Bundle();
        bundle.putString("mode", mode);
        //將Bundle物件assign給intent
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setDomainTitle() {
        TextView tvTitle = findViewById(R.id.tvPoiBriefInfoDomainTitle);
//        String chi_domain = "";
        if (this.domain.equals("news")) {
            this.chi_domain = "新聞";
        } else if (this.domain.equals("activity")) {
            this.chi_domain = "地點";
        }
        tvTitle.setText(this.chi_domain + "推薦清單");
    }

    private void fillData(HashMap<String, String[]> results) throws UnsupportedEncodingException {

        // results ["img_url", "title", "brief_info", "detailed_info", "tag_1"]
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
        setInfos();         // 設定每個info資料

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
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putInt("idx" , idx);
        bundle.putInt("num_of_data", num_of_data);
        bundle.putString("domain", domain);
        bundle.putString("mode", mode);

        intent.putExtra("Bundle", bundle);
        intent.setClass(PoiBriefInfoActivity.this, PoiDetailInfoActivity.class);
        startActivity(intent);
    }

    private void setInfos() {
        for(int i=0; i< num_of_data; i++) {
            final int finalI = i;
            PoiSingleBriefInfo singleBriefInfo = findViewById(brief_info_ids[i]);
//            Log.d("!!!!!!!!!!!!!!", ""+ singleBriefInfo.getHeight());
            singleBriefInfo.setInfo(titles[i], img_urls[i], brief_infos[i], this.domain);
            singleBriefInfo.setVisibility(View.VISIBLE);

            findViewById(brief_info_ids[i]).setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Log.d(TAG, "inside click");

                       goToNextPage(finalI);

                   }
               }

            );

        }

        for (int i=num_of_data;i<10;i++) {
            findViewById(brief_info_ids[i]).setVisibility(View.GONE); //GONE: 不可見, 不佔用原來的佈局空間
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



}
