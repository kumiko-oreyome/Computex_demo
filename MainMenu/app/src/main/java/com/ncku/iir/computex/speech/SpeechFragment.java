package com.ncku.iir.computex.speech;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;

/*
所有的fragment請繼承這個fragment,要跳轉到下一個fragment請call jumpNextFragment
 */
public abstract  class SpeechFragment extends Fragment {
    public  abstract SpeechReaction getSpeechReaction();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("gg1234","onAttach");
        Global.ma.setCurrentReaction(getSpeechReaction());
    }



    public void jumpNextFragment(Fragment f){
        Global.ma.changeFragment(f);
    }
}
