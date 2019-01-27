package com.example.yingjianwu.event_recommendation;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    List<Event> eventList = new ArrayList<>();
    Context context;
    String userName;
    AppDatabase db;

//    private int visibleThreshold = 5;
//    private int lastVisibleItem, totalItemCount;
//    private boolean loading;
//    private OnLoadMoreListener onLoadMoreListener;
//    private LinearLayoutManager linearLayoutManager;

    public EventAdapter(List<Event> list, Context context, String userName) {
        this.eventList = list;
        this.context = context;
        this.userName = userName;
        db = Room.databaseBuilder(context, AppDatabase.class, db.Name).fallbackToDestructiveMigration().build();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(
                viewGroup.getContext());
        View v = inflater.inflate(R.layout.event_list_item, viewGroup, false);
        EventAdapter.ViewHolder vh = new EventAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (position % 4 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else if (position % 4 == 1) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.ColorBlue));
        } else if (position % 4 == 2){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.ColorGreen));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.ColorYellow));
        }
        final Event event = eventList.get(position);
        holder.title.setText(event.getName());
        holder.time.setText(event.getEventsDate() +"T:" + event.getEventsTime());
        holder.genre.setText(event.getGenre());

        //will update the likes and comments right now
        new CheckedExist(holder).execute(event);
        //will display the correct image for the user given if he likes or not
        new CheckedLike(holder).execute(event);


        holder.likeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Drawable.ConstantState currentDraw = holder.likeImg.getDrawable().getConstantState();
                if (currentDraw == holder.itemView.getResources().getDrawable(R.drawable.baseline_favorite_border_black_18dp).getConstantState()) {
                    holder.likeImg.setImageResource(R.drawable.baseline_favorite_black_18dp);
                    //addLike
                    new LikeEventAsync().execute(event);

                } else if (currentDraw == holder.itemView.getResources().getDrawable(R.drawable.baseline_favorite_black_18dp).getConstantState()){
                    holder.likeImg.setImageResource(R.drawable.baseline_favorite_border_black_18dp);
                    //RemoveLike
                    new UnLikeEventAsync().execute(event);
                }

            }
        });

        holder.commentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);
                intent.putExtra("obj",bundle);
                intent.putExtra("userName", userName);
                ((Activity) context).startActivityForResult(intent,2345);
            }
        });

//        if (! (context instanceof RecomendActivity)) {
//            holder.commentImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context,CommentActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("event", event);
//                    intent.putExtra("obj",bundle);
//                    intent.putExtra("userName", userName);
//                    ((Activity) context).startActivity(intent);
//                }
//            });
//        }


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event current = eventList.get(position);
                Intent intent = new Intent(context,EventInfo.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", current);
                bundle.putString("userName", userName);
                intent.putExtra("bundle", bundle);
                ((Activity) context).startActivityForResult(intent,1234);
            }
        });


    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class CheckedExist extends AsyncTask<Event, Void, int[]> {
        private ViewHolder holder;
        public CheckedExist(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        protected int[] doInBackground(Event... events) {
            Event event = events[0];
            Event eventDb = db.databaseInterface().checkEventExist(event.getEventId());
            int[] arr = new int[2];
            if (eventDb != null) {
                arr[0] = eventDb.getNumLikes();
                arr[1] = eventDb.getNumComments();
                return arr;
            } else {
                return arr;
            }
        }

        @Override
        protected void onPostExecute(int[] arr) {
            if (arr != null) {
                holder.numLikes.setText(String.valueOf(arr[0]));
                holder.numComments.setText(String.valueOf(arr[1]));
            }
        }
    }
    //Check whether the event is liked or not;
    class CheckedLike extends AsyncTask<Event, Void, Boolean> {
        private ViewHolder holder;

        public CheckedLike(ViewHolder viewHolder) {
            this.holder = viewHolder;
        }

        @Override
        protected Boolean doInBackground(Event... events) {
            Event event = events[0];
            EventLike e = db.databaseInterface().checkLike(userName, event.getEventId());
            if (e == null) {
                return false;
            } else {
                return true;
            }
        }
        @Override
        protected void onPostExecute(Boolean b) {
            if (b) {
                holder.likeImg.setImageResource(R.drawable.baseline_favorite_black_18dp);
            } else {
                holder.likeImg.setImageResource(R.drawable.baseline_favorite_border_black_18dp);
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView time;
        public View layout;
        public TextView numComments;
        public TextView numLikes;
        public ImageView likeImg;
        public ImageView commentImg;
        public TextView genre;


        public ViewHolder(@NonNull View v) {
            super(v);
            layout = v;
            title = (TextView) v.findViewById(R.id.event_item_title);
            time = (TextView) v.findViewById(R.id.event_item_time);
            numComments = (TextView) v.findViewById(R.id.event_comment_number);
            numLikes = (TextView) v.findViewById(R.id.event_good_number);
            likeImg = (ImageView) v.findViewById(R.id.event_good_img);
            commentImg = (ImageView) v.findViewById(R.id.event_comment_img);
            genre = (TextView) v.findViewById(R.id.event_item_genre);
        }
    }

    class UnLikeEventAsync extends AsyncTask<Event, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Event... events) {
            Event current = events[0];
            String eventId = current.getEventId();

            db.databaseInterface().decLike(eventId);
            db.databaseInterface().deleteLike(eventId, userName);
            for (int i = 0; i < eventList.size(); i++) {
                Event sub = eventList.get(i);
                //Only sync with the current App;
                if (sub.getEventId().equals(current.getEventId())) {
                    Event dbEvent = db.databaseInterface().checkEventExist(current.getEventId());
                    int likes = dbEvent.getNumLikes();
                    sub.setNumLikes(likes);
                }
            }
            return eventList;
        }

        @Override
        protected void onPostExecute(List<Event> list) {
            notifyDataSetChanged();
        }

    }

    class LikeEventAsync extends AsyncTask<Event, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Event...events) {
            Event current = events[0];
            String eventId = current.getEventId();
            String genreId = current.getGenreId();


            //Go to check database whether we have this event or not;
            if (db.databaseInterface().checkEventExist(eventId) == null) {
                //if no, add to event table
                db.databaseInterface().insertEvent(current);
            }
            //increment num of likes by 1
            db.databaseInterface().incrtLike(eventId);
            //add to like table
            EventLike eventlike = new EventLike();
            eventlike.setEventId(eventId);
            eventlike.setUserId(userName);
            eventlike.setGenreId(genreId);
            db.databaseInterface().insertLike(eventlike);

            //Sync the Event with the arraylist;
            for (int i = 0; i < eventList.size(); i++) {
                Event sub = eventList.get(i);
                //Only sync with the current App;
                if (sub.getEventId().equals(current.getEventId())) {
                    Event dbEvent = db.databaseInterface().checkEventExist(current.getEventId());
                    int likes = dbEvent.getNumLikes();
                    sub.setNumLikes(likes);
                }
            }
            return eventList;
        }

        @Override
        protected void onPostExecute(List<Event> list) {
            notifyDataSetChanged();
        }

    }
}
