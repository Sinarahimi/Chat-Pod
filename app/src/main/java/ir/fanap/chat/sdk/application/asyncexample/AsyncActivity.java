package ir.fanap.chat.sdk.application.asyncexample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import ir.fanap.chat.sdk.R;

public class AsyncActivity extends AppCompatActivity implements SocketContract.view {

    //("ws://172.16.110.235:8003/ws", "UIAPP")
//    @Inject
    SocketPresenter socketPresenter;
    private static final String SOCKET_SERVER = "ws://172.16.110.235:8003/ws";
    private Button connectButton;
    private TextView textViewState;
    private Button getStateButton;
    private Button closeButton;
    private Button buttonSendMessage;
    private TextView textViewPeerId;
    private EditText editTextReceiverId;
    //    private String name = "jiji";
//    private static String TOKEN = "fbd4ecedb898426394646e65c6b1d5d1";

    //Fifi
    private String name = "Fifi";
    private static String TOKEN = "5fb88da4c6914d07a501a76d68a62363";

//    private String name = "zizi";
//    private static String TOKEN = "7cba09ff83554fc98726430c30afcfc6";
    //Token Alexi
//    private static String TOKEN = "bebc31c4ead6458c90b607496dae25c6";
//    private static String name = "Alexi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);

        init();

        socketPresenter = new SocketPresenter(this, this);
        textViewPeerId.setText(socketPresenter.getPeerId());

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketPresenter.sendMessage("hello", 3);
                if (isEmpty(editTextReceiverId)) {
                    long receiverId = Long.valueOf(editTextReceiverId.getText().toString().trim());
                    final long[] receiverIdArray = {receiverId};
                    socketPresenter.sendMessage("hello", 3, receiverIdArray);
                } else {
                    Toast.makeText(AsyncActivity.this, "Message is Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        socketPresenter.closeSocket();
                    }
                }, 3000);
            }

        });

        getStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketPresenter.connect("ws://172.16.106.26:8003/ws", "POD-Chat", "chat-server",
                        "afa51d8291dc4072a0831d3a18cb5030", "http://172.16.110.76", "879765786gnkjSina");
            }
        });

    }

    @Override
    public void onStateChanged(String state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewState.setText(state);
            }
        });
    }

    private void init() {
        connectButton = findViewById(R.id.button);
        getStateButton = findViewById(R.id.getState);
        closeButton = findViewById(R.id.buttonclosesocket);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);
        textViewPeerId = findViewById(R.id.textViewPeerId);
        editTextReceiverId = findViewById(R.id.editTextReceiverId);
        textViewState = findViewById(R.id.textViewstate);

    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void showMessage(String message) {
        TextView textViewShowMessage = findViewById(R.id.textViewShowMessage);
        textViewShowMessage.setText(message);
        Log.d("message", message);
    }

    @Override
    public void messageCalled() {

    }

    @Override
    public void showErrorMessage(String error) {

    }

    private void sendMessage(String textMessage, int messageType, long[] receiversId) {
        socketPresenter.sendMessage(textMessage, messageType, receiversId);
    }

    public void getThread(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketPresenter.socketLogOut();
    }
}
