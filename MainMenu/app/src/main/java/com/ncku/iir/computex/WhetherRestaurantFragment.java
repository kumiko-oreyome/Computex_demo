package com.ncku.iir.computex;


import android.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;
import com.google.logging.v2.GetLogMetricRequest;
import com.ncku.iir.computex.book.FirstFragment;
import com.ncku.iir.computex.poi.PoiQuestionFragment;
import com.ncku.iir.computex.restaurant.RestaurantTypeQuestionFragment;

import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;
import com.ncku.iir.computex.util.TextUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class WhetherRestaurantFragment extends SpeechFragment {

    private RobotAPI api;
    private TextView buttonL;
    private TextView buttonR;
    private TextView Speak;
    private TextView Dialog;
    private Class targetClass;
    private Class[] domainClasses = new Class[] {FirstFragment.class, RestaurantTypeQuestionFragment.class, PoiQuestionFragment.class,  PoiQuestionFragment.class};
    private String[] domains = new String[] {"book","rest","activity","news"};

    private String domain;
    private String speakText;
    private int domainIndex;
    private int newIndex;
    private String mode;
    private Timer timer;

    public WhetherRestaurantFragment() {
    }

    public static WhetherRestaurantFragment newInstance(Bundle args) {
        WhetherRestaurantFragment fragment = new WhetherRestaurantFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getString("mode");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_whether_restaurant, container, false);
        api = Global.api;

        buttonL = view.findViewById(R.id.button_l);
        buttonR = view.findViewById(R.id.button_r);
        Dialog = view.findViewById(R.id.dialogue);
        Speak = view.findViewById(R.id.speak);

        domainIndex = randomChoice();

        Dialog.setText(speakText);
        Global.speak(speakText);

        buttonL.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

                if(domainIndex != 3) { //NOT NEWS
                    api.robot.stopSpeak();
                    // TODO Auto-generated method stub

                    //new一個Bundle物件，並將要傳遞的資料傳入
                    Bundle bundle = new Bundle();
                    bundle.putString("mode", mode);
                    bundle.putString("domain", domain);
                    Fragment fragment = getFragmentByClass(domainClasses[domainIndex],bundle);
                    jumpNextFragment(fragment);
                }else{

                    newIndex = randomChoice();
                    while(newIndex == domainIndex){
                        newIndex = randomChoice();
                    }
                    api.robot.stopSpeak();
                    speakAccordingToNextPage(newIndex);
                    Bundle bundle = new Bundle();
                    bundle.putString("mode", mode);
                    bundle.putString("domain", domain);
                    Fragment fragment = getFragmentByClass(domainClasses[newIndex],bundle);
                    jumpNextFragment(fragment);

                }
            }
        });

        buttonR.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

                if(domainIndex == 3){ //NEWS
                    api.robot.stopSpeak();
                    // TODO Auto-generated method stub

                    Bundle bundle = new Bundle();
                    bundle.putString("mode", mode);
                    bundle.putString("domain", domain);

                    Fragment f = getFragmentByClass(domainClasses[domainIndex],bundle);
                    jumpNextFragment(f);

                }else {
                    api.robot.stopSpeak();
                    Log.d("DOMAIN", domain);
                    newIndex = randomChoice();
                    while (newIndex == domainIndex) {
                        newIndex = randomChoice();
                    }
                    Log.d("DOMAIN", domain);
                    speakAccordingToNextPage(newIndex);
                    //new一個Bundle物件，並將要傳遞的資料傳入
                    Bundle bundle = new Bundle();
                    bundle.putString("mode", mode);
                    bundle.putString("domain", domain);

                    Fragment f = getFragmentByClass(domainClasses[newIndex],bundle);
                    jumpNextFragment(f);
                }

            }
        });

        Speak.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                // TODO Auto-generated method stub
                api.robot.speak(speakText);
            }
        });
        return view;
    }

    private Fragment getFragmentByClass(Class clazz,Bundle bundle){
        Method method = null;
        try {
            method = clazz.getMethod("newInstance", Bundle.class);
        } catch (NoSuchMethodException e) {
            Global.speak("反射1失敗");
        }

        Object o = null;

        try {
            o = method.invoke(null, bundle);
        } catch (IllegalAccessException e) {
            Global.speak("反射2-1失敗");
        } catch (InvocationTargetException e) {
            Global.speak("反射2-2失敗");
        }
        Fragment f = (Fragment)o;

        return f;
    }


    private int randomChoice(){
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(4);

        if(index == 0){ // Book
            speakText = "推薦幾本書給你好嗎 ?";
            targetClass = domainClasses[0];
            domain =  domains[0];
        }else if(index == 1){
            speakText = "要不要來點好料的 ?";
            targetClass = domainClasses[1];
            domain =  domains[1];
        }else if(index == 2){
            speakText = "想不想出門走走 ?";
            targetClass = domainClasses[2];
            domain =  domains[2];
        }else{
            speakText = "你今天看新聞了嗎 ? ";
            targetClass = domainClasses[3];
            domain =  domains[3];
        }

        return index;
    }

    private void speakAccordingToNextPage(int index){
        if(index == 0){
            speakWithFace(5000,"那麼讓我推薦你會喜歡的書籍");
        }else if(index == 1){
            speakWithFace(5000,"那麼讓我推薦餐廳給你吧");
        }else if(index == 2){
            speakWithFace(7500,"那麼讓我推薦附近有趣的活動給您，先讓我更了解你一點吧!");
        }else if(index == 3){
            speakWithFace(7000,"那麼讓我推薦近期的新聞給您，先讓我更了解你一點吧!");
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

    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {
                String leftString = buttonL.getText().toString();
                String rightString = buttonR.getText().toString();

                if(TextUtil.inKeywords(text, "不","否","沒")){
                    buttonR.performClick();
                }
                else if(TextUtil.inKeywords(text, "是","好","想","看")){
                    buttonL.performClick();
                }
                else{
                    Global.speak("抱歉再說一次");
                }
            }
        };
    }
}
