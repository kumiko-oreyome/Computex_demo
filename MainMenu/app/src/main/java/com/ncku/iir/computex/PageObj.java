package com.ncku.iir.computex;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by veron on 2018/5/30.
 */

public class PageObj {
    public String title;
    public String brief_info;
    public String detail_info;
    public String rating;
    public String[] types;
    public String img_str = "https://n21.daknoadmin.com/site_data/c21frontier/editor_assets/BLACK%20OUTLINE%20LOC.jpg";
    public String qrcode_link;

    public PageObj(String title, String brief_info, String detail_info, String[] types, String img_str, String rating, String link, String domain) throws UnsupportedEncodingException {
//        Log.d("PageObj", "creating page obj");
        this.title = title;
        this.brief_info = brief_info;
        this.detail_info = detail_info;
        this.types = types;
        if (!img_str.equals("")) {
            this.img_str = img_str;
//             if (img_str.contains("maps.googleapis.com")) {
//                 // change key
//                 //https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CmRZAAAAzyl2YaKPdxI-jQObPTDOQU9GFGqBxBe7qQ2VgNQTd3_M_Qiz4E_H2TldlhA1k3WNI4LGlrM1Rs5g5I1srJvcvbLvSnQbC2SGVht3qsKN8FEZhhtCrR5iNP-vthV0P627EhBzE1o4YANLGo94uM4Q-GBcGhTMKVyZTUqX93bGqTPa_yJXKWdhMw&key=AIzaSyDVb_2HU1Yq5cclrqxB8MoPYVkVi_HJGUs
//             }
        }
        this.rating = rating;

        if(domain.equals("activity")) {
            // url 製作
            String start = URLEncoder.encode("台北世界貿易中心展覽一館", "utf-8");
            String end = URLEncoder.encode(this.title, "utf-8");
            this.qrcode_link = String.format("https://www.google.com.tw/maps/dir/%s/%s", start, end);
        } else {
            this.qrcode_link = link;
        }
//        Log.d("PageObj", "link="+this.qrcode_link);


    }

    public String getTitle() {return this.title;}
    public String getBriefInfo() {return this.brief_info;}
    public String getRating() {return this.rating;}
    public String getDetailInfo() {return this.detail_info;}
    public String[] getTypes() {return this.types;}
    public String getImgStr() {return this.img_str;}
    public String getQrcodeLink() {return this.qrcode_link;}

}