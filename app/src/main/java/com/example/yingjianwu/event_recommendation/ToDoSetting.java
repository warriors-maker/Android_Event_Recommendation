//package com.example.yingjianwu.event_recommendation;
//
//import android.arch.persistence.room.Room;
//import android.content.Context;
//import android.os.AsyncTask;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.RadioButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ToDoSetting extends AppCompatActivity {
//    private TextView name;
//    private TextView genre;
//    private TextView date;
//    private TextView time;
//    private TextView minPrice;
//    private TextView maxPrice;
//    private Context mContext;
//    private Button submit;
//
//    private RadioButton high;
//    private RadioButton med;
//    private RadioButton low;
//    private int priority;
//
//    private AppDatabase db;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_to_do_setting);
//
//        mContext = this;
//        submit = findViewById(R.id.submit);
//        name = findViewById(R.id.title);
//        genre = findViewById(R.id.genre);
//        date = findViewById(R.id.date);
//        time = findViewById(R.id.time);
//        minPrice = findViewById(R.id.min_price);
//        maxPrice = findViewById(R.id.max_price);
//
//        high = findViewById(R.id.radButton1);
//        med = findViewById(R.id.radButton2);
//        low = findViewById(R.id.radButton3);
//
//        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, db.Name).fallbackToDestructiveMigration().build();
//
//        high.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                priority = 3;
//            }
//        });
//
//        med.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                priority = 2;
//            }
//        });
//
//        low.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                priority = 1;
//            }
//        });
//
//
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (high.isSelected() || med.isSelected() || low.isSelected()) {
//                    if (high.isSelected()) {
//
//                    } else if (med.isSelected()){
//
//                    } else {
//
//                    }
//                    toast("Good");
//                    finish();
//                }
//            }
//        });
//    }
//
//    class upDateOrInsert extends AsyncTask <String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... strings) {
//            String userName = strings[0];
//            String eventId = strings[1];
//            ToDoItem toDoItem = (ToDoItem) db.databaseInterface().getToDoItem(userName, eventId);
//            if (toDoItem != null) {
//                db.databaseInterface().upDatePriority(userName, eventId);
//            } else {
//
//            }
//        }
//    }
//    class checkExist extends AsyncTask<String, Void, Integer> {
//
//        @Override
//        protected Integer doInBackground(String... strings) {
//            String userName = strings[0];
//            String eventId = strings[1];
//            ToDoItem toDoItem = (ToDoItem) db.databaseInterface().getToDoItem(userName, eventId);
//            if (toDoItem != null) {
//                return toDoItem.priority;
//            } else {
//                return -1;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Integer num) {
//            if (num == 1) {
//                low.setSelected(true);
//            } else if (num == 2) {
//                med.setSelected(true);
//            } else if (num == 3) {
//                high.setSelected(true);
//            }
//        }
//    }
//
//    public void toast(String msg) {
//        // --- Show the string for a long time
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//    }
//}
