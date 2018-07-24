package com.ncku.iir.computex;

import android.app.Application;

import com.asus.robotframework.API.RobotAPI;

public class GlobalService extends Application {

    public static SpeechService gSpeechService = null ;
    public static RobotAPI gRobotApi = null ;

    @Override
    public void onCreate() {

        super.onCreate();
    }

}
