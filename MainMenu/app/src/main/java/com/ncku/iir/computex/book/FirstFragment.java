package com.ncku.iir.computex.book;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.asus.robotframework.API.RobotAPI;
import com.bumptech.glide.Glide;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends SpeechFragment {
    private static final String TAG = "FirstActivity";
    private static final String ARG_MODE = "mode";

    private String mode;
    private ImageButton homeBtn;
    private RobotAPI api;


    public FirstFragment() {
        // Required empty public constructor
    }

    public static FirstFragment newInstance(Bundle args) {
        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public static FirstFragment newInstance(String mode) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE,mode);
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
        View view = inflater.inflate(R.layout.activity_first, container, false);

        homeBtn = view.findViewById(R.id.homeButton1);


        api = Global.api;
        api.robot.speak("先選擇一本你喜歡的書");

        Log.d(TAG, mode);

        // collect book info boxes
        BookInfoBox bookInfoBox1 = new BookInfoBox(
                (LinearLayout) view.findViewById(R.id.bookInfoBox1),
                (ImageView) view.findViewById(R.id.bookImg1),
                (TextView) view.findViewById(R.id.bookName1),
                (TextView) view.findViewById(R.id.bookRating1)
        );
        BookInfoBox bookInfoBox2 = new BookInfoBox(
                (LinearLayout) view.findViewById(R.id.bookInfoBox2),
                (ImageView) view.findViewById(R.id.bookImg2),
                (TextView) view.findViewById(R.id.bookName2),
                (TextView) view.findViewById(R.id.bookRating2)
        );
        BookInfoBox bookInfoBox3 = new BookInfoBox(
                (LinearLayout) view.findViewById(R.id.bookInfoBox3),
                (ImageView) view.findViewById(R.id.bookImg3),
                (TextView) view.findViewById(R.id.bookName3),
                (TextView) view.findViewById(R.id.bookRating3)
        );

        setBookInfoBoxes(bookInfoBox1, bookInfoBox2, bookInfoBox3);

        homeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

                Intent intent = new Intent();
               // intent.setClass(FirstActivity.this, MainActivity.class);
                //切換Activity
                startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {
//                Global.speak("書啦");
            }
        };
    }

    protected void setBookInfoBoxes(final BookInfoBox bookInfoBox1, final BookInfoBox bookInfoBox2, final BookInfoBox bookInfoBox3) {
        final RequestQueue mQueue = Volley.newRequestQueue(Global.ma);

        String url = "http://140.116.247.169/get3Books.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onSuccessResponse: " + response.toString());
                        try {
                            bookInfoBox1.setBookInfoBox(response.getJSONObject(0));
                            bookInfoBox2.setBookInfoBox(response.getJSONObject(1));
                            bookInfoBox3.setBookInfoBox(response.getJSONObject(2));
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    }
                }
        );
        mQueue.add(jsonArrayRequest);
    }

    private class BookInfoBox {
        protected LinearLayout bookInfobox;
        protected ImageView bookImg;
        protected TextView bookName;
        protected TextView bookRating;

        public BookInfoBox(LinearLayout bookInfobox, ImageView bookImg, TextView bookName, TextView bookRating) {
            this.bookInfobox = bookInfobox;
            this.bookImg = bookImg;
            this.bookName = bookName;
            this.bookRating = bookRating;
        }

        public void setBookInfoBox(final JSONObject bookInfo) {
            try {
                Glide.with(Global.ma).load(bookInfo.getString("bookImgUrl")).into(bookImg);
                bookName.setText(bookInfo.getString("bookName"));
                bookRating.setText(bookInfo.getString("bookRating"));
            } catch (JSONException e) {
                Log.d(TAG, "setBookInfoBox: " + e.toString());
            }

            bookInfobox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    api.robot.stopSpeak();
                    try {
                        toSecondActivity(bookInfo.getString("bookId"));
                    } catch (JSONException e) {
                        Log.d(TAG, "onClick: " + e.toString());
                    }

                }
            });
        }

        protected void toSecondActivity(String bookId) {
           // intent.setClass(Glo, SecondActivity.class);
            api.robot.speak("選擇你喜歡的特色，我可以推薦你更適合的書");
            jumpNextFragment(SecondFragment.newInstance(mode,bookId));
        }
    }


}
