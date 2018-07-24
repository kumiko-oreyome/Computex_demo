package com.ncku.iir.computex.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;
import com.ncku.iir.computex.util.TextUtil;


public class RestaurantTypeQuestionFragment extends SpeechFragment {

    private static final String ARG_MODE = "mode";

    private String TAG = "RestaurantTypeQuestion";
    private TextView button1;
    private TextView button2;
    private TextView button3;
    private TextView button4;
    private TextView tv_dialogue;
    private String mode;

    private RobotAPI api;

    public RestaurantTypeQuestionFragment() {

    }

    public static RestaurantTypeQuestionFragment newInstance(Bundle args) {
        RestaurantTypeQuestionFragment fragment = new RestaurantTypeQuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static RestaurantTypeQuestionFragment newInstance(String mode) {
        RestaurantTypeQuestionFragment fragment = new RestaurantTypeQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getString(ARG_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_restaurant_type_question, container, false);

        Log.d(TAG,"mode "+mode);
        api = new RobotAPI(Global.ma);
        api.robot.speak("請問你喜歡什麼類型的餐廳?") ;
        button1 = view.findViewById(R.id.button_1);
        button2 = view.findViewById(R.id.button_2);
        button3 = view.findViewById(R.id.button_3);
        button4 = view.findViewById(R.id.button_4);
        tv_dialogue = view.findViewById(R.id.dialogue);
        tv_dialogue.setText("請問你喜歡什麼類型的餐廳?");

        setTVListener(button1,"中式");
        setTVListener(button2,"西式");
        setTVListener(button3,"日式");
        setTVListener(button4,"");

        return view;
    }

    private void setTVListener(TextView button,final String s){
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                sendRestaurantType(s);
                api.robot.stopSpeak();
                api.robot.speak(s);
            }
        });
    }

    private void sendRestaurantType(String situation){
        Log.d(TAG,situation);
        BriefInfoFragment fragment = BriefInfoFragment.newInstance(mode,situation);
        jumpNextFragment(fragment);
    }


    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {
                Log.d(TAG,"on sentence end");

                if(TextUtil.inKeywords(text,"中式","中","中視")){
                    sendRestaurantType("中式");
                }else if(TextUtil.inKeywords(text,"西式","西","稀釋","騎士")){
                    sendRestaurantType("西式");
                }else if(TextUtil.inKeywords(text,"日式","日")){
                    sendRestaurantType("日式");
                }else if(TextUtil.inKeywords(text,"沒有","無","都","隨便","決定","你")){
                    sendRestaurantType("");
                }else{
                    api.robot.speak("抱歉  可以再說一次嗎");
                }
            }
        };
    }

}
