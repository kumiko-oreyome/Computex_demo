package com.ncku.iir.computex.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.asus.robotframework.API.RobotAPI;
import com.ncku.iir.computex.ALS.RecommendationObject;
import com.ncku.iir.computex.ALS.Request;
import com.ncku.iir.computex.MainActivity;
import com.ncku.iir.computex.R;

import java.util.ArrayList;
import java.util.List;

public class briefInfo extends AppCompatActivity {
    private static int[] brief_info_ids = {R.id.brief_info_0, R.id.brief_info_1, R.id.brief_info_2, R.id.brief_info_3, R.id.brief_info_4,
            R.id.brief_info_5, R.id.brief_info_6, R.id.brief_info_7, R.id.brief_info_8, R.id.brief_info_9};

    private static final String TAG = "briefInfo";
    private final String USERID = "104208676126208324781";
    private List<RecommendationObject> recmdList ;
    private String situation;
    private String mode;
    private ImageButton homeBtn;


    private RobotAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.brief_info_layout);

        homeBtn = findViewById(R.id.imageButton7);
        api = new RobotAPI(getApplicationContext());

        Bundle bundle = this.getIntent().getExtras();
        situation = bundle.getString("situation");
        mode = bundle.getString("mode");
        Log.d(TAG,"situation "+situation);
        Log.d(TAG,mode);
        api.robot.stopSpeak();


        for(int i=0;i<brief_info_ids.length;i++) {
            final int finalI = i;
            findViewById(brief_info_ids[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "inside click");
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    ArrayList list = new ArrayList();
                    list.add(recmdList) ;
                    bundle.putParcelableArrayList("recmdList",list) ;

                    bundle.putInt("idx" , finalI);
                    bundle.putString("mode",mode);
                    intent.putExtra("Bundle", bundle);
                    intent.setClass(briefInfo.this, info_page.class);
                    startActivity(intent);
                }
           });

        }


        RestaurantRequest();



        homeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

                Intent intent = new Intent();
                intent.setClass(briefInfo.this, MainActivity.class);
                //切換Activity
                startActivity(intent);
            }
        });
    }

    public void RestaurantRequest(){
        String url = "http://140.116.247.172:8888";
        Request request = new Request(this,url) ;
       // RecommendCallback callback = new RecommendCallback(briefInfo.this);
       // request.getRecommendationList(USERID,situation, callback);
    }

    public void onGetRecmdList(List<RecommendationObject> recmdList){
        this.recmdList = recmdList ;
        for(int i=0;i<brief_info_ids.length;i++) {
            SingleBriefInfo single = findViewById(brief_info_ids[i]) ;
            single.setName(recmdList.get(i).getTitle());
            single.setImage(recmdList.get(i).getPicture());
        }

    }



}
