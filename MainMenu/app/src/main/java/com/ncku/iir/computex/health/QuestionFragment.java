package com.ncku.iir.computex.health;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;



import com.asus.robotframework.API.RobotAPI;
import com.ncku.iir.computex.MainActivity;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.VolleyCallback;
import com.ncku.iir.computex.core.IRequest;
import com.ncku.iir.computex.core.RequestServer;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;
import com.ncku.iir.computex.util.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;


public class QuestionFragment extends SpeechFragment implements IRequest {

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
    private String A;
    private String B;

    private HashMap<String, HashMap<String, ArrayList<String>>> keywordTable;

    public QuestionFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static QuestionFragment newInstance(Bundle args) {
        QuestionFragment fragment = new QuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = getArguments().getString("question");
            A = getArguments().getString("A");
            B = getArguments().getString("B");
            answer = getArguments().getString("answer");
            mode = getArguments().getString("mode");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_question, container, false);
        mText = (TextView) view.findViewById(R.id.dialogue);
        buttonL = view.findViewById(R.id.button_l);
        buttonR = view.findViewById(R.id.button_r);
        Speak = view.findViewById(R.id.speak);

        mText.setText(""+question);
        buttonL.setText(A);
        buttonR.setText(B);
        Speak.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Speak.setEnabled(true);
            }
        }, 3000);

        api = Global.api;
        api.robot.speak(question) ;

        requestServer = new RequestServer(this);


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

                            //new一個Bundle物件，並將要傳遞的資料傳入
                            Bundle bundle = new Bundle();
                            bundle.putString("correct", String.valueOf(result.get(1)));
                            bundle.putString("text", String.valueOf(result.get(2)));
                            bundle.putString("speak", String.valueOf(result.get(3)));
                            bundle.putString("mode", mode);
                            jumpNextFragment(ReplyFragment.newInstance(bundle));

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

                requestServer.send_text_teach("answer_btn", "B", new VolleyCallback() {
                    @Override
                    public void onSuccess(ArrayList result) {
                        if (String.valueOf(result.get(0)).equals("show_correct")) {

                            //new一個Bundle物件，並將要傳遞的資料傳入
                            Bundle bundle = new Bundle();
                            bundle.putString("correct", String.valueOf(result.get(1)));
                            bundle.putString("text", String.valueOf(result.get(2)));
                            bundle.putString("speak", String.valueOf(result.get(3)));
                            bundle.putString("mode", mode);
                            //將Bundle物件assign給intent
                            jumpNextFragment(ReplyFragment.newInstance(bundle));
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

                lastClickTime = SystemClock.elapsedRealtime();


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
                String title = question;
                String rightString = buttonR.getText().toString();
                String leftString = buttonL.getText().toString();
                keywordTable = ((com.ncku.iir.computex.speech.MainActivity)getActivity()).getKeywordTable();
                ArrayList<String> rKeys = keywordTable.get(title).get(rightString);
                ArrayList<String> lKeys = keywordTable.get(title).get(leftString);

                if(TextUtil.inKeywords(text,lKeys)){ ;
                    buttonL.performClick();
                }else if(TextUtil.inKeywords(text,rKeys)){
                    buttonR.performClick();
                }else{
                    //Global.speak(leftString+" "+rightString);
                }
            }
        };
    }
}
