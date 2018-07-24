package com.ncku.iir.computex.speech;


import android.content.Context;

import com.asus.robotframework.API.RobotAPI;

/*
這個類別可以用全域變數的方式拿到mainactivity,拿到之後可以做一些operation
 */
public class Global {
    //public static MainActivity ma ;
    public static MainActivity ma ;
    public static RobotAPI api;
    public static void speak(String text){
        api.robot.speak(text);
    }
    //public
}
