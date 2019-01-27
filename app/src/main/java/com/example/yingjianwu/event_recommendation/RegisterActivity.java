package com.example.yingjianwu.event_recommendation;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private EditText userName;
    private EditText passWord;
    private EditText retypePassword;
    private Button register;
    private AppDatabase mdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //the database
        mdb = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, mdb.Name).fallbackToDestructiveMigration().build();

        userName = findViewById(R.id.editTextLogin);
        passWord = findViewById(R.id.editTextPassword);
        retypePassword  = findViewById(R.id.comfirmPassword);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(userName.getText().toString(), passWord.getText().toString(), retypePassword.getText().toString());
            }
        });

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
    protected void onRestart() {
        super.onRestart();
        mdb = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, mdb.Name).fallbackToDestructiveMigration().build();
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


    //check whether can register or not:
    public void register(String userName, String passWord, String confirmPassword) {
        String words = "";
        if (userName.length() < 6) {
            words = "UserName length must be at least 6";
            toast(words);
            return;
        } else if (passWord.length() < 6){
            words = "passWord length must be at least 6";
            toast(words);
            return;
        } else if (!passWord.equals(confirmPassword) ){
            words = "passWord and retypePassword do not match";
            toast(words);
            return;
        }

        new canRegister().execute(userName, passWord);
    }

    public void toast(String msg) {
        // --- Show the string for a long time
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    class canRegister extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String name = strings[0];
            if (mdb.databaseInterface().canRegister(name) != null) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                String name = userName.getText().toString();
                String pass = passWord.getText().toString();
                new insertRegister().execute(name, pass);
            } else {
                toast("Username is registered already");
            }
        }

    }

    class insertRegister extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            String name = strings[0];
            String pass = strings[1];
            mdb.databaseInterface().insertUser(new UserRecord(name,pass));
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                toast("Successfully Register");
                finish();
            }
        }
    }

}
