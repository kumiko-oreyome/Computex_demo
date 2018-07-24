package com.ncku.iir.computex;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WeatherFragment extends Fragment {

    private Activity activity;

    public Context getContext(){
        if(activity == null){
            return getActivity().getApplication().getApplicationContext();
        }
        return activity;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity = getActivity();
    }


    //    private RequestServer requestServer ;
    private TextInputLayout textInput;
    private ImageView iView;

    //main board initialize
    private int[] mainBoard_id = {R.id.location, R.id.temp, R.id.cond, R.id.background};
    private int[] bg_drawable_id = {R.drawable.bg_daysun,  R.drawable.bg_daycloud, R.drawable.bg_dayrain, R.drawable.bg_daythunder, R.drawable.bg_nightcloud, R.drawable.bg_nightrain, R.drawable.bg_nightthunder};

    //remind board initialize
    private int[] remindBoard_id = { R.id.remind_umbrella, R.id.remind_mask, R.id.remind_sunscreen, R.id.remind_jacket};
    private int[] remind_on_drawable_id = {R.drawable.remind_umbrella_on,  R.drawable.remind_mask_on, R.drawable.remind_sunscreen_on, R.drawable.remind_jacket_on};
    private int[] remind_off_drawable_id = {R.drawable.remind_umbrella_off,  R.drawable.remind_mask_off, R.drawable.remind_sunscreen_off, R.drawable.remind_jacket_off};


    //feature board initialize
    private int[] featureBoard_id = {R.id.featureHumidity, R.id.featurePressure, R.id.featureWind, R.id.featureSunTime, R.id.featureVisibility};

    //forecast board initialize
    private int[] forecasts_id = {R.id.forecast0, R.id.forecast1, R.id.forecast2, R.id.forecast3, R.id.forecast4, R.id.forecast5, R.id.forecast6, R.id.forecast7, R.id.forecast8, R.id.forecast9};
    private int[] cond_drawable_id = {R.drawable.cond_sun,  R.drawable.cond_suncloud, R.drawable.cond_cloud, R.drawable.cond_rain, R.drawable.cond_thunderrain, R.drawable.cond_wind};


    private static final String TAG = "WeatherMainActivity";
    private JSONObject Weather;
    public static String serverResponse;

    Date date = new Date();
    Calendar rightNow = Calendar.getInstance();
    DateFormat dateFormat = new SimpleDateFormat("MM/dd");


    private Map<String, String> unitMaps = new HashMap<>();
    private List<Set<String>> bgCondList;
    private List<Set<String>> forecastCondList;
    private List<Set<String>> remindCondList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_activity_main, container, false);
        textInput = view.findViewById(R.id.textInput);
        iView = view.findViewById(R.id.imageView);
        return inflater.inflate(R.layout.weather_activity_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        requestServer = new RequestServer(WeatherMainActivity.this) ;
//        requestServer = new RequestServer(getContext()) ;
//        requestServer.send_text("台北天氣"); // 初始化台北天氣
        rightNow.setTime(date);


//        setContentView(R.layout.weather_activity_main);

//        textInput = findViewById(R.id.textInput);
//        iView = findViewById(R.id.imageView);

        // units for data
        unitMaps.put("temp", getResources().getString(R.string.unit_temp));
        unitMaps.put("humidity", getResources().getString(R.string.unit_humidity));
        unitMaps.put("pressure", getResources().getString(R.string.unit_pressure));
        unitMaps.put("wind", getResources().getString(R.string.unit_wind));
        unitMaps.put("visibility", getResources().getString(R.string.unit_visibility));

        // background condition
        Set<String> day_sun = new HashSet<>(Arrays.asList(new String[]{"32", "34", "36"})); //日間, 天晴
        Set<String> day_cloud = new HashSet<>(Arrays.asList(new String[]{"19", "20", "21", "22", "23", "24", "25", "26", "28", "30", "44"})); // 天氣較不明朗, 沒太陽, 塵, 霧
        Set<String> day_rain = new HashSet<>(Arrays.asList(new String[]{"5", "6", "7", "8", "9", "10", "13", "14", "15", "16", "17", "18", "35", "41", "42", "43", "46"})); //小雨, 雪
        Set<String> day_thunder = new HashSet<>(Arrays.asList(new String[]{"0", "1", "2", "3", "37", "38", "39"}));// 小雨, 雨雪
        Set<String> night_cloud = new HashSet<>(Arrays.asList(new String[]{"27", "29", "31", "33"}));
        Set<String> night_rain = new HashSet<>(Arrays.asList(new String[]{"11", "12", "40"})); //暴雨
        Set<String> night_thunder = new HashSet<>(Arrays.asList(new String[]{"4", "45", "47"})); // 強雷陣雨

        bgCondList = Arrays.asList(day_sun, day_cloud, day_thunder, day_rain, night_cloud, night_rain, night_thunder);


        // forecast icon condition
        Set<String> sun = new HashSet<>(Arrays.asList(new String[]{"31", "32", "33", "34", "36"}));
        Set<String> sun_cloud = new HashSet<>(Arrays.asList(new String[]{"27", "28", "29", "30"}));
        Set<String> cloud = new HashSet<>(Arrays.asList(new String[]{"19", "20", "21", "22", "26", "44"}));
        Set<String> rain = new HashSet<>(Arrays.asList(new String[]{"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "35", "41", "42", "43", "46"}));
        Set<String> thunder_rain = new HashSet<>(Arrays.asList(new String[]{"1", "2", "3", "4", "37", "38", "39", "40", "45", "47"})); //風暴, 雷陣雨
        Set<String> wind = new HashSet<>(Arrays.asList(new String[]{"0", "23", "24", "25"})); //龍捲風, 有風, 冷

        forecastCondList = Arrays.asList(sun, sun_cloud, cloud, rain, thunder_rain, wind);


        // remind icon condition
        Set<String> umbrella = new HashSet<>(Arrays.asList(new String[]{"3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "35", "37", "38", "39", "40", "45", "47"}));
        Set<String> mask = new HashSet<>(Arrays.asList(new String[]{"19", "22"}));
        Set<String> sunscreen = new HashSet<>(Arrays.asList(new String[]{"28", "30", "32", "34", "36"}));
        Set<String> jacket = new HashSet<>(Arrays.asList(new String[]{"13", "14", "15", "16", "17", "18", "25", "41", "42", "43", "46"}));
        remindCondList = Arrays.asList(umbrella, mask, sunscreen, jacket);

    }

}

