package com.ncku.iir.computex;

import android.Manifest;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;

public class RequestActivity extends AppCompatActivity {

    public RequestServer requestServer ;

//    public RobotAPI api;

//    public SpeechService mSpeechService;
//
//    public VoiceRecorder mVoiceRecorder;

//    public VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {
//
//        @Override
//        public void onVoiceStart() {
//            if (mSpeechService != null) {
//                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
//            }
//        }
//
//        @Override
//        public void onVoice(byte[] data, int size) {
//            if (mSpeechService != null) {
//                mSpeechService.recognize(data, size);
//            }
//        }
//
//        @Override
//        public void onVoiceEnd() {
//            if (mSpeechService != null) {
//                mSpeechService.finishRecognizing();
//            }
//        }
//
//    };
//
//    public SpeechService.Listener mSpeechServiceListener = new SpeechService.Listener() {
//        @Override
//        public void onSpeechRecognized(String text, boolean isFinal) {
//
//            if (isFinal) {
//                mVoiceRecorder.dismiss();
//            }
//            if (text !=null) {
////                Toast.makeText(RequestActivity.this, "on initial mSpeechServiceListener: "+text, Toast.LENGTH_LONG).show();
//                Log.d("on initial : ", text);
//            }
//        }
//    };
//
//    public ServiceConnection mServiceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder binder) {
//            mSpeechService = SpeechService.from(binder);
//            Log.i("yoyo", "before add listener");
//            mSpeechService.addListener(mSpeechServiceListener);
//            Log.i("yoyo", "after add listener");
////            mStatus.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            mSpeechService = null;
//        }
//
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestServer = new RequestServer(RequestActivity.this) ;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Prepare Cloud Speech API
        // bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);
        // Start listening to voices

        //startVoiceRecorder();

    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void onGetMessage(String response) {
    }

//    private void startVoiceRecorder() {
//        if (mVoiceRecorder != null) {
//            mVoiceRecorder.stop();
//        }
//        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
//        mVoiceRecorder.start();
//    }
}
