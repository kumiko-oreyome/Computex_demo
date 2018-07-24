package com.ncku.iir.computex.book;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.ncku.iir.computex.MainActivity;
import com.ncku.iir.computex.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FirstActivity extends AppCompatActivity {

    private static final String TAG = "FirstActivity";
    private String mode;
    private ImageButton homeBtn;
    private RobotAPI api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Bundle bundle = this.getIntent().getExtras();
        mode = bundle.getString("mode");
        homeBtn = findViewById(R.id.homeButton1);
        api = new RobotAPI(getApplicationContext());
        api.robot.stopSpeak();
        api.robot.speak("先選擇一本你喜歡的書");

        Log.d(TAG, mode);

        // collect book info boxes
        BookInfoBox bookInfoBox1 = new BookInfoBox(
                (LinearLayout) findViewById(R.id.bookInfoBox1),
                (ImageView) findViewById(R.id.bookImg1),
                (TextView) findViewById(R.id.bookName1),
                (TextView) findViewById(R.id.bookRating1)
        );
        BookInfoBox bookInfoBox2 = new BookInfoBox(
                (LinearLayout) findViewById(R.id.bookInfoBox2),
                (ImageView) findViewById(R.id.bookImg2),
                (TextView) findViewById(R.id.bookName2),
                (TextView) findViewById(R.id.bookRating2)
        );
        BookInfoBox bookInfoBox3 = new BookInfoBox(
                (LinearLayout) findViewById(R.id.bookInfoBox3),
                (ImageView) findViewById(R.id.bookImg3),
                (TextView) findViewById(R.id.bookName3),
                (TextView) findViewById(R.id.bookRating3)
        );

        setBookInfoBoxes(bookInfoBox1, bookInfoBox2, bookInfoBox3);

        homeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

                Intent intent = new Intent();
                intent.setClass(FirstActivity.this, MainActivity.class);
                //切換Activity
                startActivity(intent);
            }
        });
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
                Glide.with(FirstActivity.this).load(bookInfo.getString("bookImgUrl")).into(bookImg);
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
            Intent intent = new Intent();
            intent.setClass(FirstActivity.this, SecondActivity.class);
            api.robot.speak("選擇你喜歡的特色，我可以推薦你更適合的書");

            Bundle bundle = new Bundle();
            bundle.putString("bookId", bookId);
            bundle.putString("mode", mode);

            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


    protected void setBookInfoBoxes(final BookInfoBox bookInfoBox1, final BookInfoBox bookInfoBox2, final BookInfoBox bookInfoBox3) {
        final RequestQueue mQueue = Volley.newRequestQueue(this);

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
}
