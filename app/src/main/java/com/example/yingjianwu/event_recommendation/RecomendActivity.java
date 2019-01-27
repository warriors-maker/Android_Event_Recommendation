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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;

public class RecomendActivity extends AppCompatActivity {
    String userName;
    List<Event> eventList = new ArrayList<>();
    final double default_lon = -71.057083;
    final double default_lat = 42.361145;
    double longitude = default_lon;
    double latitude = default_lat;
    Context mContext = this;
    String city;
    DateParser dp = new DateParser();
    private TextView loading;

    private EventAdapter mAdapter;
    private RecyclerView recyclerView;

    private AppDatabase db;

    private List<String> genreList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomend);
        loading = findViewById(R.id.Loading);

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        city = intent.getStringExtra("city");

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses = null;

        recyclerView = (RecyclerView) findViewById(R.id.recommendRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        db = Room.databaseBuilder(mContext, AppDatabase.class, db.Name).fallbackToDestructiveMigration().build();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            this.city = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }


        new PrepareRecommend().execute(userName);
    }


    public void toast(String msg) {
        // --- Show the string for a long time
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestart () {
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

    class DownLoadData extends AsyncTask<String, Void, List<Event>> {

        private List<String> genreList = null;

        public DownLoadData(List<String> genreList) {
            this.genreList = genreList;
        }

        @Override
        protected void onPreExecute() {
            loading.setText("Downloading the data");
            eventList.clear();
            mAdapter = new EventAdapter(eventList, mContext, userName);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected List<Event> doInBackground(String... strings) {
            String cityName = strings[0];
            //https://app.ticketmaster.com/discovery/v2/events.json?countryCode=US&city=Boston&sort=date,asc&size=30&apikey=o4jshCp0COUWqH5TSIofLEl8vwJG1djP

            List<Event> events = new ArrayList<>();

            for (String g : genreList) {
                //https://app.ticketmaster.com/discovery/v2/events.json?countryCode=US&city=Boston&sort=date,asc&genreId=KnvZfZ7vAeA&size=8&apikey=o4jshCp0COUWqH5TSIofLEl8vwJG1djP
                String s = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=US&city=" + cityName + "&sort=date,asc&genreId=" + g + "&size=8&apikey=o4jshCp0COUWqH5TSIofLEl8vwJG1djP";
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
                } catch (FileNotFoundException f) {
                    while (true) {
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
                        } catch (FileNotFoundException FN) {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (entity != null) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (entity == null) {
                    return null;
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
                                                }
                                                if (loc.has("latitude")) {
                                                    String str = loc.getString("latitude");
                                                    subEvent.setLat(Double.parseDouble(str));
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
                            events.addAll(list);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            // Getting JSON from URL
            return events;
        }

        @Override
        protected void onPostExecute(List<Event> list) {
            loading.setText("");
            if (list != null) {
                eventList = list;
//                for (Event event : eventList) {
//                    event.printObj();
//                }
                //toast("Thank you for waiting Downloading the Data");
                mAdapter = new EventAdapter(eventList, mContext, userName);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

//                for (Event subEvent : eventList) {
//                    subEvent.printObj();
//                }
            } else {
                toast("Come Back later...Seems like the Server is not responding.");
            }

        }

    }

    class myComparator implements Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            return o1.getValue() > o2.getValue() ? -1 : 1;
        }
    }

    class PrepareRecommend extends AsyncTask<String, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            db = Room.databaseBuilder(mContext, AppDatabase.class, db.Name).fallbackToDestructiveMigration().build();
        }
        @Override
        protected List<String> doInBackground(String... strings) {
            String userName = strings[0];
            List<EventLike> likedEvents = db.databaseInterface().getAllLike(userName);

            if (likedEvents != null) {

//                List<String> maxGenre = new ArrayList<>();

//                int maxCount = 0;
                HashMap<String, Integer> map = new HashMap<>();
                for (EventLike e : likedEvents) {
                    String gId = e.getGenreId();
                    if (map.containsKey(gId)) {
                        map.put(gId, map.get(gId) + 1);
                    } else {
                        map.put(gId, 1);
                    }

//                    int subCount = map.get(gId);
//                    if (subCount > maxCount) {
//                        maxCount = subCount;
//                        maxGenre.clear();
//                        maxGenre.add(gId);
//                    } else if (subCount == maxCount) {
//                        maxGenre.add(gId);
//                    }
                }

                PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(new myComparator());
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    pq.offer(entry);
                }

                return getGenreList(pq);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> list) {
            genreList = list;
            if (list == null || list.size() == 0) {
                loading.setText("You have to like events in order to get Recommendation");
            } else {
                new DownLoadData(genreList).execute(city);
            }
            db.close();
        }
    }

    private List<String> getGenreList(PriorityQueue<Map.Entry<String, Integer>> pq) {
        List<String> returnList = new ArrayList<>();
        int count = 3;
        while (count != 0 && !pq.isEmpty()) {
            returnList.add(pq.poll().getKey());
            count--;
        }
        return returnList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.comment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // --- Switch on the item ID that was clicked
        switch (item.getItemId()) {

            case R.id.refresh:
                new PrepareRecommend().execute(userName);
                break;

            default:
                toast("Hit Default! Should not be here!!");
                break;
        }
        return true;
    }
}
