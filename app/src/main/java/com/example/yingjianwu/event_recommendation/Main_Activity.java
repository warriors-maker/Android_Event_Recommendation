package com.example.yingjianwu.event_recommendation;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.yingjianwu.event_recommendation.DateParser.parse;

public class Main_Activity extends AppCompatActivity {
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

    private ProgressBar loadMore;

    private AppDatabase db;

    private boolean loadingData = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager layoutManager;

    List<Event> totalData = new ArrayList<>();

    int page = 1;
    final int loadEvents = 10;

    //    private static final int INITIAL_REQUEST=1337;
    //    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = INITIAL_REQUEST + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_);

        loading = findViewById(R.id.Loading);
        loadMore = findViewById(R.id.myRotatingProgressBarBar);
        page = 1;

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses = null;

        recyclerView = (RecyclerView) findViewById(R.id.eventRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();


        db = Room.databaseBuilder(mContext, AppDatabase.class, db.Name).fallbackToDestructiveMigration().build();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            this.city = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loadingData) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            toast("load more data");
                            loadingData = false;
                            System.out.println(page);
                            int[] arr = new int[2];
                            arr[0] = page;
                            arr[1] = loadEvents;
                            new LoadMoreData(arr).execute(city);
                        }
                    }
                }

            }

        });
        new DownLoadData().execute(city);
    }


    public void toast(String msg) {
        // --- Show the string for a long time
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    class LoadMoreData extends AsyncTask<String, Void, List<Event>> {
        int pageInner;
        int pageSize;

        public LoadMoreData(int[] arr) {
            this.pageInner = arr[0];
            this.pageSize = arr[1];
        }

        @Override
        protected void onPreExecute() {

            loadMore.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Event> doInBackground(String... strings) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<Event> returnList = new ArrayList<>();
            if (page >= 10) {
                return null;
            } else {
                int startIndex = 0;
                int endIndex = (page + 1) * 10;
                while (startIndex < endIndex) {
                    returnList.add(totalData.get(startIndex));
                    startIndex++;
                }
                return returnList;
            }
        }

        @Override
        protected void onPostExecute(List<Event> list) {
            loading.setText("");

            loadMore.setVisibility(View.GONE);
            if (list != null) {
                eventList.clear();
                eventList.addAll(list);
                mAdapter.notifyDataSetChanged();
            }
            page += 1;
            loadingData = true;
        }
    }

    class DownLoadData extends AsyncTask<String, Void, List<Event>> {

        @Override
        protected void onPreExecute() {
            loading.setText("Downloading the data");
        }

        @Override
        protected List<Event> doInBackground(String... strings) {
            String cityName = strings[0];
            //https://app.ticketmaster.com/discovery/v2/events.json?countryCode=US&city=Boston&sort=date,asc&size=30&apikey=o4jshCp0COUWqH5TSIofLEl8vwJG1djP
            String s = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=US" +
                    "&city=" + cityName + "&sort=date,asc&size=150&apikey=o4jshCp0COUWqH5TSIofLEl8vwJG1djP";
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            JSONObject entity = null;
            try {
                URL url = new URL(s);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                entity = new JSONObject(stringBuffer.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (entity.has("_embedded")) {
                JSONObject embed_obj = null;
                try {
                    embed_obj = (JSONObject) entity.get("_embedded");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (embed_obj.has("events")) {
                    List<Event> list = new ArrayList<>();

                    try {
                        JSONArray events_Array = embed_obj.getJSONArray("events");

                        for (int i = 0; i < events_Array.length(); i++) {

                            //Individual Object
                            JSONObject obj = (JSONObject) events_Array.get(i);
                            Event subEvent = new Event();

                            //check whether we have this record or not;
                            if (obj.has("id")) {
                                subEvent.setEventId(obj.getString("id"));
                            } else {
                                continue;
                            }

                            if (obj.has("name")) {
                                subEvent.setName(obj.getString("name"));
                            } else {
                                subEvent.setName(obj.getString("Unknown"));
                            }

                            if (obj.has("url")) {
                                subEvent.setEventUrl(obj.getString("url"));
                            } else {
                                subEvent.setEventUrl(obj.getString(""));
                            }

                            //Event start date
                            if (obj.has("dates")) {
                                JSONObject dates = (JSONObject) obj.get("dates");
                                if (dates.has("start")) {
                                    JSONObject start = (JSONObject) dates.get("start");

                                    if (start.has("localDate")) {
                                        subEvent.setEventsDate(start.getString("localDate"));
                                    } else {
                                        subEvent.setEventsDate("");
                                    }

                                    if (start.has("localTime")) {
                                        subEvent.setEventsTime(start.getString("localTime"));
                                    } else {
                                        subEvent.setEventsTime("");
                                    }
                                } else {
                                    subEvent.setEventsTime("");
                                    subEvent.setEventsDate("");
                                }
                            } else {
                                subEvent.setEventsTime("");
                                subEvent.setEventsDate("");
                            }

                            //Get the Genre
                            if (obj.has("classifications")) {
                                JSONArray cf = (JSONArray) obj.get("classifications");
                                if (cf.length() != 0) {
                                    JSONObject cfObj = (JSONObject) cf.get(0);
                                    if (cfObj.has("genre")) {
                                        JSONObject genre = (JSONObject) cfObj.get("genre");

                                        if (genre.has("id")) {
                                            subEvent.setGenreId(genre.getString("id"));
                                        } else {
                                            subEvent.setGenreId("");
                                        }

                                        if (genre.has("name")) {
                                            subEvent.setGenre(genre.getString("name"));
                                        }

                                    } else {
                                        subEvent.setGenreId("");
                                        subEvent.setGenre("");
                                    }

                                } else {
                                    subEvent.setGenreId("");
                                    subEvent.setGenre("");
                                }

                            } else {
                                subEvent.setGenreId("");
                                subEvent.setGenre("");
                            }

                            //Get lat and lon
                            if (obj.has("_embedded")) {
                                JSONObject emb = (JSONObject) obj.get("_embedded");
                                if (emb.has("venues")) {
                                    JSONArray venues = emb.getJSONArray("venues");
                                    if (venues.length() != 0) {
                                        JSONObject vObj = (JSONObject) venues.get(0);
                                        if (vObj.has("location")) {
                                            JSONObject loc = (JSONObject) vObj.get("location");
                                            if (loc.has("longitude")) {
                                                String str = loc.getString("longitude");
                                                subEvent.setLon(Double.parseDouble(str));
//                                                boolean neg = false;
//                                                if (str.charAt(0) == '-') {
//                                                    str = str.substring(1,str.length());
//                                                    neg = true;
//                                                }
//
//                                                Long pos = Long.parseLong(str);
//                                                if (neg) {
//                                                    subEvent.setLon(-pos);
//                                                } else {
//                                                    subEvent.setLon(pos);
//                                                }

                                            }
                                            if (loc.has("latitude")) {
                                                String str = loc.getString("latitude");
                                                subEvent.setLat(Double.parseDouble(str));
//                                                boolean neg = false;
//                                                if (str.charAt(0) == '-') {
//                                                    str = str.substring(1,str.length());
//                                                    neg = true;
//                                                }
//                                                Long pos = Long.parseLong(str);
//                                                if (neg) {
//                                                    subEvent.setLat(-pos);
//                                                } else {
//                                                    subEvent.setLat(pos);
//                                                }
                                            }
                                        }

                                    }
                                }
                            }

                            //Prices
                            if (obj.has("priceRanges")) {
                                JSONArray range = (JSONArray) obj.getJSONArray("priceRanges");
                                if (range.length() != 0) {
                                    JSONObject rObj = (JSONObject) range.get(0);
                                    if (rObj.has("min")) {
                                        subEvent.setMin_Price(Double.parseDouble(rObj.getString("min")));
                                    }
                                    if (rObj.has("max")) {
                                        subEvent.setMax_Price(Double.parseDouble(rObj.getString("max")));
                                    }

                                }
                            }
                            list.add(subEvent);
                        }

                        //If the Event is liked and stored in our Database
                        for (Event event : list) {
                            String eventId = event.getEventId();
                            Event sub = db.databaseInterface().checkEventExist(eventId);
                            if (sub != null) {
                                int numLikes = sub.getNumLikes();
                                int comments = sub.getNumComments();
                                event.setNumLikes(numLikes);
                                event.setNumComments(comments);
                            }
                        }
                        return list;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Getting JSON from URL
            return null;
        }

        @Override
        protected void onPostExecute(List<Event> list) {
            loading.setText("");
            if (list != null) {
                totalData = list;
                eventList = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    eventList.add(list.get(i));
                }
                mAdapter = new EventAdapter(eventList, mContext, userName);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, db.Name).fallbackToDestructiveMigration().build();

    }

    @Override
    protected void onStop() {
        db.close();
        super.onStop();
    }

    @Override
    protected void onPause() {
        db.close();
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, db.Name).fallbackToDestructiveMigration().build();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // --- Switch on the item ID that was clicked
        switch (item.getItemId()) {

            case R.id.quit:
                finish(); // --- Quit
                break;

            case R.id.recommend:
                Intent intent = new Intent(this, RecomendActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("cityName", city);
                startActivityForResult(intent, 1111);
                break;

            case R.id.Liked:
                Intent intent1 = new Intent(this, LikedEventActivity.class);
                intent1.putExtra("userName", userName);
                startActivityForResult(intent1, 2222);
                break;

            case R.id.Reload:
                eventList.clear();
                mAdapter = new EventAdapter(eventList, mContext, userName);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                new DownLoadData().execute(city);
                break;

            default:
                toast("Hit Default! Should not be here!!");
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2345 && resultCode == RESULT_OK) {
            //toast("Back");
            Bundle bundle = data.getBundleExtra("bundle");
            if (bundle == null) {
                toast("null");
            } else {
                int addedComments = bundle.getInt("comments");
                Event returnEvent = (Event) bundle.getSerializable("event");
                for (Event e : eventList) {
                    if (e.getEventId().equals(returnEvent.getEventId())) {
                        e.setNumComments(e.getNumComments() + addedComments);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        } else {
            eventList.clear();
            mAdapter = new EventAdapter(eventList, mContext, userName);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            new DownLoadData().execute(city);
        }
    }

}

