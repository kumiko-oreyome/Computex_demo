package com.ncku.iir.computex.poi;

import android.graphics.Bitmap;

import android.os.Bundle;
import android.app.Fragment;
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
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.MainActivityFragment;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;
import com.ncku.iir.computex.util.TextUtil;
import com.ncku.iir.computex.weather.WeatherFragment;

import java.util.Timer;
import java.util.TimerTask;

public class PoiQRCodeFragment extends SpeechFragment {
    private static final String TAG = "PoiQRCodeActivity";

    private ImageButton btnHome;
    private Button exitBtn;

    private String domain;
    private String mode;
    private String bookLink;

    private Timer timer;
    private RobotAPI api;

    public PoiQRCodeFragment() {
        // Required empty public constructor
    }

    public static PoiQRCodeFragment newInstance(Bundle args) {
        PoiQRCodeFragment fragment = new PoiQRCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            domain = getArguments().getString("domain");
            mode = getArguments().getString("mode");
            bookLink = getArguments().getString("link");
            Log.d(TAG, "domain = "+domain);
            Log.d(TAG, mode);
        }
        Log.d(TAG, mode);
        Log.d(TAG, domain);
        Log.d(TAG, bookLink);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_poi_qrcode, container, false);
        api = Global.api;
        //重新命名TITLE
        setDomainTitle(view);

        ImageView qrCode =  view.findViewById(R.id.ivPoiQRCode);
        setQRCode(qrCode, bookLink);

        btnHome = view.findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                //TODO: 首頁
                Global.api.robot.stopSpeak();
                goToHomePage();
            }
        });

        exitBtn = view.findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                Global.api.robot.stopSpeak();
                if(mode.equals("demo")) {
                    if(domain.equals("activity") || domain.equals("restaurant")) {
                        goToWeatherPage();
                    }else{
                        goToHomePage();
                    }

                }else{
                    goToHomePage();
                }
            }
        });
        Global.api.robot.stopSpeak();
        Global.api.robot.speak("拿出你的手機掃描這個條碼吧") ;

        return view;
    }

    private void setDomainTitle(View view) {
        TextView tvTitle = view.findViewById(R.id.tvQRCodeDomainTitle);
        String chi_domain = "";
        if (this.domain.equals("news")) {
            chi_domain = "新聞";
        } else if (this.domain.equals("activity")) {
            chi_domain = "活動";
        }else if (this.domain.equals("restaurant")) {
            chi_domain = "餐廳";
        }
        tvTitle.setText(chi_domain + "推薦");
    }

    private void goToHomePage() {
        jumpNextFragment(new MainActivityFragment());
    }

    private void goToWeatherPage() {
        speakWithFace(4000,"我還可以提供你天氣的資訊");
        jumpNextFragment(WeatherFragment.newInstance(mode));
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
    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {

            @Override
            public void onSentenceEnd(String text) {
                if(TextUtil.inKeywords(text,"謝謝","離開","不錯")){
                    exitBtn.performClick();
                }else{
                    Global.speak("");
                }
            }
        };
    }
}
