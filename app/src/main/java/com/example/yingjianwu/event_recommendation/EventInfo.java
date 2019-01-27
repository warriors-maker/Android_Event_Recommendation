package com.example.yingjianwu.event_recommendation;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class EventInfo extends AppCompatActivity {
    private Button submit;
//    private Button addToDo;
    private TextView name;
    private TextView genre;
    private TextView date;
    private TextView time;
    private TextView minPrice;
    private TextView maxPrice;
    private Context mContext;
    private String userName;

    private double lat;
    private double lon;
    private String url;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        mContext = this;
        submit = findViewById(R.id.submit);
        name = findViewById(R.id.title);
        genre = findViewById(R.id.genre);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        minPrice = findViewById(R.id.min_price);
        maxPrice = findViewById(R.id.max_price);
//        addToDo = findViewById(R.id.addToDo);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");

        event = (Event) bundle.getSerializable("event");
        userName = (String) bundle.getString("userName");


        name.setText(event.getName());
        genre.setText(event.getGenre());
        date.setText(event.getEventsDate());
        time.setText(event.getEventsTime());
        minPrice.setText(String.valueOf(event.getMin_Price()));
        maxPrice.setText(String.valueOf(event.getMax_Price()));

        lat = event.getLat();
        lon = event.getLon();
        url = event.getEventUrl();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("event",event);
                bundle.putString("userName",userName);
                intent.putExtra("bundle",bundle);
                startActivity(intent);

            }
        });

//        addToDo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, ToDoSetting.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("obj", event);
//                bundle.putString("userName", userName);
//                intent.putExtra("bundle", bundle);
//                startActivity(intent);
//            }
//        });

    }
}
