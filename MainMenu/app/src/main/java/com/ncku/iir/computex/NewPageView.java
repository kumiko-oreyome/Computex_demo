package com.ncku.iir.computex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ncku.iir.computex.ALS.Request;
import com.ncku.iir.computex.ALS.StoreComment;
import com.ncku.iir.computex.ALS.UpdateCallback;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class NewPageView extends PageView{
    public NewPageView(Context context, String resName, final ArrayList<StoreComment> commentList, String res_type1, String imgurl, final String res_key, final String address) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.page_content, null);

        String url = "http://140.116.247.172:8888";
        final String USERID = "104208676126208324781";
        final Request request = new Request(context,url) ;
        final UpdateCallback callback = new UpdateCallback();
        final GlobalService globalService = (GlobalService) context.getApplicationContext();

        ImageView img = (ImageView) view.findViewById(R.id.res_image);
        if ( !imgurl.isEmpty() ) {
            Picasso.with(context).load(imgurl).into(img);
        }
//

        TextView res_textView = (TextView) view.findViewById(R.id.res_textView);
        res_textView.setText(resName);

        TextView add_textView = (TextView) view.findViewById(R.id.add_textView);
        add_textView.setText(address + "\n " + commentList.get(0).getText());
//        add_textView.setText(address);

        TextView type1 = (TextView) view.findViewById(R.id.res_type_1);
        type1.setText(res_type1);

        TextView rate = (TextView) view.findViewById(R.id.rating);
        rate.setText(""+commentList.get(0).getRating());
        ImageView star = (ImageView) view.findViewById(R.id.star);
//
//        if(rating.equals("None")){
//            star.setVisibility(View.INVISIBLE);
//            rate.setVisibility(View.INVISIBLE);
//        }
//        else{
//            rate.setText(rating);
//        }

        Button like_button = (Button) view.findViewById(R.id.like_button);
        Button dislike_button = (Button) view.findViewById(R.id.dislike_button);
        Button qr_button = (Button) view.findViewById(R.id.QR_button) ;

        like_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                request.updateUserPreference(USERID,res_key,"Y", callback);
                globalService.gRobotApi.robot.speak("好的，已更新您的喜好，並幫您加到最愛清單") ;
            }
        });

        dislike_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                request.updateUserPreference(USERID,res_key,"N", callback);
                globalService.gRobotApi.robot.speak("好的，已更新您的喜好") ;
            }
        });

        qr_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String start = null;
                String end = null ;
                try {
                    start = URLEncoder.encode("台北世界貿易中心展覽一館", "utf-8");
                    end = URLEncoder.encode(address, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String qrcode_link = String.format("https://www.google.com.tw/maps/dir/%s/%s", start, end);
                Log.d("sendQR",qrcode_link);
                globalService.gRobotApi.robot.speak("好的，請掃描代碼") ;
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("domain", "restaurant");
                bundle.putString("link", qrcode_link);
                intent.putExtra("Bundle", bundle);
                intent.setClass(getContext(), PoiQRCodeActivity.class);
                getContext().startActivity(intent);
            }
        });


//        TextView type2 = (TextView) view.findViewById(R.id.res_type_2);
//        type2.setText(res_type2);
//
//        TextView type3 = (TextView) view.findViewById(R.id.res_type_3);
//        type3.setText(res_type3);
        addView(view);
    }

    @Override
    public void refreshView() {

    }
}
