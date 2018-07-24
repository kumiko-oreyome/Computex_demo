package com.ncku.iir.computex.speech;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;
import com.ncku.iir.computex.FaceDetection;
import com.ncku.iir.computex.WhetherRestaurantFragment;
import com.ncku.iir.computex.core.IRequest;
import com.ncku.iir.computex.health.Question;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.core.RequestServer;
import com.ncku.iir.computex.VolleyCallback;
import com.ncku.iir.computex.book.FirstFragment;
import com.ncku.iir.computex.health.QuestionFragment;
import com.ncku.iir.computex.poi.PoiQuestionFragment;
import com.ncku.iir.computex.restaurant.RestaurantTypeQuestionFragment;
import com.ncku.iir.computex.util.TextUtil;
import com.ncku.iir.computex.weather.WeatherFragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends SpeechFragment implements IRequest {
    ImageButton weatherButton;
    ImageButton testingButton;
    ImageButton bookButton;
    ImageButton restaurantButton;
    ImageButton balloonButton;
    ImageButton newsButton;
    ImageButton modeButton;

    private RobotAPI api;
    private Timer timer;
    private RequestServer requestServer;

    public MainActivityFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);


        requestServer = new RequestServer(this) ;
        modeButton = view.findViewById(R.id.modeButton);

        weatherButton = view.findViewById(R.id.weatherButton);
        testingButton = view.findViewById(R.id.testingButton);
        bookButton = view.findViewById(R.id.bookButton);
        restaurantButton = view.findViewById(R.id.restaurantButton);
        balloonButton = view.findViewById(R.id.balloonButton);
        newsButton = view.findViewById(R.id.newsButton);

        setButtonOnclickImage(weatherButton, R.drawable.main_weather_main_icon, R.drawable.main_weather_push);
        setButtonOnclickImage(testingButton, R.drawable.main_testing_main_icon, R.drawable.main_testing_push);
        setButtonOnclickImage(bookButton, R.drawable.book_main_icon, R.drawable.book_push);
        setButtonOnclickImage(restaurantButton, R.drawable.main_restaurant_main_icon, R.drawable.main_restaurant_push);
        setButtonOnclickImage(balloonButton, R.drawable.main_activity_main_icon, R.drawable.main_activity_push);
        setButtonOnclickImage(newsButton, R.drawable.main_news_main_icon, R.drawable.main_news_push);

        Log.d("HEY", "recorder finish");

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("gg1234","change to face detection");
                 //bundle.putString("mode","Demo");
                 jumpNextFragment(new FaceDetection());
            }
        });

        restaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestaurantTypeQuestionFragment fragment = RestaurantTypeQuestionFragment.newInstance("command");
                jumpNextFragment(fragment);
            }
        });


        bookButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               jumpNextFragment(FirstFragment.newInstance("command"));
            }
        });



        newsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                speakWithFace(6500,"讓我推薦近期的新聞給您，先讓我更了解你一點吧!") ;
                Bundle bundle = new Bundle();
                bundle.putString("mode","comment");
                bundle.putString("domain", "news");
                jumpNextFragment(PoiQuestionFragment.newInstance(bundle));


            }
        });



        balloonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakWithFace(7000,"讓我推薦附近有趣的活動給您，先讓我更了解你一點吧!");
                Bundle bundle = new Bundle();
                bundle.putString("mode","comment");
                bundle.putString("domain", "activity");
                jumpNextFragment(PoiQuestionFragment.newInstance(bundle));
            }
        });



        testingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                api.robot.stopSpeak();
                speakWithFace(5500,"衛教小常識，你來試一試，要開始囉");

                requestServer.send_text_teach("start_btn" , new VolleyCallback(){
                    @Override
                    public void onSuccess(ArrayList result){

                        String re = "show_question" ;
                        if( String.valueOf(result.get(0)).equals(re) ){
                            //new一個Bundle物件，並將要傳遞的資料傳入
                            Bundle bundle = new Bundle();
                            bundle.putString("index",String.valueOf(result.get(1)) );
                            bundle.putString("question", String.valueOf(result.get(2)));
                            bundle.putString("A", String.valueOf(result.get(3)));
                            bundle.putString("B", String.valueOf(result.get(4)));
                            bundle.putString("answer", String.valueOf(result.get(5)));
                            bundle.putString("mode","demo");
                            jumpNextFragment(QuestionFragment.newInstance(bundle));
                        }

                    }
                });
            }
        });


        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               jumpNextFragment(WeatherFragment.newInstance("command"));
            }
        });


        api = Global.api;
        api.robot.setExpression(RobotFace.HIDEFACE);
        api.robot.stopSpeak();
        api.robot.stopSpeak();
        api.robot.speak("請選擇你想要使用的功能") ;
        return  view;
    }

    @Override
    public Context getContext() {
        return Global.ma;
    }

    @Override
    public void onGetMessage(String response) {
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

    private void setButtonOnclickImage(ImageButton ib, int norm, int press){
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[] {android.R.attr.state_pressed}, getResources().getDrawable(press));
        states.addState(new int[] {android.R.attr.state_focused}, getResources().getDrawable(press));
        states.addState(new int[] { }, getResources().getDrawable(norm));
        ib.setImageDrawable(states);
    }

    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {
                if(TextUtil.inKeywords(text,"餐廳","吃","餓")){
                    RestaurantTypeQuestionFragment fragment = RestaurantTypeQuestionFragment.newInstance("command");
                    MainActivityFragment.this.jumpNextFragment(fragment);
                }
                else if(TextUtil.inKeywords(text,"天氣")){
                    WeatherFragment fragment = WeatherFragment.newInstance("command");
                    MainActivityFragment.this.jumpNextFragment(fragment);
                }else if(TextUtil.inKeywords(text,"衛教","測驗","常識","健康","知識")){
                    speakWithFace(5500,"衛教小常識，你來試一試，要開始囉");

                    requestServer.send_text_teach("start_btn" , new VolleyCallback(){
                        @Override
                        public void onSuccess(ArrayList result){

                            String re = "show_question" ;
                            if( String.valueOf(result.get(0)).equals(re) ){
                                //new一個Bundle物件，並將要傳遞的資料傳入
                                Bundle bundle = new Bundle();
                                bundle.putString("index",String.valueOf(result.get(1)) );
                                bundle.putString("question", String.valueOf(result.get(2)));
                                bundle.putString("A", String.valueOf(result.get(3)));
                                bundle.putString("B", String.valueOf(result.get(4)));
                                bundle.putString("answer", String.valueOf(result.get(5)));
                                bundle.putString("mode","demo");
                                jumpNextFragment(QuestionFragment.newInstance(bundle));
                            }

                        }
                    });
                }else if(TextUtil.inKeywords(text,"書")){
                    jumpNextFragment(FirstFragment.newInstance("command"));
                }else if(TextUtil.inKeywords(text,"新聞","新鮮事")){
                    speakWithFace(6500,"讓我推薦近期的新聞給您，先讓我更了解你一點吧!") ;
                    Bundle bundle = new Bundle();
                    bundle.putString("mode","demo");
                    bundle.putString("domain", "news");
                    jumpNextFragment(PoiQuestionFragment.newInstance(bundle));

                }else if(TextUtil.inKeywords(text,"活動","出門","走走","好玩")){
                    speakWithFace(7000,"讓我推薦附近有趣的活動給您，先讓我更了解你一點吧!");
                    Bundle bundle = new Bundle();
                    bundle.putString("mode","demo");
                    bundle.putString("domain", "activity");
                    jumpNextFragment(PoiQuestionFragment.newInstance(bundle));
                }else if(TextUtil.inKeywords(text,"demo","展示")){
                    jumpNextFragment(new FaceDetection());
                } else{
                    Global.speak("抱歉我聽不懂");
                }
            }

        };
    }
}
