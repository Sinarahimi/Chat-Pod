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

public class AsyncSandBoxActivity extends AppCompatActivity implements SocketContract.view {

    //("ws://172.16.110.235:8003/ws", "UIAPP")
//    @Inject
    SocketPresenter socketPresenter;
    private static final String SOCKET_SERVER = "ws://172.16.110.235:8003/ws";
    Button connectButton;
    Button getStateButton;
    Button closeButton;
    Button buttonSendMessage;
    TextView textViewPeerId;
    EditText editTextReceiverId;
    TextView textView;

    private String TOKEN = "ecfee54231e8467b838858e5ae3a66d6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);

        init();

        socketPresenter = new SocketPresenter(this, this);
        textViewPeerId.setText(socketPresenter.getPeerId());

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketPresenter.connect("ws://chat-sandbox.pod.land/ws", "POD-Chat", "chat-server",
                        TOKEN, "https://sandbox.pod.land:8043/srv/basic-platform/", "ksf98jhsdf5784");

            }
        });

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                socketPresenter.sendMessage("hello", 3);

                if (isEmpty(editTextReceiverId)) {
                    long receiverId = Long.valueOf(editTextReceiverId.getText().toString().trim());
                    final long[] receiverIdArray = {receiverId};
                    socketPresenter.sendMessage("hello", 3);
                } else {
                    Toast.makeText(AsyncSandBoxActivity.this, "Message is Empty", Toast.LENGTH_SHORT).show();
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
    }

    private void init() {
        connectButton = findViewById(R.id.button);
        getStateButton = findViewById(R.id.getState);
        closeButton = findViewById(R.id.buttonclosesocket);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);
        textViewPeerId = findViewById(R.id.textViewPeerId);
        editTextReceiverId = findViewById(R.id.editTextReceiverId);
        textView = findViewById(R.id.textViewstate);
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
    public void onStateChanged(String state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(state);
            }
        });
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
