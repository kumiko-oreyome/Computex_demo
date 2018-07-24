package com.ncku.iir.computex.poi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.ncku.iir.computex.core.Connect_db;
import com.ncku.iir.computex.core.IConnectDB;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;
import com.ncku.iir.computex.util.TextUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PoiQuestionFragment extends SpeechFragment implements IConnectDB {

    private HashMap<String, HashMap<String, ArrayList<String>>> keywordTable;

    private static String TAG = "PoiQuestionActivity";

    private int[] btn_ans_ids = {R.id.button_l, R.id.button_r};
    private TextView[] btnAns = new TextView[2];
    private TextView tvQuestion;
    private int state = 0;
    private View view;
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


    public PoiQuestionFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PoiQuestionFragment newInstance(Bundle args) {
        PoiQuestionFragment fragment = new PoiQuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            domain = getArguments().getString("domain");
            mode = getArguments().getString("mode");
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_poi_question, container, false);
        this.view = view;
        tvQuestion = view.findViewById(R.id.dialogue);

        for (int i=0; i < btn_ans_ids.length;i++) {

            btnAns[i] = view.findViewById(btn_ans_ids[i]);
        }
        getConnectDB().get_question(domain,Global.ma);
        return view;
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

            btnAns[i] = view.findViewById(btn_ans_ids[i]);
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
        Bundle bundle = new Bundle();
        bundle.putIntArray("responses", responses);
        bundle.putStringArray("qs_types", types);
        bundle.putString("domain", domain);
        bundle.putString("mode", mode);
        bundle.putString("poi_response", poi_response);
        jumpNextFragment(PoiBriefInfoFragment.newInstance(bundle));

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
        getConnectDB().get_question2(domain, Global.ma, next_type);
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

    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {

                String title = tvQuestion.getText().toString();
                String rightString = btnAns[0].getText().toString();
                String leftString = btnAns[1].getText().toString();
                keywordTable = ((com.ncku.iir.computex.speech.MainActivity)getActivity()).getKeywordTable();
                ArrayList<String> rKeys = keywordTable.get(title).get(rightString);
                ArrayList<String> lKeys = keywordTable.get(title).get(leftString);

//                String ans_1= btnAns[0].getText().toString();
//                String ans_2= btnAns[1].getText().toString();

                if(TextUtil.inKeywords(text,lKeys)){
                    change_question(1);
                }
                else if(TextUtil.inKeywords(text,rKeys)){
                    change_question(0);
                }
                else{
                    Global.speak("");
                }
            }
        };
    }
}
