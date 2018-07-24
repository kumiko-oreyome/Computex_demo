package com.ncku.iir.computex.util;

import android.content.Context;

import com.ncku.iir.computex.MainActivity;
import com.ncku.iir.computex.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextUtil {

    public static boolean inKeywords(String text,String... keywords){
        for(String keyword : keywords){
            if(text.contains(keyword)){
                return true;
            }
        }
        return  false;
    }

    public static boolean inKeywords(String text, ArrayList<String> keywords){
        if(keywords==null){
            return  false;
        }
        for(String keyword : keywords){
            if(text.contains(keyword)){
                return true;
            }
        }
        return  false;
    }
    //stupid code start

    public static HashMap<String, HashMap<String, ArrayList<String>>> readKeywordsTable(Context c){


        HashMap<String, HashMap<String, ArrayList<String>>> keywordTable = new HashMap<String, HashMap<String, ArrayList<String>>>();
        InputStream csvInput = c.getResources().openRawResource(R.raw.keywords);
        BufferedReader reader = new BufferedReader(new InputStreamReader(csvInput));
        try{
            String word;
            while ((word = reader.readLine()) != null){
                String[] colContain = word.split(",");
                String[] opt1 = colContain[2].split(";");
                String[] opt2 = colContain[4].split(";");
                HashMap<String, ArrayList<String>> innerHash = new HashMap<String, ArrayList<String>>();
                ArrayList<String> opt1Keys = new ArrayList<String>();
                ArrayList<String> opt2Keys = new ArrayList<String>();
                for(String k : opt1){
                    opt1Keys.add(k);}
                for(String k : opt2) {
                    opt2Keys.add(k);
                }
                innerHash.put(colContain[1], opt1Keys);
                innerHash.put(colContain[3], opt2Keys);
                keywordTable.put(colContain[0], innerHash);
            }
        }catch (Exception e){
        }
        return keywordTable;
    }

    //stupid code end
}
