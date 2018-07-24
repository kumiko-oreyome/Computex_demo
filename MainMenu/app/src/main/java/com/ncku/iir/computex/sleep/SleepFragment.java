package com.ncku.iir.computex.sleep;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;
import com.ncku.iir.computex.util.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class SleepFragment extends SpeechFragment {
    private HashMap<String, HashMap<String, ArrayList<String>>> keywordTable;

    private RobotAPI api;
    TextView question;
    Button goodbtn;
    Button badbtn;
    View view;

    public SleepFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialize
        view = inflater.inflate(R.layout.activity_sleep_q, container, false);
        question=view.findViewById(R.id.question);
        goodbtn=view.findViewById(R.id.good);
        badbtn=view.findViewById(R.id.bad);
        api= Global.api;

        Q1();


        return view;
    }

    public void Q1() {
        // Do work

                question.setText(getString(R.string.sleep_status));
                api.robot.speak(getString(R.string.sleep_status));
                goodbtn.setText(getString(R.string.good));
                badbtn.setText(getString(R.string.notgood));
                //sleep good
                goodbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        api.robot.stopSpeak();
                        //換smile fragment
                        Global.speak("來學習一些小知識吧");
                        jumpNextFragment(new SmileFragment());
                    }
                });
                //sleep bad
                badbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        api.robot.stopSpeak();
                        Q2();
                    }
                });

    }
    public void Q2() {
        // Do work
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                question.setText(getString(R.string.sleep_quality));
                api.robot.speak(getString(R.string.sleep_quality));
                goodbtn.setText(getString(R.string.bad));
                badbtn.setText(getString(R.string.awful));

                goodbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        api.robot.stopSpeak();
                        Q3();
                    }
                });

                badbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        api.robot.stopSpeak();
                        Q3();
                    }
                });
            }
        });
    }

    public void Q3() {
        // Do work
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                question.setText(getString(R.string.when_sleep));
                api.robot.speak(getString(R.string.when_sleep));
                goodbtn.setText("");
                goodbtn.setVisibility(View.GONE);
                badbtn.setText(getString(R.string.toNextBtn));

                badbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        api.robot.stopSpeak();
                        Q4();
                    }
                });

            }
        });
    }

    public void Q4() {
        // Do work
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                question.setText(getString(R.string.when_wake));
                api.robot.speak(getString(R.string.when_wake));
                goodbtn.setText("");
                goodbtn.setVisibility(View.GONE);
                badbtn.setText(getString(R.string.toNextBtn));

                badbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        api.robot.stopSpeak();
                        Q5();
                    }
                });

            }
        });
    }

    public void Q5() {
        // Do work
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                question.setText(getString(R.string.coffein));
                api.robot.speak(getString(R.string.coffein));
                goodbtn.setVisibility(View.VISIBLE);
                goodbtn.setText(getString(R.string.yes));
                badbtn.setText(getString(R.string.no));

                goodbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        api.robot.stopSpeak();
                        Q6();
                    }
                });

                badbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        api.robot.stopSpeak();
                        //換smile fragment
                        Global.speak("睡不好可能跟營養有關係,來學習一些小知識吧");
                        jumpNextFragment(new SmileFragment());
                    }
                });

            }
        });
    }

    public void Q6() {
        // Do work
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                question.setText(getString(R.string.when_drink));
                api.robot.speak(getString(R.string.when_drink));
                goodbtn.setVisibility(View.GONE);
                badbtn.setText(getString(R.string.toNextBtn));

                badbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        api.robot.stopSpeak();
                        Global.speak("睡不好可能跟營養有關係,來學習一些小知識吧");
                        jumpNextFragment(new SmileFragment());
                    }
                });

            }
        });
    }

    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {
                String str1 = goodbtn.getText().toString();
                String str2 = badbtn.getText().toString();

                if(text.contains("點")){
                    badbtn.performClick();
                }


//                if(text.equals(str1)){
//                    goodbtn.performClick();
//                }else if(text.equals(str2)){
//                    badbtn.performClick();
//                }
                keywordTable = Global.ma.getKeywordTable();
                String title = question.getText().toString();
                if(keywordTable.containsKey(title)) {

                    ArrayList<String> gKeys = keywordTable.get(title).get(str1);
                    ArrayList<String> bKeys = keywordTable.get(title).get(str2);

//                String goodStr = goodbtn.getText().toString();
//                String badStr =  badbtn.getText().toString();

                    if (TextUtil.inKeywords(text, bKeys)) {
                        badbtn.performClick();
                        } else if (TextUtil.inKeywords(text, gKeys)) {
                            goodbtn.performClick();
                        } else {
                            Global.speak("");
                    }}

            }
        };
    }
}
