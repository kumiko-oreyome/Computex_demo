package com.ncku.iir.computex.book;



import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ncku.iir.computex.MainActivity;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;

import java.util.Timer;
import java.util.TimerTask;

public class ThirdActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";
    private Button exitBtn;
    private String mode;
    private String bookLink;
    private ImageButton homeBtn;
    private RobotAPI api;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        Bundle bundle = this.getIntent().getExtras();

        bookLink = bundle.getString("bookLink");
        mode = bundle.getString("mode");

        api = Global.api;
        api.robot.speak("可以掃描這個二維條碼進入博客來的網頁");


        Log.d(TAG, mode);

        ImageView qrCode = (ImageView) findViewById(R.id.QRCode);
        exitBtn = findViewById(R.id.exitBtn);
        homeBtn = findViewById(R.id.homeButton3);

        exitBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

               // ThirdActivity.this.
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
               // bundle.putString("mode", mode);
               // bundle.putString("bookLink", bookLink);

                //將Bundle物件assign給intent
                //intent.putExtras(bundle);
                //切換Activity
               // startActivity(intent);

            }
        });

        homeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent();
                intent.setClass(ThirdActivity.this, MainActivity.class);
                //切換Activity
                startActivity(intent);
            }
        });

        setQRCode(qrCode, bookLink);
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

}
