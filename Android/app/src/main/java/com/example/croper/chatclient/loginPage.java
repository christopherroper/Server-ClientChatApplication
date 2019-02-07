package com.example.croper.chatclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;



public class loginPage extends AppCompatActivity {
    private EditText mChatroom;
    private String room;
    private EditText mUsername;
    public String username;
    public static final String EXTRA_MESSAGE = "com.example.croper.chatclient.MESSAGE";
    public static final String EXTRA_MESSAGE2 = "com.example.croper.chatclient.MESSAGE2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
    }
    /** Called when the user taps the Send button */
    public void loginButton(View view) {
        mChatroom = (EditText) findViewById(R.id.chatroom);
        mUsername = (EditText) findViewById(R.id.username);
        Intent intent = new Intent(this, chatroom.class);
//        EditText editText = (EditText) findViewById(R.id.editText);
        room = mChatroom.getText().toString();
        username = mUsername.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, room);
        intent.putExtra(EXTRA_MESSAGE2, username);
        startActivity(intent);
    }
}
