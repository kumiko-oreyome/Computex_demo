package com.ncku.iir.computex.health;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.VolleyCallback;
import com.ncku.iir.computex.WhetherRestaurantFragment;
import com.ncku.iir.computex.core.IRequest;
import com.ncku.iir.computex.core.RequestServer;
import com.ncku.iir.computex.poi.PoiQuestionFragment;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.MainActivityFragment;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;
import com.ncku.iir.computex.util.TextUtil;
import com.ncku.iir.computex.weather.WeatherFragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;








public class ReplyFragment extends SpeechFragment implements IRequest {
    // TODO: Rename parameter arguments, choose names that match

    private RobotAPI api;
    private RequestServer requestServer;


    private TextView mText;
    private ImageView answer;
    private Button nextQuestion;
    private TextView textReply;
    private String mode;
    private Button exitBtn;
    private Timer timer;
    private long startTime ;
    private long tmpLong;
    private String correct;
    private String text;
    private String speakText;



    public ReplyFragment() {
        // Required empty public constructor
    }


    public static ReplyFragment newInstance(Bundle bundle) {
        ReplyFragment fragment = new ReplyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getString("mode");
            correct = getArguments().getString("correct");
            text  = getArguments().getString("text");
            speakText  = getArguments().getString("speak");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_reply, container, false);
        requestServer = new RequestServer(this);
        api = new RobotAPI(getActivity().getApplicationContext());

        textReply = view.findViewById(R.id.dialogue);
        answer = view.findViewById(R.id.answer);
        nextQuestion = view.findViewById(R.id.nextQ);
        exitBtn = view.findViewById(R.id.exit);

        textReply.setMovementMethod(new ScrollingMovementMethod());

        nextQuestion.setEnabled(false);
        api.robot.speak(speakText) ;

        startTime = SystemClock.elapsedRealtime();


        exitBtn.setVisibility(View.INVISIBLE);
        nextQuestion.setVisibility(View.INVISIBLE);
        textReply.setText(text);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextQuestion.setVisibility(View.VISIBLE);
                nextQuestion.setEnabled(true);
                exitBtn.setVisibility(View.VISIBLE);
            }
        }, 2500);
        if(correct.equals("true")){
            answer.setImageResource(R.drawable.correct);
        }
        else{
            answer.setImageResource(R.drawable.wrong);
        }

        exitBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

                if(mode.equals("command")) {
                    jumpNextFragment(new MainActivityFragment());
                }else{

                    //按下按鈕，顯示臉，講完話跳頁

                    api.robot.setExpression(RobotFace.PLEASED);
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            api.robot.setExpression(RobotFace.HIDEFACE);
                            Bundle bundle = new Bundle();
                            bundle.putString("mode", mode);
                            bundle.putString("return", "success");
                            jumpNextFragment(WhetherRestaurantFragment.newInstance(bundle));
                        }
                    }, 7500);
                    api.robot.speak("平常就要多注意健康方面的知識喔，接下來我有些好東西想要跟你分享");

                }
            }
        });

        nextQuestion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



                requestServer.send_text_teach("next_btn" , new VolleyCallback(){
                    @Override
                    public void onSuccess(ArrayList result){
                        if( String.valueOf(result.get(0)).equals("show_question") ){
                            //new一個Bundle物件，並將要傳遞的資料傳入
                            Bundle bundle = new Bundle();
                            bundle.putString("index",String.valueOf(result.get(1)) );
                            bundle.putString("question", String.valueOf(result.get(2)));
                            bundle.putString("A", String.valueOf(result.get(3)));
                            bundle.putString("B", String.valueOf(result.get(4)));
                            bundle.putString("answer", String.valueOf(result.get(5)));
                            bundle.putString("mode",mode);
                            //將Bundle物件assign給intent
                            jumpNextFragment(QuestionFragment.newInstance(bundle));
                        }

                        else if( String.valueOf(result.get(0)).equals("show_result") ){
                            if(mode.equals("command")) {
                                if (mode.equals("demo")) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("mode", mode);
                                    bundle.putString("return", "success");
                                    jumpNextFragment(WhetherRestaurantFragment.newInstance(bundle));
                                } else {
                                    jumpNextFragment(new MainActivityFragment());
                                }
                            }else {

                                //按下按鈕，顯示臉，講完話跳頁

                                api.robot.setExpression(RobotFace.PLEASED);
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        api.robot.setExpression(RobotFace.HIDEFACE);
                                        if (mode.equals("demo")) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("mode", mode);
                                            bundle.putString("return", "success");
                                            jumpNextFragment(WhetherRestaurantFragment.newInstance(bundle));
                                        } else {
                                            jumpNextFragment(new MainActivityFragment());
                                        }
                                        //new一個Bundle物件，並將要傳遞的資料傳入

                                    }
                                }, 7000);
                                api.robot.speak("看來你已經學到很多囉! 我有些好東西想要跟你分享");
                            }


                        }

                    }
                });
                
            }
        });




        return view;
    }

    @Override
    public Context getContext() {
        return Global.ma;
    }

    @Override
    public void onGetMessage(String text) {

    }

    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {
                //Global.speak("還敢下來阿 冰鳥");
                if(TextUtil.inKeywords(text,"試試看","再","下一題")){
                    nextQuestion.performClick();
                }else if(TextUtil.inKeywords(text,"離開","不","先這樣")){
                    exitBtn.performClick();
                }else{
                    Global.speak("");
                }
            }
        };
    }
}
