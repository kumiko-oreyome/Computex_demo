package com.ncku.iir.computex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by k12s35h813g on 2018/5/12.
 */

public class Reply extends RequestActivity {

    private TextView mText;
    private ImageView answer;
    private ImageView nextQuestion;
    private TextView textReply;

    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        textReply = findViewById(R.id.dialogue);
        answer = findViewById(R.id.answer);
        nextQuestion = findViewById(R.id.nextQ);



        Bundle reply =this.getIntent().getExtras();
        String correct = reply.getString("correct");

        String text  = reply.getString("text");
        textReply.setText(text);

        String image = reply.getString("image");
        if(image.equals("1")){
            answer.setImageResource(R.drawable.image_1);
        }
        else if(image.equals("2")){
            answer.setImageResource(R.drawable.image_2);
        }

        nextQuestion.setImageResource(R.drawable.next);

        /*
        if(correct.equals("true")){
            answer.setImageResource(R.drawable.correct);
            nextQuestion.setImageResource(R.drawable.next);
        }
        else{
            answer.setImageResource(R.drawable.wrong);
            nextQuestion.setImageResource(R.drawable.next);
        }
        */

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
                            //將Bundle物件assign給intent
                            intent.putExtras(bundle);
                            //切換Activity
                            startActivity(intent);
                        }

                        else if( String.valueOf(result.get(0)).equals("show_result") ){
                            Intent intent = new Intent();
                            intent.setClass(Reply.this, MainActivity.class);
                            //new一個Bundle物件，並將要傳遞的資料傳入
                            Bundle bundle = new Bundle();
                            //將Bundle物件assign給intent
                            intent.putExtras(bundle);
                            //切換Activity
                            startActivity(intent);
                        }

                    }
                });
            }
        });

    }


}
