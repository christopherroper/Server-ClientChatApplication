package com.example.croper.chatclient;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

public class chatroom extends AppCompatActivity {
    private String username;
    private EditText mMessage;
    private String message;
    private LinearLayout scroll;
    private ScrollView scrollView;
    private WebSocket websocket;
    private String roomName;
    public static final String EXTRA_MESSAGE3 = "com.example.croper.chatclient.MESSAGE3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        scroll = findViewById (R.id.scroll);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        roomName = intent.getStringExtra(loginPage.EXTRA_MESSAGE);
        username = intent.getStringExtra(loginPage.EXTRA_MESSAGE2);
        // Capture the layout's TextView and set the string as its text
        TextView roomNameTitle = findViewById(R.id.title);
        roomNameTitle.setText("Chatroom: " + roomName);
        scrollView = findViewById (R.id.scrollView3);

        AsyncHttpClient.getDefaultInstance().websocket("http://10.0.2.2:8080/SlackServer.html", "my-protocol", new AsyncHttpClient.WebSocketConnectCallback() {

            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
//                webSocket.send("a string");
//                webSocket.send(new byte[10]);
                chatroom.this.websocket = webSocket;
                webSocket.send("join " + roomName);
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
                        System.out.println("I got a string: " + s);
                        postMessage(s);
                    }
                });
            }
        });
    }
    public void sendMessageButton(View view) {
        mMessage = findViewById(R.id.messageBody);
        message = mMessage.getText().toString();
//        Intent intent = new Intent(this, chatroom.class);
//        intent.putExtra(EXTRA_MESSAGE3, message);
//        TextView messageSent = new TextView(this);
        String combinedMessage = username + " " + message;
        //scroll.addView(messageSent);
        //startActivity(intent);
        websocket.send(combinedMessage);
        mMessage.setText("");
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
    public void postMessage(String s) {
        Handler handler =new Handler (chatroom.this.getMainLooper());
        final String string = s;
        JSONObject json = null;
        try {
            json = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String username = "";
        try {
            username = json.getString("user");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String message = "";
        try {
            message = json.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String combined = username + ": " + message;
        handler.post(new Runnable() {
            @Override
            public void run() {
                TextView messagePost = new TextView(chatroom.this);
                messagePost.setText(combined);
                // messagePost.setLayoutParams(new LayoutParams(LayoutParams.))
                //ListView *can work with array lists<spanned>
                //ArrayAdapter connects these things
                //lv.notifyDataChanged()
                scroll.addView(messagePost);

            }
        });
    }
}
