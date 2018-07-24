package com.ncku.iir.computex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;

import java.util.ArrayList;

/**
 * Created by k12s35h813g on 2018/5/12.
 */

public class Question extends RequestActivity {

    private RobotAPI api;
    private TextView mText;
    private TextView buttonL;
    private TextView buttonR;
    private String answer;

//    private RequestServer requestServer ;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        mText = (TextView) findViewById(R.id.dialogue);
//        requestServer = new RequestServer(getApplicationContext(), mText) ;

        api = new RobotAPI(getApplicationContext());
        buttonL = findViewById(R.id.button_l);
        buttonR = findViewById(R.id.button_r);
        Bundle reply =this.getIntent().getExtras();
        String question = reply.getString("question");
        String A = reply.getString("A");
        String B = reply.getString("B");
        answer = reply.getString("answer");

        mText.setText(""+question);
        buttonL.setText(A);
        buttonR.setText(B);
        api.robot.speak(question) ;

        buttonL.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                // TODO Auto-generated method stub
                requestServer.send_text_teach("answer_btn" , "A" , new VolleyCallback(){
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
                        api.robot.speak("答對了，你好棒喔哦哦哦") ;
                    }else{
                        buttonL.setBackgroundResource(R.drawable.button_red);
                        api.robot.speak("答錯了，蠢包") ;
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
                        api.robot.speak("答對了，你好棒喔喔喔喔喔喔喔喔喔喔喔喔") ;
                    }else{
                        buttonR.setBackgroundResource(R.drawable.button_red);
                        api.robot.speak("答錯了，傻眼") ;
                    }


                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    buttonL.setBackgroundResource(R.drawable.button_health);
                }
                return false;
            }

        });

    }
}
