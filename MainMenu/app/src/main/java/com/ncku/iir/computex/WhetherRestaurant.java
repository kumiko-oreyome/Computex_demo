package com.ncku.iir.computex;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;
import com.ncku.iir.computex.book.FirstActivity;
import com.ncku.iir.computex.poi.PoiQuestionActivity;
import com.ncku.iir.computex.restaurant.RestaurantTypeQuestion;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class WhetherRestaurant extends  AppCompatActivity{
    private RobotAPI api;
    private TextView buttonL;
    private TextView buttonR;
    private TextView Speak;
    private TextView Dialog;
    private Class targetClass;
    private Class[] domainClasses = new Class[] {FirstActivity.class, RestaurantTypeQuestion.class, PoiQuestionActivity.class,  PoiQuestionActivity.class};
    private String[] domains = new String[] {"book","rest","activity","news"};
    private String domain;
    private String speakText;
    private int domainIndex;
    private int newIndex;
    private String mode;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whether_restaurant);


        Bundle bundle = this.getIntent().getExtras();
//        mText = (TextView) findViewById(R.id.dialogue);
////        requestServer = new RequestServer(getApplicationContext(), mText) ;

        api = new RobotAPI(getApplicationContext());


        api.robot.stopSpeak();
        buttonL = findViewById(R.id.button_l);
        buttonR = findViewById(R.id.button_r);
        Dialog = findViewById(R.id.dialogue);
        Speak = findViewById(R.id.speak);

        mode = bundle.getString("mode");

        domainIndex = randomChoice();




        Dialog.setText(speakText);
        api.robot.speak(speakText);

        buttonL.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

                if(domainIndex != 3) { //NOT NEWS
                    api.robot.stopSpeak();
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClass(WhetherRestaurant.this, domainClasses[domainIndex]);
                    //new一個Bundle物件，並將要傳遞的資料傳入
                    Bundle bundle = new Bundle();
                    bundle.putString("mode", mode);
                    bundle.putString("domain", domain);

                    //將Bundle物件assign給intent
                    intent.putExtras(bundle);
                    //切換Activity
                    startActivity(intent);
                }else{

                    newIndex = randomChoice();
                    while(newIndex == domainIndex){
                        newIndex = randomChoice();
                    }
                    api.robot.stopSpeak();
                    speakAccordingToNextPage(newIndex);

                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClass(WhetherRestaurant.this, domainClasses[newIndex]);
                    //new一個Bundle物件，並將要傳遞的資料傳入
                    Bundle bundle = new Bundle();
                    bundle.putString("mode", mode);
                    bundle.putString("domain", domain);

                    //將Bundle物件assign給intent
                    intent.putExtras(bundle);
                    //切換Activity
                    startActivity(intent);
                }


            }
        });

        //否
        buttonR.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

                if(domainIndex == 3){ //NEWS
                    api.robot.stopSpeak();
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClass(WhetherRestaurant.this, domainClasses[domainIndex]);
                    Bundle bundle = new Bundle();
                    bundle.putString("mode", mode);
                    bundle.putString("domain", domain);

                    intent.putExtras(bundle);
                    startActivity(intent);

                }else {

                    api.robot.stopSpeak();
                    Intent intent = new Intent();

                    Log.d("DOMAIN", domain);
                    newIndex = randomChoice();
                    while (newIndex == domainIndex) {
                        newIndex = randomChoice();
                    }
                    Log.d("DOMAIN", domain);


                    speakAccordingToNextPage(newIndex);


                    intent.setClass(WhetherRestaurant.this, domainClasses[newIndex]);
                    //new一個Bundle物件，並將要傳遞的資料傳入
                    Bundle bundle = new Bundle();
                    bundle.putString("mode", mode);
                    bundle.putString("domain", domain);
                    //將Bundle物件assign給intent
                    intent.putExtras(bundle);
                    //切換Activity
                    startActivity(intent);
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

}
