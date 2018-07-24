package com.ncku.iir.computex.health;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.VolleyCallback;
import com.ncku.iir.computex.core.IRequest;
import com.ncku.iir.computex.core.RequestServer;

import java.util.ArrayList;

/**
 * Created by k12s35h813g on 2018/5/12.
 */

public class Question extends AppCompatActivity implements IRequest {

    private RobotAPI api;
    private TextView mText;
    private TextView buttonL;
    private TextView buttonR;
    private String answer;
    private String mode;
    private String question;
    private Button Speak;
    private long lastClickTime = 0;
    private RequestServer requestServer;

//    private RequestServer requestServer ;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        requestServer = new RequestServer(this);

        mText = (TextView) findViewById(R.id.dialogue);
//        requestServer = new RequestServer(getApplicationContext(), mText) ;

        api = new RobotAPI(getApplicationContext());
        buttonL = findViewById(R.id.button_l);
        buttonR = findViewById(R.id.button_r);
        Speak = findViewById(R.id.speak);
        Bundle reply =this.getIntent().getExtras();



        question = reply.getString("question");
        String A = reply.getString("A");
        String B = reply.getString("B");

        answer = reply.getString("answer");
        mode = reply.getString("mode");

        mText.setText(""+question);
        buttonL.setText(A);
        buttonR.setText(B);

        api.robot.speak(question) ;


        Speak.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Speak.setEnabled(true);
            }
        }, 3000);



        buttonL.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                // TODO Auto-generated method stub
                api.robot.stopSpeak();
                if("A".equals(answer)){
                    api.robot.speak("答對了") ;
                }else{
                    api.robot.speak("答錯了") ;
                }

                requestServer.send_text_teach("answer_btn", "A", new VolleyCallback() {
                    @Override
                    public void onSuccess(ArrayList result) {
                        if (String.valueOf(result.get(0)).equals("show_correct")) {

                            Intent intent = new Intent();

                            intent.setClass(Question.this, Reply.class);

                            //new一個Bundle物件，並將要傳遞的資料傳入
                            Bundle bundle = new Bundle();
                            bundle.putString("correct", String.valueOf(result.get(1)));
                            bundle.putString("image", String.valueOf(result.get(2)));
                            bundle.putString("text", String.valueOf(result.get(3)));
                            bundle.putString("speak", String.valueOf(result.get(4)));
                            bundle.putString("mode", mode);
                            //將Bundle物件assign給intent
                            intent.putExtras(bundle);
                            //切換Activity
                            startActivity(intent);
                        }

                    }
                });


            }
        });

        buttonL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if("A".equals(answer)){
                        buttonL.setBackgroundResource(R.drawable.button_blue);
                    }else{
                        buttonL.setBackgroundResource(R.drawable.button_red);
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    buttonL.setBackgroundResource(R.drawable.button_health);
                }
                return false;
            }

        });

        buttonR.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                // TODO Auto-generated method stub
                api.robot.stopSpeak();

                if("B".equals(answer)){
                    api.robot.speak("答對了") ;
                }else{
                    api.robot.speak("答錯了") ;
                }
                requestServer.send_text_teach("answer_btn" , "B" , new VolleyCallback(){
                    @Override
                    public void onSuccess(ArrayList result){
                        if( String.valueOf(result.get(0)).equals("show_correct") ){
                            Intent intent = new Intent();
                            intent.setClass(Question.this, Reply.class);
                            //new一個Bundle物件，並將要傳遞的資料傳入
                            Bundle bundle = new Bundle();
                            bundle.putString("correct",String.valueOf(result.get(1)) );
                            bundle.putString("image",String.valueOf(result.get(2)) );
                            bundle.putString("text",String.valueOf(result.get(3)) );
                            bundle.putString("speak",String.valueOf(result.get(4)) );
                            bundle.putString("mode",mode);
                            //將Bundle物件assign給intent
                            intent.putExtras(bundle);
                            //切換Activity
                            startActivity(intent);
                        }

                    }
                });

            }
        });

        buttonR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if ("B".equals(answer)){
                        buttonR.setBackgroundResource(R.drawable.button_blue);
                    }else{
                        buttonR.setBackgroundResource(R.drawable.button_red);
                    }

                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    buttonL.setBackgroundResource(R.drawable.button_health);
                }
                return false;
            }

        });

        Speak.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                // TODO Auto-generated method stub

                if (SystemClock.elapsedRealtime() - lastClickTime < 5000){
                    return;
                }

                Log.d("QUESTION", "CLICKED---------");

                api.robot.speak(question);
                // replay button, temporal forbidden
//                try {
//                    Thread.sleep(3000);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    Log.d("QUESTION", "DELAY+---------");
//                }

                lastClickTime = SystemClock.elapsedRealtime();


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
