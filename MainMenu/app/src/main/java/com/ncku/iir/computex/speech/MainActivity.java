/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ncku.iir.computex.speech;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.asus.robotframework.API.RobotAPI;
import com.ncku.iir.computex.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ncku.iir.computex.util.TextUtil;

/*
最上層的activity 負責初始化google speech service
之後的fragment都會在這個activity內做切換


 */
public class MainActivity extends AppCompatActivity {

    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private SpeechService mSpeechService;
    private SpeechReaction currentReaction;
    private VoiceRecorder mVoiceRecorder;
    private HashMap<String, HashMap<String, ArrayList<String>>> keywordTable;
    private String utterance;
    private RobotAPI api;

    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            if (mSpeechService != null) {
                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (mSpeechService != null) {
                mSpeechService.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }

    };
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            Log.d("gg1234","connected");
            mSpeechService = SpeechService.from(binder);
            MainActivity.this.mSpeechService.addListener(mSpeechServiceListener);
            changeFragment(new MainActivityFragment());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };
    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (!TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    Toast.makeText(MainActivity.this,"偵測到 :"+text,Toast.LENGTH_SHORT).show();
                                    if(text.contains("主畫面")){
                                        MainActivity.this.changeFragment(new MainActivityFragment());
                                    }
                                    else if(currentReaction != null) {
                                        currentReaction.onSentenceEnd(text);
                                    }
                                    /*
                                    if(currentReaction!=null) {
                                        Log.d("gg1234","final :"+text);
                                        currentReaction.onSentenceEnd(text);
                                        Log.d("gg1234","- - - - - - - - -");
                                    }else{
                                        Log.d("gg1234","final :"+"null");
                                    }
                                    */
                                    utterance  = "";
                                } else {
                                    utterance  =text;
                                    Log.d("gg1234","1");
                                }
                            }
                        });
                    }
                }
            };



    public void changeFragment(Fragment f){
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.main_container,f).commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);
        Global.ma = this;
        api = new RobotAPI(Global.ma);
        Global.api = api;
        keywordTable = TextUtil.readKeywordsTable(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("gg1234","start");
        // Prepare Cloud Speech API
        bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startVoiceRecorder();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
                showPermissionMessageDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    protected void onStop() {
        // Stop listening to voice
        stopVoiceRecorder();

        // Stop Cloud Speech API
        if( mSpeechService==null){
            return;
        }
        mSpeechService.removeListener(mSpeechServiceListener);
        unbindService(mServiceConnection);
        mSpeechService = null;
        Log.d("gg1234","stop");
        super.onStop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (permissions.length == 1 && grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecorder();
            } else {
                showPermissionMessageDialog();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    private void showPermissionMessageDialog() {
        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }



    public VoiceRecorder getVoiceRecorder() {
        return mVoiceRecorder;
    }


    public void setCurrentReaction(SpeechReaction reaction){
        Log.d("gg1234","setCurrentReaction");
        //Global.speak("切換");
        this.currentReaction = reaction;
    }


    public SpeechReaction getCurrentSpeechReaction() {
        return currentReaction;
    }

    public HashMap<String, HashMap<String, ArrayList<String>>> getKeywordTable() {
        return keywordTable;
    }
}
