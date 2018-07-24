package com.ncku.iir.computex.book;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ncku.iir.computex.MainActivity;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.MainActivityFragment;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;
import com.ncku.iir.computex.util.TextUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends SpeechFragment {

    private static final String ARG_MODE = "mode";
    private static final String ARG_BL = "booklink";

    private static final String TAG = "ThirdFragment";

    private Button exitBtn;
    private String  mode;
    private String  bookLink;
    private ImageButton homeBtn;

    private Timer timer;

    public ThirdFragment() {
        // Required empty public constructor
    }


    public static ThirdFragment newInstance(String mode, String bookLink) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE , mode);
        args.putString(ARG_BL, bookLink);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getString(ARG_MODE);
            bookLink  = getArguments().getString(ARG_BL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_third, container, false);

        Global.speak("可以掃描這個二維條碼進入博客來的網頁");
        Log.d(TAG, mode);

        ImageView qrCode = (ImageView) view.findViewById(R.id.QRCode);
        exitBtn = view.findViewById(R.id.exitBtn);
        homeBtn = view.findViewById(R.id.homeButton3);

        exitBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                jumpNextFragment(new MainActivityFragment());
                //Intent intent = new Intent();
                //new一個Bundle物件，並將要傳遞的資料傳入

//                if(mode.equals("Demo")) {
//                    speakWithFace(4000,"我還可以提供你天氣的資訊");
//                    intent.setClass(ThirdActivity.this, WeatherMainActivity.class);
//                }else{
//                    intent.setClass(ThirdActivity.this, MainActivity.class);
//                }
               // intent.setClass(ThirdActivity.this, MainActivity.class);
               // Bundle bundle = new Bundle();
                //bundle.putString("mode", mode);
                //bundle.putString("bookLink", bookLink);

                //將Bundle物件assign給intent
                //intent.putExtras(bundle);
                //切換Activity
                //startActivity(intent);
            }
        });

        homeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                jumpNextFragment(new MainActivityFragment());
            }
        });

        setQRCode(qrCode, bookLink);


        return view;
    }

    private void setQRCode(ImageView qrCode, String link) {
        BarcodeEncoder encoder = new BarcodeEncoder();
        try{
            Bitmap bitmap = encoder.encodeBitmap(link, BarcodeFormat.QR_CODE, 500, 500);
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.d(TAG, "setQRCode: " + e.toString());
        }
    }

    private void speakWithFace(int milisec, String text){

        Global.api.robot.setExpression(RobotFace.PLEASED);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Global.api.robot.setExpression(RobotFace.HIDEFACE);
            }
        }, milisec);
        Global.speak(text) ;

    }
    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {
                if(TextUtil.inKeywords(text,"離開","謝謝","不")){
                    homeBtn.performClick();
                }
                else{
                    Global.speak("");
                }
            }
        };
    }
}
