package com.example.yingjianwu.event_recommendation;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private Button submit;
    private EditText comment;

    List<Comment> commentList;
    private CommentAdapter mAdapter;
    private RecyclerView recyclerView;
    private AppDatabase db;
    private Context mContext;


    private Event event;
    private String userName;
    private String eventId;
    int addedComments = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mContext = this;

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("obj");
        event = (Event)bundle.getSerializable("event");

        userName = intent.getStringExtra("userName");
        eventId = event.getEventId();
        comment = findViewById(R.id.comment_edittext);
        comment.setShowSoftInputOnFocus(false);


        submit = findViewById(R.id.comment_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String post = comment.getText().toString();
                if (post == null || post.length() == 0) {
                    toast("Comment cannot be Empty");
                } else {
                    Comment c = new Comment();
                    c.setEventId(eventId);
                    c.setUserId(userName);
                    c.setText(post);
                    new InsertComment().execute(c);
                    //Refresh the List of Comments;
                    new LoadComments().execute();
                    comment.setText("");
                    addedComments += 1;
                }
            }
        });

        recyclerView = findViewById(R.id.comment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        db = Room.databaseBuilder(this, AppDatabase.class, db.Name).fallbackToDestructiveMigration().build();

        new LoadComments().execute();

    }

    class InsertComment extends  AsyncTask<Comment, Void, Void> {

        @Override
        protected Void doInBackground(Comment... comments) {
            Comment comment = comments[0];
            if (db.databaseInterface().checkEventExist(eventId) == null) {
                db.databaseInterface().insertEvent(event);
            }
            db.databaseInterface().insertComments(comment);
            db.databaseInterface().incrComments(comment.getEventId());
            return null;
        }
    }
    class LoadComments extends AsyncTask<Void, Void, List<Comment>> {

        @Override
        protected List<Comment> doInBackground(Void... voids) {
            List<Comment> cList = db.databaseInterface().getAllComments(eventId);
            return cList;
        }

        @Override
        protected void onPostExecute(List<Comment> cList){
            if (cList != null) {
                commentList = cList;
                mAdapter = new CommentAdapter(commentList);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }
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
    public void onBackPressed(){
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("event",event);
        bundle.putInt("comments", addedComments);
        data.putExtra("bundle",bundle);
        setResult(RESULT_OK,data);
        db.close();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, db.Name).fallbackToDestructiveMigration().build();
    }
}
