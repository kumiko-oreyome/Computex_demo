package com.ncku.iir.computex;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class PoiQRCodeActivity extends AppCompatActivity {
    private static final String TAG = "PoiQRCodeActivity";

    private ImageButton btnHome;
    private Button exitBtn;

    private String domain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_qrcode);

        //取得上一頁傳過來的 Key為Bundle的 Value
        Bundle bundle = getIntent().getBundleExtra("Bundle");
        domain = bundle.getString("domain");
        String bookLink = bundle.getString("link");
//        Log.d(TAG, "domain = "+domain);

        //重新命名TITLE
        setDomainTitle();
//        TextView tvTtile = findViewById(R.id.tvQRCodeDomainTitle);
//        String chi_domain = "";
//
//        if (domain.equals("news")) {
//            chi_domain = "新聞";
//        } else if (domain.equals("activity")) {
//            chi_domain = "活動";
//        }
//        Log.d(TAG, "chi_domain="+chi_domain);
//        tvTtile.setText(chi_domain + "推薦清單");

        ImageView qrCode =  findViewById(R.id.ivPoiQRCode);
        setQRCode(qrCode, bookLink);

        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                //TODO: 首頁
                goToHomePage();
            }
        });

        exitBtn = findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                //TODO: 首頁
                goToHomePage();
            }
        });

        final GlobalService globalService = (GlobalService) getApplicationContext();
        globalService.gRobotApi.robot.speak("拿出你的手機掃描這個QR CODE吧") ;

    }

    private void setDomainTitle() {
        TextView tvTitle = findViewById(R.id.tvQRCodeDomainTitle);
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
        Intent intent = new Intent();
        intent.setClass(PoiQRCodeActivity.this, MainActivity.class);
        //new一個Bundle物件，並將要傳遞的資料傳入
        Bundle bundle = new Bundle();

        //將Bundle物件assign給intent
        intent.putExtras(bundle);
        startActivity(intent);
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


}
