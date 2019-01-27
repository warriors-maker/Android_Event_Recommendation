package com.example.yingjianwu.event_recommendation;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LikedEventActivity extends AppCompatActivity {
    String userName;
    List<Event> eventList = new ArrayList<>();
    final double default_lon = -71.057083;
    final double default_lat = 42.361145;
    double longitude = default_lon;
    double latitude = default_lat;
    Context mContext = this;
    String city = "Boston";
    DateParser dp = new DateParser();
    private TextView loading;
    private EventAdapter mAdapter;
    private RecyclerView recyclerView;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_event);
        loading = findViewById(R.id.Loading);
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses = null;

        recyclerView = (RecyclerView) findViewById(R.id.eventRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        db = Room.databaseBuilder(mContext, AppDatabase.class, db.Name).fallbackToDestructiveMigration().build();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            this.city = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new DownLoadLikedEvent().execute(userName);
    }


    public void toast(String msg) {
        // --- Show the string for a long time
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    class DownLoadLikedEvent extends AsyncTask<String, Void, List<Event>> {
        @Override
        protected void onPreExecute() {
            loading.setText("Loading the Liked Events"
            );
        }

        @Override
        protected List<Event> doInBackground(String... strings) {
            List<EventLike> list = db.databaseInterface().getAllLike(userName);
            if (list == null) {
                return null;
            }

            List<Event> returnList = new ArrayList<>();
            for (EventLike l : list) {
                String eventId = l.getEventId();
                Event event = db.databaseInterface().checkEventExist(eventId);
                if (event != null) {
                    returnList.add(event);
                }
            }
            return returnList;
        }

        @Override
        protected void onPostExecute(List<Event> list) {
            if(list != null && list.size() != 0) {
                loading.setText("");
                eventList = list;
                //toast("Thank you for waiting Downloading the Data");
                mAdapter = new EventAdapter(eventList, mContext, userName);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {
                loading.setText("You have not liked Any Event yet");
            }
        }
    }
    @Override
    protected void onRestart () {
        super.onRestart();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, db.Name).fallbackToDestructiveMigration().build();
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }



    @Override
    protected void onResume() {
        super.onResume();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, db.Name).fallbackToDestructiveMigration().build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2345 &&resultCode == RESULT_OK) {
            //toast("Back");
            Bundle bundle = data.getBundleExtra("bundle");
            if (bundle == null) {
                toast("null");
            } else {
                int addedComments = bundle.getInt("comments");
                Event returnEvent = (Event)bundle.getSerializable("event");
                for (Event e : eventList) {
                    if (e.getEventId().equals(returnEvent.getEventId())) {
                        e.setNumComments(e.getNumComments() + addedComments);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

        }
    }
}











