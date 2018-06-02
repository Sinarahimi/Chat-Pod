package ir.fanap.chat.sdk.application.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ir.fanap.chat.sdk.R;

public class ChatActivity extends AppCompatActivity implements ChatContract.view {

    ChatContract.presenter presenter;
    //ab
    private static String TOKEN = "ed4be26a60c24ed594e266a2181424c5";

    //private static String TOKEN = "afa51d8291dc4072a0831d3a18cb5030";
    //Felfeli
//    private static String TOKEN = "a11768091eac48f2a7b84ed6a241f9c3";
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView textViewState = findViewById(R.id.textViewStateChat);
        presenter = new ChatPresenter(this);
        presenter.getLiveState().observe(this, textViewState::setText);
        presenter = new ChatPresenter(this);
    }

    public void getThread(View view) {
        presenter.getThread(50, 0);
    }

    public void connect(View view) {
        presenter.connect("ws://172.16.106.26:8003/ws", "POD-Chat", "chat-server", TOKEN);
    }

    public void getThreadHistory(View view) {
        presenter.getHistory(50, 0, "desc", 83);
    }

    public void getContact(View view) {
        presenter.getContact(50, 0);
    }

    public void createThread(View view) {
        presenter.createThread(0, "this the  thread that we create");
    }

    public void sendMessage(View view) {
        EditText editText = findViewById(R.id.editTextMessage);
        EditText editTextThread = findViewById(R.id.editTextThread);
        String text = editText.getText().toString();
        long textThread = Long.valueOf(editTextThread.getText().toString());
        if (!text.equals("")) {
            presenter.sendTextMessage(text, textThread);
        } else {
            Toast.makeText(this,"Message is Empty",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetContacts(String content) {

    }
}
