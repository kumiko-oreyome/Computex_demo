package com.ncku.iir.computex.health;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;
import com.ncku.iir.computex.MainActivity;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.VolleyCallback;
import com.ncku.iir.computex.WhetherRestaurant;
import com.ncku.iir.computex.core.IRequest;
import com.ncku.iir.computex.core.RequestServer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 嘟嘟 on 2018/5/12.
 */

public class Reply extends AppCompatActivity implements IRequest {

    private RobotAPI api;
    private TextView mText;
    private ImageView answer;
    private Button nextQuestion;
    private TextView textReply;
    private String mode;
    private Button exitBtn;
    private Timer timer;
    private long startTime ;
    private long tmpLong;

    private RequestServer requestServer;

    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        api = new RobotAPI(getApplicationContext());


        requestServer = new RequestServer(this);

        textReply = findViewById(R.id.dialogue);
        answer = findViewById(R.id.answer);
        nextQuestion = findViewById(R.id.nextQ);
        exitBtn = findViewById(R.id.exit);

        textReply.setMovementMethod(new ScrollingMovementMethod());

        nextQuestion.setEnabled(false);


        startTime = SystemClock.elapsedRealtime();


        exitBtn.setVisibility(View.INVISIBLE);
        nextQuestion.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextQuestion.setVisibility(View.VISIBLE);
                nextQuestion.setEnabled(true);
                exitBtn.setVisibility(View.VISIBLE);
            }
        }, 2500);

        Bundle reply =this.getIntent().getExtras();
        mode = reply.getString("mode");
        String correct = reply.getString("correct");
        String text  = reply.getString("text");
        String speakText  = reply.getString("speak");

        textReply.setText(text);
        api.robot.speak(speakText) ;

        Log.d("REPLY-------------", mode);

        String image = reply.getString("image");

        if(correct.equals("true")){
            answer.setImageResource(R.drawable.correct);
        }
        else{
            answer.setImageResource(R.drawable.wrong);
        }

        if(image.equals("1")){
            answer.setImageResource(R.drawable.image_1);
        }
        else if(image.equals("2")){
            answer.setImageResource(R.drawable.image_2);
        }


        exitBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

                if(mode.equals("command")) {
                    Intent intent = new Intent();
                    intent.setClass(Reply.this, MainActivity.class);
                    startActivity(intent);
                }else{

                    //按下按鈕，顯示臉，講完話跳頁
                    api.robot.setExpression(RobotFace.PLEASED);
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            api.robot.setExpression(RobotFace.HIDEFACE);
                            Intent intent = new Intent();

                            intent.setClass(Reply.this, WhetherRestaurant.class);

                            //new一個Bundle物件，並將要傳遞的資料傳入
                            Bundle bundle = new Bundle();
                            bundle.putString("mode", mode);
                            bundle.putString("return", "success");
                            //將Bundle物件assign給intent
                            intent.putExtras(bundle);
                            //切換Activity
                            startActivity(intent);

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
                            Intent intent = new Intent();
                            intent.setClass(Reply.this, Question.class);
                            //new一個Bundle物件，並將要傳遞的資料傳入
                            Bundle bundle = new Bundle();
                            bundle.putString("index",String.valueOf(result.get(1)) );
                            bundle.putString("question", String.valueOf(result.get(2)));
                            bundle.putString("A", String.valueOf(result.get(3)));
                            bundle.putString("B", String.valueOf(result.get(4)));
                            bundle.putString("answer", String.valueOf(result.get(5)));
                            bundle.putString("mode",mode);
                            //將Bundle物件assign給intent
                            intent.putExtras(bundle);
                            //切換Activity
                            startActivity(intent);
                        }

                        else if( String.valueOf(result.get(0)).equals("show_result") ){

                            if(mode.equals("command")) {
                                Intent intent = new Intent();
                                if (mode.equals("Demo")) {
                                    intent.setClass(Reply.this, WhetherRestaurant.class);
                                } else {
                                    intent.setClass(Reply.this, MainActivity.class);
                                }
                                //new一個Bundle物件，並將要傳遞的資料傳入
                                Bundle bundle = new Bundle();
                                bundle.putString("mode", mode);
                                bundle.putString("return", "success");
                                //將Bundle物件assign給intent
                                intent.putExtras(bundle);
                                //切換Activity
                                startActivity(intent);

                            }else {

                                //按下按鈕，顯示臉，講完話跳頁

                                api.robot.setExpression(RobotFace.PLEASED);
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        api.robot.setExpression(RobotFace.HIDEFACE);
                                        Intent intent = new Intent();
                                        if (mode.equals("Demo")) {
                                            intent.setClass(Reply.this, WhetherRestaurant.class);
                                        } else {
                                            intent.setClass(Reply.this, MainActivity.class);
                                        }
                                        //new一個Bundle物件，並將要傳遞的資料傳入
                                        Bundle bundle = new Bundle();
                                        bundle.putString("mode", mode);
                                        bundle.putString("return", "success");
                                        //將Bundle物件assign給intent
                                        intent.putExtras(bundle);
                                        //切換Activity
                                        startActivity(intent);

                                    }
                                }, 7000);
                                api.robot.speak("看來你已經學到很多囉! 我有些好東西想要跟你分享");
                            }


                        }

                    }
                });
            }
        });

    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onGetMessage(String text) {

    }
}
