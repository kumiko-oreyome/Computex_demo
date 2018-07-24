package com.ncku.iir.computex.restaurant;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.asus.robotframework.API.RobotAPI;
import com.ncku.iir.computex.ALS.RecommendCallback;
import com.ncku.iir.computex.ALS.RecommendationObject;
import com.ncku.iir.computex.ALS.Request;
import com.ncku.iir.computex.speech.MainActivityFragment;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BriefInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BriefInfoFragment extends SpeechFragment {


    private View view ;
    private static final String ARG_MODE = "mode";
    private static final String ARG_SITUATION = "situation";

    private String mode;
    private String situation;

    private static int[] brief_info_ids = {R.id.brief_info_0, R.id.brief_info_1, R.id.brief_info_2, R.id.brief_info_3, R.id.brief_info_4,
            R.id.brief_info_5, R.id.brief_info_6, R.id.brief_info_7, R.id.brief_info_8, R.id.brief_info_9};

    private static final String TAG = "briefInfo";
    private final String USERID = "104208676126208324781";
    private List<RecommendationObject> recmdList ;
    private ImageButton homeBtn;
    private RobotAPI api;

    public BriefInfoFragment() {

    }

    public static BriefInfoFragment newInstance(String mode, String situation) {
        BriefInfoFragment fragment = new BriefInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE, mode);
        args.putString(ARG_SITUATION, situation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getString(ARG_MODE);
            situation = getArguments().getString(ARG_SITUATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.brief_info_layout, container, false);
        this.view = view;
        homeBtn = view.findViewById(R.id.imageButton7);
        api = Global.api;


        Log.d(TAG,"situation "+situation);
        Log.d(TAG,mode);
        api.robot.stopSpeak();
        for(int i=0;i<brief_info_ids.length;i++) {
            final int finalI = i;
            view.findViewById(brief_info_ids[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "inside click");

                    InfoPageFragment fragment = InfoPageFragment.newInstance(finalI,mode,recmdList);
                    jumpNextFragment(fragment);


                }
            });

        }
        Global.speak("推薦這些餐廳給你參考") ;




        RestaurantRequest();



        homeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                jumpNextFragment(new MainActivityFragment());

            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("gg1234","onAttach");
        Global.ma.setCurrentReaction(getSpeechReaction());
        String url = "http://140.116.247.172:8888";
        Request request = new Request(Global.ma,url) ;
        Log.d("gg8787","123456");
        RecommendCallback callback = new RecommendCallback(this);
        request.getRecommendationList(USERID,situation, callback);
    }

    public void onGetRecmdList(List<RecommendationObject> recmdList){
        this.recmdList = recmdList ;
        for(int i=0;i<brief_info_ids.length;i++) {
            SingleBriefInfo single = view.findViewById(brief_info_ids[i]) ;
            single.setName(recmdList.get(i).getTitle());
            single.setImage(recmdList.get(i).getPicture());
        }

    }
    public void RestaurantRequest(){
        String url = "http://140.116.247.172:8888";
        Request request = new Request(Global.ma,url) ;
        RecommendCallback callback = new RecommendCallback(this);
        request.getRecommendationList(USERID,situation, callback);
    }
    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {
                Log.d(TAG,"text 1234 "+text);
            }
        };
    }
}
