package com.example.yingjianwu.event_recommendation;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText userName;
    private EditText passWord;
    private Button mSubmitButton;
    private Button mRegisterButton;
    private AppDatabase mdb;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mdb = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, mdb.Name).fallbackToDestructiveMigration().build();

        userName = findViewById(R.id.editTextLogin);
        passWord = findViewById(R.id.editTextPassword);
        mSubmitButton = findViewById(R.id.submit);
        mRegisterButton = findViewById(R.id.register);
        mContext =this;

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(mContext, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Login().execute(userName.getText().toString(), passWord.getText().toString());
            }
        });

        //new clearDatabase().execute();


    }

    class Login extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            String name = strings[0];
            String pass = strings[1];
            if (mdb.databaseInterface().canRegister(name) == null) {
                return false;
            } else if (mdb.databaseInterface().canLogin(name, pass) == null) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                Intent login = new Intent(mContext, Main_Activity.class);
                login.putExtra("userName", userName.getText().toString());
                startActivity(login);
                userName.setText("");
                passWord.setText("");
            } else {
                toast("Username is not registerd or Password does not match");
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
        mdb = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, mdb.Name).fallbackToDestructiveMigration().build();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mdb.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mdb.close();
    }

    @Override
    protected void onDestroy() {
        mdb.close();
        super.onDestroy();
    }



    @Override
    protected void onResume() {
        super.onResume();
        mdb = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, mdb.Name).fallbackToDestructiveMigration().build();
    }

    class clearDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mdb.databaseInterface().dropEventLikeTable();
            mdb.databaseInterface().dropEventTable();
            mdb.databaseInterface().dropComment();
            return null;
        }
    }
}
