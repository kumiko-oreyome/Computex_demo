package com.ncku.iir.computex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;

public class RestaurantTypeQuestion extends AppCompatActivity {
    private String TAG = "RestaurantTypeQuestion";
    private TextView button1;
    private TextView button2;
    private TextView button3;
    private TextView button4;
    private TextView tv_dialogue;

    private RobotAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_type_question);
        api = new RobotAPI(getApplicationContext());
        api.robot.setExpression(RobotFace.HIDEFACE);
        api.robot.speak("請問要中式還西式還日式的?") ;
        button1 = findViewById(R.id.button_1);
        button2 = findViewById(R.id.button_2);
        button3 = findViewById(R.id.button_3);
        button4 = findViewById(R.id.button_4);
        tv_dialogue = findViewById(R.id.dialogue);
        tv_dialogue.setText("請問要中式還西式還日式的?");

        setTVListener(button1,"中式");
        setTVListener(button2,"西式");
        setTVListener(button3,"日式");
        setTVListener(button4,"");


        //sendRestaurantType("中式");
        //sendRestaurantType("西式");
        //sendRestaurantType("日式");
        //都沒有
        //sendRestaurantType("");
    }

    private void setTVListener(TextView button,final String s){
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                // TODO Auto-generated method stub
                sendRestaurantType(s);
            }
        });
    }

    private void sendRestaurantType(String situation){
        Intent intent = new Intent();
        intent.setClass(RestaurantTypeQuestion.this, briefInfo.class);
        Bundle bundle = new Bundle();
        bundle.putString("situation", situation);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
