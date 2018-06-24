package ir.fanap.chat.sdk.application.chatexample;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fanap.podchat.model.Invitee;

import java.util.Date;

import ir.fanap.chat.sdk.R;

public class ChatActivity extends AppCompatActivity implements ChatContract.view {
    private ChatContract.presenter presenter;
    private ConstraintLayout constraintLayout;
    private EditText editText;
    private EditText editTextThread;
    //ab token
//    private static String TOKEN = "ed4be26a60c24ed594e266a2181424c5";
    //baz token
//     private static String TOKEN = "afa51d8291dc4072a0831d3a18cb5030";
    //zam token
//    private static String TOKEN = "c0866c4cc5274ea7ada6b01575b19d24";

    //fel token
//    private static String TOKEN = "a11768091eac48f2a7b84ed6a241f9c3";
    //Fifi
    private static String TOKEN = "1fcecc269a8949d6b58312cab66a4926";
    //Token Alexi
//    private static String TOKEN = "bebc31c4ead6458c90b607496dae25c6";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView textViewState = findViewById(R.id.textViewStateChat);
        TextView textViewToken = findViewById(R.id.textViewUserId);
        editText = findViewById(R.id.editTextMessage);
        editTextThread = findViewById(R.id.editTextThread);
        constraintLayout = findViewById(R.id.constraintLayout);
        textViewToken.setText(TOKEN);

        presenter = new ChatPresenter(this);
        presenter.getLiveState().observe(this, textViewState::setText);
        presenter = new ChatPresenter(this);

    }

    public void getThread(View view) {
        presenter.getThread(50, 0);
    }

    public void connect(View view) {
        presenter.connect("ws://172.16.106.26:8003/ws",
                "POD-Chat", "chat-server", TOKEN, "http://172.16.110.76",
                "http://172.16.106.26:8080/hamsam/");
    }

    public void getThreadHistory(View view) {
        presenter.getHistory(50, 0, "desc", 191);
    }

    public void getContact(View view) {
        presenter.getContact(50, 0);
    }

    /** int TO_BE_USER_SSO_ID = 1;
        int TO_BE_USER_CONTACT_ID = 2;
        int TO_BE_USER_CELLPHONE_NUMBER = 3;
        int TO_BE_USER_USERNAME = 4;*/
    public void createThread(View view) {
        Invitee[] invite = new Invitee[]{new Invitee(485, 2)};
        presenter.createThread(0, invite, "");
    }

    @Override
    public void onGetContacts(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetUserInfoId(int UserId) {
        Toast.makeText(this, UserId, Toast.LENGTH_SHORT).show();
    }

    //thread id  231
    public void mute(View view) {
        presenter.muteThread(231);
    }

    //thread id  231
    public void unMute(View view) {
        presenter.unMuteThread(231);
    }

    public void EditMsg(View view) {
        presenter.editMessage(533, "edited_at" + new Date().getTime());
    }

    public void getParticipant(View view) {
        EditText editText = findViewById(R.id.editTextMessage);
        EditText editTextThread = findViewById(R.id.editTextThread);
        String text = editText.getText().toString();
        long textThread = Long.valueOf(editTextThread.getText().toString());
        if (!text.equals("")) {
            presenter.getThreadParticipant(50, 5, textThread);
        } else {
            Snackbar.make(constraintLayout, "Message is Empty", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void sendMessage(View view) {
        String text = editText.getText().toString();
        long textThread = Long.valueOf(editTextThread.getText().toString());
        if (!text.equals("")) {
            presenter.sendTextMessage(text, textThread);
        } else {
            Toast.makeText(this, "Message is Empty", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendReplyMessage(View view) {
        String text = editText.getText().toString();
        long textThread = Long.valueOf(editTextThread.getText().toString());
        if (!text.equals("")) {
            presenter.sendReplyMessage(text, textThread, 532);
        } else {
            Toast.makeText(this, "Message is Empty", Toast.LENGTH_SHORT).show();
        }
    }

    public void addContact(View view) {
        presenter.addContact("SINA", "RAHIMI", "09183565465", "DEVE@MAIL.COM");
//        presenter.removeContact(561);
    }
}
