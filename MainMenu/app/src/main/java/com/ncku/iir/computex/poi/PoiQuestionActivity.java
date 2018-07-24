package com.ncku.iir.computex.poi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.ncku.iir.computex.core.Connect_db;
import com.ncku.iir.computex.core.IConnectDB;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;


public class PoiQuestionActivity extends AppCompatActivity implements IConnectDB {
    private static String TAG = "PoiQuestionActivity";

    private RobotAPI api;
    private int[] btn_ans_ids = {R.id.button_l, R.id.button_r};
    private TextView[] btnAns = new TextView[2];
    private TextView tvQuestion;
    private int state = 0;
    private Connect_db connect_db;

    // data cols for database
    // question, answer 1, answer2
    private String[] questions;
    private String[] ans_1, ans_2;
    private String[][] answers = new String[3][];
    private String[] poi_final_responses = new String[2];

    // data for next page
    // types, responses(news), poi_response(activity)
    private String[] types = new String[3];
    private int[] responses = new int[3]; // 儲存回覆
    private String poi_response;

    // db
    private String domain;
    private String mode;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_question);
        api = new RobotAPI(getApplicationContext());
        connect_db = getConnectDB();


        Bundle bundle =this.getIntent().getExtras();

        domain = bundle.getString("domain");
        mode = bundle.getString("mode");
        Log.d(TAG, "domain:" + domain);
        Log.d(TAG, mode);

        tvQuestion = findViewById(R.id.dialogue);

         /* Database setup */
        connect_db.get_question(domain,Global.ma);
        /* End of Database setup  */





       Global.api.robot.speak("先讓我更了解你一點吧!") ;



    }

    private void fillData(HashMap<String, String[]> results) {


        try {
            questions = results.get("question");
            ans_1 = results.get("ans_1");
            ans_2 = results.get("ans_2");
            types = results.get("type");
        } catch (Exception e) {
            Log.d("ERROR in initial data", e.toString());
        }


        for (int i=0;i<questions.length;i++) {
            answers[i] = new String[] {ans_1[i], ans_2[i]};
        }

        Log.d(TAG, Arrays.toString(types));

        setQuestion(0);
        initialAnswerButtons();
    }

    private void fillData2(HashMap<String, String[]> results) {
        String qs = results.get("question")[0];
        String ans_l = results.get("ans_1")[0];
        String ans_r = results.get("ans_2")[0];

        poi_final_responses = new String[] {results.get("act_type1")[0], results.get("act_type2")[0]};

        state += 1;
        btnAns[0].setText(ans_l);
        btnAns[1].setText(ans_r);
        tvQuestion.setText(qs);
        Global.api.robot.speak(qs) ;

    }

    private void initialAnswerButtons() {
        for (int i=0; i < btn_ans_ids.length;i++) {

            btnAns[i] = findViewById(btn_ans_ids[i]);

            // set Text
            btnAns[i].setText(answers[0][i]);

            final int finalI = i;
            btnAns[i].setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(final View view) {
                    change_question(finalI);
                }
            });

        }
    }

    private void goToNextPage() {
        Global.api.robot.stopSpeak() ;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putIntArray("responses", responses);
        bundle.putStringArray("qs_types", types);
        bundle.putString("domain", domain);
        bundle.putString("mode", mode);
        bundle.putString("poi_response", poi_response);

        intent.putExtra("Bundle", bundle);
        intent.setClass(PoiQuestionActivity.this, PoiBriefInfoActivity.class);
        startActivity(intent);
    }

    private void change_question(int ans) {
        Global.api.robot.stopSpeak();
        responses[state] = ans;

        if (state == 2) {
            //TODO:: go to next page
            Log.d(TAG, "Ask questions completed");

            if (domain.equals("activity")) {
                poi_response = poi_final_responses[ans];
                Log.d("GET POI RESPONSE", poi_response);
            }
            goToNextPage();

        } else if (state < 2) {
            //TODO:: change questions
           if (needDBQuery()) {
               queryDB();
           } else {
                state += 1;
                setAnswer(state);
                setQuestion(state);
           }

        }
    }

    private boolean needDBQuery() {
        if (state == 1 && domain.equals("activity")) {
            return true;
        }
        else {
            return false;
        }
    }

    private void queryDB() {

        int next_type;
        if (responses[0] == 1) {
            if (responses[1] == 0) {
                // 3 室外動態
                next_type = 3;
            }else {
                // 4 室外靜態
                next_type = 4;
            }
        } else {
            if (responses[1] == 0) {
                // 5 室內動態
                next_type = 5;
            }else {
                // 6 室內靜態
                next_type = 6;
            }
        }

        Log.d(TAG, "next type = "+next_type);
        connect_db.get_question2(domain, Global.ma, next_type);
    }

    private void setQuestion(int i) {
        tvQuestion.setText(questions[i]);
        Global.api.robot.speak(questions[i]) ;
    }

    private void setAnswer(int j) {
        for(int i=0;i< btn_ans_ids.length;i++) {
            btnAns[i].setText(answers[j][i]);
        }
    }

    @Override
    public Connect_db getConnectDB() {
        return new Connect_db(this);
    }

    @Override
    public void onGetMessage_ques(HashMap<String, String[]> results) {
        Log.d(TAG, "Got message from db");
        fillData(results);
    }

    @Override
    public void onGetMessage_ques2(HashMap<String, String[]> results) {
        Log.d(TAG, "Got message2 from db");
        fillData2(results);
    }

    @Override
    public void onGetMessage_items(HashMap<String, String[]> results) throws UnsupportedEncodingException {

    }


}
