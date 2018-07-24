package com.ncku.iir.computex.restaurant;

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
import com.ncku.iir.computex.PageView;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.poi.PoiQRCodeFragment;
import com.ncku.iir.computex.speech.Global;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class NewPageView extends PageView {
    public NewPageView(Context context, String resName, final ArrayList<StoreComment> commentList, String res_type1, String imgurl, final String res_key, final String address, final String mode) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.page_content, null);

        String url = "http://140.116.247.172:8888";
        final String USERID = "104208676126208324781";
        final Request request = new Request(context,url) ;
        final UpdateCallback callback = new UpdateCallback();


        ImageView img = (ImageView) view.findViewById(R.id.res_image);
        if ( !imgurl.isEmpty() ) {
            Picasso.with(context).load(imgurl).into(img);
        }


        TextView res_textView = (TextView) view.findViewById(R.id.res_textView);
        res_textView.setText(resName);

        TextView add_textView = (TextView) view.findViewById(R.id.add_textView);
        add_textView.setText(address + "\n " + commentList.get(0).getText());


        TextView type1 = (TextView) view.findViewById(R.id.res_type_1);
        type1.setText(res_type1);

        TextView rate = (TextView) view.findViewById(R.id.rating);
        rate.setText(""+commentList.get(0).getRating());
        ImageView star = (ImageView) view.findViewById(R.id.star);


        final Button like_button = (Button) view.findViewById(R.id.like_button);
        final Button dislike_button = (Button) view.findViewById(R.id.dislike_button);
        final Button qr_button = (Button) view.findViewById(R.id.QR_button) ;

        like_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.api.robot.stopSpeak() ;
                like_button.setVisibility(GONE);
                dislike_button.setVisibility(GONE);
                request.updateUserPreference(USERID,res_key,"Y", callback);
                Global.api.robot.speak("好的，已更新您的喜好") ;
            }
        });

        dislike_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.api.robot.stopSpeak() ;
                dislike_button.setVisibility(GONE);
                like_button.setVisibility(GONE);
                qr_button.setVisibility(GONE);
                request.updateUserPreference(USERID,res_key,"N", callback);
                Global.api.robot.speak("好的，已更新您的喜好") ;

                goToPreviousPage(mode);
            }
        });

        qr_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.api.robot.stopSpeak() ;
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

                Bundle bundle = new Bundle();
                bundle.putString("domain", "restaurant");
                bundle.putString("mode", mode);
                bundle.putString("link", qrcode_link);
                Global.ma.changeFragment(PoiQRCodeFragment.newInstance(bundle));
            }
        });

        addView(view);
    }

    public void goToPreviousPage(String mode) {
        Global.ma.changeFragment(BriefInfoFragment.newInstance(mode,""));
    }

    @Override
    public void refreshView() {

    }
}
