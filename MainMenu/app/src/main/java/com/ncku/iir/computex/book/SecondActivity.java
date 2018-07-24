package com.ncku.iir.computex.book;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.asus.robotframework.API.RobotAPI;
import com.bumptech.glide.Glide;
import com.ncku.iir.computex.MainActivity;
import com.ncku.iir.computex.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";
    private String mode;
    private ImageButton homeBtn;
    private RobotAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        api = new RobotAPI(getApplicationContext());




        Bundle bundle = this.getIntent().getExtras();

        mode = bundle.getString("mode");
        String bookId = bundle.getString("bookId");

        Log.d(TAG, mode);

        Button exitBtn = (Button) findViewById(R.id.exitBtn);
        homeBtn = findViewById(R.id.homeButton2);

        BookInteraction book = new BookInteraction(
                (ImageView) findViewById(R.id.bookImg),
                (TextView) findViewById(R.id.bookRating),
                (TextView) findViewById(R.id.bookName),
                (TextView) findViewById(R.id.bookContent),
                (TextView) findViewById(R.id.bookType),
                (Button) findViewById(R.id.commentBtn1),
                (Button) findViewById(R.id.commentBtn2),
                (Button) findViewById(R.id.commentBtn3),
                (Button) findViewById(R.id.buyBtn)
        );

        setBook(book, bookId);

        homeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

                Intent intent = new Intent();
                intent.setClass(SecondActivity.this, MainActivity.class);
                //切換Activity
                startActivity(intent);
            }
        });

        exitBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {

                Intent intent = new Intent();

                if(mode.equals("demo")) {
                   //intent.setClass(SecondActivity.this, WeatherMainActivity.class);
                }else{
                    intent.setClass(SecondActivity.this, MainActivity.class);
                }

                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("mode", mode);

                //將Bundle物件assign給intent
                intent.putExtras(bundle);
                //切換Activity
                startActivity(intent);

            }
        });
    }

    private class BookInteraction {
        ImageView bookImg;
        TextView bookRating;
        TextView bookName;
        TextView bookContent;
        TextView bookType;
        Button commentBtn1;
        Button commentBtn2;
        Button commentBtn3;
        Button buyBtn;

        public BookInteraction(ImageView bookImg, TextView bookRating, TextView bookName, TextView bookContent, TextView bookType,
                               Button commentBtn1, Button commentBtn2, Button commentBtn3, Button buyBtn) {
            this.bookImg = bookImg;
            this.bookRating = bookRating;
            this.bookName = bookName;
            this.bookContent = bookContent;
            this.bookType = bookType;
            this.commentBtn1 = commentBtn1;
            this.commentBtn2 = commentBtn2;
            this.commentBtn3 = commentBtn3;
            this.buyBtn = buyBtn;
        }

        public void setBookInteraction(final JSONObject bookInfo){
            try {
                Glide.with(SecondActivity.this).load(bookInfo.getString("bookImgUrl")).into(bookImg);
                bookRating.setText(bookInfo.getString("bookRating"));
                bookName.setText(bookInfo.getString("bookName"));
                bookContent.setText(bookInfo.getString("bookContent"));
                bookType.setText(bookInfo.getString("bookType"));

                setBuyBtn(buyBtn, bookInfo.getString("bookId"));
            } catch (JSONException e) {
                Log.d(TAG, "setBookInfoBox: " + e.toString());
            }
        }

        private void setCommentBtns(final JSONArray commentInfos) {
            try {
                setCommentBtn(commentBtn1, commentInfos.getJSONObject(0));
                setCommentBtn(commentBtn2, commentInfos.getJSONObject(1));
                setCommentBtn(commentBtn3, commentInfos.getJSONObject(2));
            } catch (JSONException e) {
                Log.d(TAG, "setCommentBtns: " + e.toString());
            }

        }

        private void setCommentBtn(Button commentBtn, final JSONObject commentInfo) {
            try {
                commentBtn.setText(commentInfo.getString("comment"));
            } catch (JSONException e) {
                Log.d(TAG, "setCommentBtn: " + e.toString());
            }

            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        toSecondActivity(commentInfo.getString("relatedBookId"));
                    } catch (JSONException e) {
                        Log.d(TAG, "onClick: " + e.toString());
                    }

                }
            });
        }

        protected void toSecondActivity(String bookId) {
            Intent intent = new Intent();
            intent.setClass(SecondActivity.this, SecondActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("bookId", bookId);
            bundle.putString("mode", mode);

            intent.putExtras(bundle);
            startActivity(intent);
        }

        private void setBuyBtn(Button buyBtn, String bookId) {
            final String bookLink = "http://www.books.com.tw/products/" + bookId;
            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toThirdActivity(bookLink);
                }
            });
        }

        protected void toThirdActivity(String bookLink) {
            Intent intent = new Intent();
            intent.setClass(SecondActivity.this, ThirdActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("bookLink", bookLink);
            bundle.putString("mode", mode);

            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    protected void setBook(final BookInteraction book, String bookId) {
        final RequestQueue mQueue = Volley.newRequestQueue(this);

        String url = "http://140.116.247.169/getBook.php?bookId=" + bookId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onSuccessResponse: " + response.toString());
                        book.setBookInteraction(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    }
                }
        );
        String url2 = "http://140.116.247.169/getComments.php?bookId=" + bookId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                url2,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onSuccessResponse: " + response.toString());
                        book.setCommentBtns(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    }
                }
        );
        mQueue.add(jsonObjectRequest);
        mQueue.add(jsonArrayRequest);
    }
}