package com.ncku.iir.computex.sleep;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.VolleyCallback;
import com.ncku.iir.computex.core.IConnectDB;
import com.ncku.iir.computex.core.IRequest;
import com.ncku.iir.computex.core.RequestServer;
import com.ncku.iir.computex.health.QuestionFragment;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;

import java.util.ArrayList;


public class SmileFragment extends SpeechFragment implements IRequest{
    private RobotAPI api;
    private RequestServer requestServer;

    public SmileFragment() {
        // Required empty public constructor
    }

    public static SmileFragment newInstance(String param1, String param2) {
        SmileFragment fragment = new SmileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        api= Global.api;
        View view = inflater.inflate(R.layout.fragment_smile, container, false);
        requestServer = new RequestServer(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                api.robot.setExpression(RobotFace.HIDEFACE);

                requestServer.send_text_teach("start_btn" , new VolleyCallback(){
                    @Override
                    public void onSuccess(ArrayList result){

                        String re = "show_question" ;
                        if( String.valueOf(result.get(0)).equals(re) ){
                            //new一個Bundle物件，並將要傳遞的資料傳入
                            Bundle bundle = new Bundle();
                            bundle.putString("index",String.valueOf(result.get(1)) );
                            bundle.putString("question", String.valueOf(result.get(2)));
                            bundle.putString("A", String.valueOf(result.get(3)));
                            bundle.putString("B", String.valueOf(result.get(4)));
                            bundle.putString("answer", String.valueOf(result.get(5)));
                            bundle.putString("mode","demo");
                            jumpNextFragment(QuestionFragment.newInstance(bundle));
                        }

                    }
                });


            }
        }, 1000);

        return view;
    }


    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {
                Global.speak("");
            }
        };
    }

    @Override
    public void onGetMessage(String text) {

    }
}
