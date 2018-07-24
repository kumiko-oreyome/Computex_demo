package com.ncku.iir.computex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class favorite_list extends AppCompatActivity {

    String[] itemList = new String[]{"庫肯花園", "轉角", "貓吐司"};
    String[] addressList;
    private ListView listView;
    private TextView res_txtView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        listView = (ListView) findViewById(R.id.fav_list_view);
        res_txtView = (TextView) findViewById(R.id.fav_res_textView);



        //建立一個arrayadapter 把 item的放進去 ，再丟到 list view裡面
        ArrayAdapter<String> adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, itemList);
        listView.setAdapter(adapterItems);

        listView.setSelector(R.drawable.list_view_selector);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(lstSelectListener);

    }

    private ListView.OnItemClickListener lstSelectListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            String select = adapterView.getItemAtPosition(position).toString();
            res_txtView.setText(select);
        }
    };
}
