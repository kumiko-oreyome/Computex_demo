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
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.asus.robotframework.API.RobotAPI;
import com.bumptech.glide.Glide;
import com.ncku.iir.computex.R;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.MainActivityFragment;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;
import com.ncku.iir.computex.util.TextUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SecondFragment extends SpeechFragment {

    private static final String ARG_MODE = "mode";
    private static final String ARG_BOOKID = "bookid";
    private String mode;
    private String bookId;

    private  BookInteraction book;
    private static final String TAG = "SecondActivity";
    private ImageButton homeBtn;
    private RobotAPI api;

    public SecondFragment() {

    }


    public static SecondFragment newInstance(String mode, String bookid) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE, mode);
        args.putString(ARG_BOOKID,  bookid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getString(ARG_MODE);
            bookId = getArguments().getString(ARG_BOOKID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_second, container, false);
        api = Global.api;
        Log.d(TAG, mode);

        Button exitBtn = (Button) view.findViewById(R.id.exitBtn);
        homeBtn = view.findViewById(R.id.homeButton2);

       book = new BookInteraction(
                (ImageView) view.findViewById(R.id.bookImg),
                (TextView) view.findViewById(R.id.bookRating),
                (TextView) view.findViewById(R.id.bookName),
                (TextView) view.findViewById(R.id.bookContent),
                (TextView) view.findViewById(R.id.bookType),
                (Button) view.findViewById(R.id.commentBtn1),
                (Button) view.findViewById(R.id.commentBtn2),
                (Button) view.findViewById(R.id.commentBtn3),
                (Button) view.findViewById(R.id.buyBtn)
        );

        setBook(book, bookId);

        homeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                jumpNextFragment(new MainActivityFragment());

            }
        });

        exitBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(final View view) {
                if(mode.equals("demo")){
                    jumpNextFragment(new MainActivityFragment());
                    //Global.speak("warning");
                }else{
                    jumpNextFragment(new MainActivityFragment());
                }
                //bundle.putString("mode", mode);
            }
        });
        return view;
    }


    protected void setBook(final BookInteraction book, String bookId) {
        final RequestQueue mQueue = Volley.newRequestQueue(Global.ma);

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
                Glide.with(Global.ma).load(bookInfo.getString("bookImgUrl")).into(bookImg);
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
            jumpNextFragment(SecondFragment.newInstance(mode,bookId));
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

        public void toThirdActivity(String bookLink) {
            jumpNextFragment(ThirdFragment.newInstance(mode,bookId));
        }
    }
    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {
                final String bookLink = "http://www.books.com.tw/products/" + bookId;
                if(TextUtil.inKeywords(text,"購買","想","要","不錯","天")){
                    book.toThirdActivity( bookLink);
                }
                else if(text.contains("離開")){
                    homeBtn.performClick();
                }else{
                    Global.speak("");
                }
            }
        };
    }
}
