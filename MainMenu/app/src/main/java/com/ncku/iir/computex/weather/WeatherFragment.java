package com.ncku.iir.computex.weather;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.ncku.iir.computex.core.IRequest;
import com.ncku.iir.computex.speech.MainActivityFragment;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.core.RequestServer;
import com.ncku.iir.computex.SingleFeatureBoardLinearLayout;
import com.ncku.iir.computex.SingleScrollLinearLayout;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;

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
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class WeatherFragment extends SpeechFragment implements IRequest {

    private static final String ARG_MODE = "mode";

    private View view;
    private RequestServer requestServer ;
    private TextInputLayout textInput;
    private ImageView iView;
    private Timer timer;
    private String mode;
    private TextView exitBtn;
    private boolean jump;
    private RobotAPI api;

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
    private List<Set<String>> remindCondTextList;



    public static WeatherFragment newInstance(String param1) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getString(ARG_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.weather_activity_main, container, false);
        this.view = view;
        textInput =  view.findViewById(R.id.textInput);
        iView = view.findViewById(R.id.imageView);
        exitBtn = view.findViewById(R.id.temp);



        requestServer = new RequestServer(this);

        jump = true;
        if (mode.equals("Demo") && (jump = true)) {
            //Switch to Main After 10 Sec
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    WeatherFragment.this.jumpNextFragment(new MainActivityFragment());
                }
            }, 20000);
        }

        requestServer.send_text("台南天氣"); // 初始化台北天氣
        //serverResponse = requestServer.getResult();
        //getAllFeatures(serverResponse);

        rightNow.setTime(date);
       // this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        api = Global.api;



        textInput =  view.findViewById(R.id.textInput);
        iView = view.findViewById(R.id.imageView);
        exitBtn = view.findViewById(R.id.temp);


        // units for data
        unitMaps.put("temp", getResources().getString(R.string.unit_temp));
        unitMaps.put("humidity", getResources().getString(R.string.unit_humidity));
        unitMaps.put("pressure", getResources().getString(R.string.unit_pressure));
        unitMaps.put("wind", getResources().getString(R.string.unit_wind));
        unitMaps.put("visibility", getResources().getString(R.string.unit_visibility));

        // background condition
        Set<String> day_sun = new HashSet<>(Arrays.asList( new String[]{"32", "34", "36"})); //日間, 天晴
        Set<String> day_cloud = new HashSet<>(Arrays.asList( new String[]{"19", "20", "21", "22", "23", "24", "25", "26", "28", "30", "44"})); // 天氣較不明朗, 沒太陽, 塵, 霧
        Set<String> day_rain = new HashSet<>(Arrays.asList( new String[]{"5", "6", "7", "8", "9", "10", "13", "14", "15", "16", "17", "18", "35", "41", "42", "43", "46"})); //小雨, 雪
        Set<String> day_thunder =  new HashSet<>(Arrays.asList( new String[]{"0", "1", "2", "3", "37", "38", "39"}));// 小雨, 雨雪
        Set<String> night_cloud = new HashSet<>(Arrays.asList( new String[]{"27", "29", "31", "33"}));
        Set<String> night_rain = new HashSet<>(Arrays.asList( new String[]{"11", "12", "40"})); //暴雨
        Set<String> night_thunder = new HashSet<>(Arrays.asList( new String[]{"4", "45", "47"})); // 強雷陣雨

        bgCondList = Arrays.asList(day_sun, day_cloud, day_thunder, day_rain, night_cloud, night_rain, night_thunder);


        // forecast icon condition
        Set<String> sun = new HashSet<>(Arrays.asList(new String[] {"31", "32", "33", "34", "36"}));
        Set<String> sun_cloud = new HashSet<>(Arrays.asList(new String[] {"27", "28", "29", "30"}));
        Set<String> cloud = new HashSet<>(Arrays.asList(new String[] {"19", "20", "21", "22", "26", "44"}));
        Set<String> rain = new HashSet<>(Arrays.asList(new String[] {"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "35", "41", "42", "43", "46"}));
        Set<String> thunder_rain = new HashSet<>(Arrays.asList(new String[] {"1", "2", "3", "4", "37", "38", "39", "40", "45", "47"})); //風暴, 雷陣雨
        Set<String> wind = new HashSet<>(Arrays.asList(new String[] {"0", "23", "24", "25"})); //龍捲風, 有風, 冷

        forecastCondList = Arrays.asList(sun, sun_cloud, cloud, rain, thunder_rain, wind);


        // remind icon condition
        Set<String> umbrella = new HashSet<>(Arrays.asList(new String[] {"3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "35","37","38","39","40","45","47"}));
        Set<String> mask = new HashSet<>(Arrays.asList(new String[] {"19","22"}));
        Set<String> sunscreen = new HashSet<>(Arrays.asList(new String[] {"28", "30", "32", "34", "36"}));
        Set<String> jacket = new HashSet<>(Arrays.asList(new String[] {"13","14","15","16","17","18","25","41","42","43","46"}));

        Set<String> umbrellaText = new HashSet<>(Arrays.asList(new String[] {"今天可能會下雨，記得帶雨傘喔"}));
        Set<String> maskText = new HashSet<>(Arrays.asList(new String[] {"外面空氣不好，戴個口罩再出門吧","今天PM二點五濃度高，出門記得戴口罩喔"}));
        Set<String> sunscreenText = new HashSet<>(Arrays.asList(new String[] {"今天太陽很大，可以擦個防曬抵擋紫外線", "今天很熱，小心外頭的太陽，你可以擦個防曬"}));
        Set<String> jacketText = new HashSet<>(Arrays.asList(new String[] {"天氣好冷，多加幾件衣服","穿個外套吧，不要著涼了"}));
        remindCondList = Arrays.asList(umbrella, mask, sunscreen, jacket);
        remindCondTextList = Arrays.asList(umbrellaText, maskText, sunscreenText, jacketText);

        exitBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                jump = false;

            }
        });
        // backend listener
        initListeners(view);


        return view;
    }


    private void initListeners(final View view){
        iView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String sendText = textInput.getEditText().getText().toString();
                if (sendText.equals("")) {
                    Log.d(TAG, "Cannot read data, default: 台南天氣");
                    sendText = "台南天氣";
                }
                serverResponse = requestServer.send_text(sendText);
                serverResponse = requestServer.getResult();
                getAllFeatures(serverResponse);
            }
        });
    }

    private void getAllFeatures(String weatherText) {
        Log.d(TAG,"CALL getAllFeatures");
        Calendar rightNow = Calendar.getInstance();
        try {
            Weather = new JSONObject(weatherText);
            JSONObject ReplyText = Weather.getJSONObject("reply_text");
            JSONObject RealtimeTmp = ReplyText.getJSONObject("realtime");
            JSONObject Realtime = RealtimeTmp.getJSONObject("realtime");


            /* -- MAIN BOARD -- */
            String str_location = Realtime.getString("location");
            String str_temperature = Realtime.getString("temp") + unitMaps.get("temp");
            String str_condition = Realtime.getString("cond");
            String str_code = Realtime.getString("code");

            setRealTimeFeature(mainBoard_id[0], str_location, view);
            setRealTimeFeature(mainBoard_id[1], str_temperature, view);
            setRealTimeFeature(mainBoard_id[2], str_condition, view);
            setMainBoardBackground(mainBoard_id[3], str_code, view);


            // remind board
            setRemindIcons(str_code, view);

            /* -- End of MAIN BOARD -- */


            /* -- FEATURE BOARD -- */
            String str_humidity = Realtime.getString("humidity") + unitMaps.get("humidity"); // 濕度
            Double pressure = Realtime.getDouble("pressure") / 33.86;
            String str_pressure = String.format("%.1f", pressure) +' '+ unitMaps.get("pressure"); // 氣壓
            String str_wind = Realtime.getString("speed") + unitMaps.get("wind") + ", " + getWindDirection(Realtime.getDouble("direction")); // 風速風向
            String str_sunTime = Realtime.getString("sunrise") + "/" + Realtime.getString("sunset"); //日出日落
            String str_visibility = getVisibilityScale(Realtime.getString("visibility")); // 能見度

            String[] featureInfoList = {str_humidity, str_pressure, str_wind, str_sunTime, str_visibility};
            setFeatureBoards(featureInfoList, view);

            /* -- End of FEATURE BOARD -- */


            /* -- FORECAST BOARD -- */
            JSONArray TenDay = ReplyText.getJSONArray("tenday");
            List<JSONArray> Days = new ArrayList<>();
            for (int i=0; i<TenDay.length(); i++) {
                Days.add( TenDay.getJSONArray(i) );

            }

            for (int i=0; i<Days.size(); i++) {
                //日期加10天
                Log.d(TAG, Days.get(i).toString());

                // data example : [{low_temp}, {high_temp}, {cond_code}]
                date =rightNow.getTime();
                String dateText =  dateFormat.format(date);
                String temp = Days.get(i).get(1).toString();
                String cond = Days.get(i).get(2).toString();

                setForecastBoard(dateText, cond, temp, i, view);

                rightNow.add(Calendar.DAY_OF_YEAR,1);
            }

            /* -- End of FORECAST BOARD -- */

            //Speak
            setRemindText(str_code);


        }catch(JSONException e){
            Log.d(TAG,"FUCK JSON");
        }catch(NullPointerException e){
            Log.d(TAG,"FUCK");
        }

    }

    private String getVisibilityScale(String data) {
        Log.d(TAG, "GetVisibilityScale " + data);
        double visibility = Double.valueOf(data);
        String scale = "Wrong";
        String[] scales = {"佳", "一般", "差", "極差"};
        double[] min_values = {15, 10, 1, 0};

        for (int i =0; i<min_values.length; i++) {
            if (visibility >= min_values[i]) {
                scale = scales[i];
                break;
            }
        }

        return scale;
    }

    private String getWindDirection(double direction) {
        Log.d(TAG, "GetWindDirection " + direction);
        String scale = "方位錯誤";
        String[] scales = {"北", "東", "南", "西", "北"};
        double[] values = {0, 90, 180, 270, 360};
        double scale_unit = 90/8;
        //String[] change_scales =

        for (int i = 0; i< values.length; i++) {
            if (direction == values[i]) {
                scale = scales[i];
                break;
            }
            else if (direction < values[i] && i > 0){
                //Log.d("In get direction", "i = "+ i);
                double sub_direction = direction - values[i-1];

                String middle_scale = (scales[i].equals("北") || scales[i].equals("南"))? scales[i]+scales[i-1] : scales[i-1]+scales[i]; //e.g.: WN --> NW
                //Log.d("In get direction", "original scales = "+ scales[i-1]+ scales[i]+ "after: middle_scale = "+ middle_scale);
                String chinese_middle_scale = middle_scale.substring(1,2) +middle_scale.substring(0,1);
                String[] sub_scales = {scales[i-1],  scales[i-1]+middle_scale, chinese_middle_scale, scales[i]+middle_scale, scales[i]};

                int j = (int) sub_direction /(90/4);
                int dev = (sub_direction % (scale_unit*2) < (scale_unit/2))? 0: 1; //方向偏位
                scale = sub_scales[j+dev];


                break;
            }
            else continue;
        }
        Log.d("After get direction", "result = "+scale);
        return scale;
    }

    private int getForecastIconId(String cond) {
        int drawable_id = cond_drawable_id[0];
        //Log.d(TAG, ">> getting forecast icon, cond="+ cond);

        for (int c=0; c<forecastCondList.size(); c++ ) {
            if (forecastCondList.get(c).contains(cond)) {
                drawable_id = cond_drawable_id[c];
                break;
            }
        }
        return drawable_id;
    }


    private void setForecastBoard(String date_text, String cond, String temp, int i, View view) {
        Log.d(TAG, "[setting forecastBoard " + i);
        int icon_id = getForecastIconId(cond);

        SingleScrollLinearLayout ll_forecast = view.findViewById(forecasts_id[i]);
        ll_forecast.setDate(date_text);
        ll_forecast.setIcon(icon_id);
        ll_forecast.setTemp(temp);

    }

    private void setRealTimeFeature(int i, String text, View view) {
        TextView feature = view.findViewById(i);
        Log.d(TAG, ">> setting main Board " + text);

        feature.setText(text);
    }

    private void setMainBoardBackground(int i, String cond, View view) {
        LinearLayout bg = view.findViewById(i);
        int drawable_id = bg_drawable_id[0];
        Log.d(TAG, ">> setting Background , cond="+ cond);

        for (int c=0;c<bgCondList.size();c++ ) {
            if (bgCondList.get(c).contains(cond)) {
                drawable_id = bg_drawable_id[c];
                break;
            }
        }
        bg.setBackgroundResource(drawable_id);
    }

    private void setRemindIcons(String cond, View view) {
        for (int i=0; i<remindBoard_id.length; i++) {     //Umbrella Mask Sunscreen Jacket
            ImageView Icon = view.findViewById(remindBoard_id[i]);
            Log.d(TAG, ">> setting Icon :" + i);

            if(remindCondList.get(i).contains(cond)) {
                Icon.setImageResource(remind_on_drawable_id[i]);
            }
            else {
                Icon.setImageResource(remind_off_drawable_id[i]);
            }

            if (i==1) {
                Icon.setImageResource(remind_on_drawable_id[i]);
            }
        }

    }

    private void setRemindText (String cond) {
        for (int i=0; i<remindBoard_id.length; i++) {     //Umbrella Mask Sunscreen Jacket

            if(remindCondList.get(i).contains(cond)) {
                speakCondition(remindCondTextList.get(i));
            }
            else {

            }

            if (i==1) {
                speakCondition(remindCondTextList.get(i));
            }

        }

    }

    private void speakCondition(Set<String> remindText){
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(remindText.size());
        int i = 0;
        for(String str: remindText){
            if (i == index){
                Log.d(TAG, ">> speak Remind Text" + str);
                api.robot.speak(str) ;
            }
            i++;
        }

    }

    private void setFeatureBoards( String[] text_array, View view) {
        for (int i =0;i<featureBoard_id.length;i++) {
            String text = text_array[i];
            SingleFeatureBoardLinearLayout featureBoard = view.findViewById(featureBoard_id[i]);

            Log.d(TAG, ">> setting feature Board " + i);
            featureBoard.setDataText(text);
        }

    }

    @Override
    public void onGetMessage(String response) {
        Log.d(TAG,"onGetMessage");
        Log.d(TAG,response);
        Log.d(TAG,"- - - - - - - - - - - - -");
        getAllFeatures(response.toString());
    }

    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {

                if (text.equals("")) {
                    Log.d(TAG, "Cannot read data, default: 台南天氣");
                    text = "台南天氣";
                }
                serverResponse = requestServer.send_text(text);
                serverResponse = requestServer.getResult();
//                getAllFeatures(serverResponse);
            }
        };
    }
}
