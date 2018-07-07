package ir.fanap.chat.sdk.application.chatexample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fanap.podchat.model.Invitee;
import com.fanap.podchat.util.FilePath;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Date;

import ir.fanap.chat.sdk.R;

public class ChatActivity extends AppCompatActivity implements ChatContract.view, AdapterView.OnItemSelectedListener, View.OnClickListener {
    private ChatContract.presenter presenter;
    private ConstraintLayout constraintLayout;
    private EditText editText;
    private EditText editTextThread;
    private Button buttonFileChoose;
    private String selectedFilePath;
    private static final int PICK_FILE_REQUEST = 1;
    private static final String[] func = {
            "Choose function",
            "get thread",
            "rename thread",
            "get user info",
            "reply message",
            "forward message",
            "send text message",
            "get thread participant",
            "create thread",
            "get thread history",
            "mute thread",
            "un mute thread"
            , "get contacts"
            , "edit message"
            , "add contact"
            , "remove contact"
            , "update contact"
    };

    private static final String[] funcSecond = {
            "Choose function"
            , "Sync Contact"
            , "Send file"
    };

    //fel token
//    private static String TOKEN = "a11768091eac48f2a7b84ed6a241f9c3";
    //Fifi
    private String name = "Fifi";
    private static String TOKEN = "1fcecc269a8949d6b58312cab66a4926";
    private Uri uri;
//    zizi
//    private static final String name = "zizi";
//    private static String TOKEN = "7cba09ff83554fc98726430c30afcfc6";
    //Token Alexi
//    private static String TOKEN = "bebc31c4ead6458c90b607496dae25c6";
//    private static String name = "Alexi";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView textViewState = findViewById(R.id.textViewStateChat);
        TextView textViewToken = findViewById(R.id.textViewUserId);
        editText = findViewById(R.id.editTextMessage);
        editTextThread = findViewById(R.id.editTextThread);
        constraintLayout = findViewById(R.id.constraintLayout);
        buttonFileChoose = findViewById(R.id.buttonFileChoose);
        buttonFileChoose.setOnClickListener(this);

        textViewToken.setText(TOKEN + name);
        Spinner spinner = findViewById(R.id.spinner);
        Spinner spinnerSecond = findViewById(R.id.spinnerSecond);

        presenter = new ChatPresenter(this);
        presenter.getLiveState().observe(this, textViewState::setText);

        setupSpinner(spinner);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, funcSecond);

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSecond.setAdapter(adapterSpinner);
        spinnerSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
//            "Choose function",
                        break;
                    case 1:
                        presenter.syncContact();
                        break;
                    case 2:
                        presenter.sendFile("http://sandbox.pod.land:8080"
                                , Uri.parse("/storage/emulated/0/Download/fff.png")
                                , "test file", ChatActivity.this);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupSpinner(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, func);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void connect(View view) {
        presenter.connect("ws://172.16.106.26:8003/ws",
                "POD-Chat", "chat-server", TOKEN, "http://172.16.110.76",
                "http://172.16.106.26:8080/hamsam/");
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//            , "add contact"
//            , "remove contact"
//            , "update contact"
        switch (position) {
            case 0:
//            "Choose function",

                break;
            case 1:
                //"get thread"
//                ArrayList<Integer> threadIds = new ArrayList<>();
//                threadIds.add(312);
//                threadIds.add(351);
                presenter.getThread(50, 0, null);
                break;
            case 2:
                //            "rename thread",
                presenter.renameThread(379, "new group name");
                break;
            case 3:
                //            "get user info",
                presenter.getUserInfo();
                break;
            case 4:
                //            "reply message",
                presenter.sendReplyMessage("this is reply", 231, 1544);
                break;
            case 5:
                //            "forward message",
                ArrayList<Long> messageIds = new ArrayList<>();
                messageIds.add(11956L);
                presenter.forwardMessage(312, messageIds);
                break;
            case 6:
                //            "send text message",
                break;
            case 7:
                //            "get thread participant",
                break;
            case 8:
                /**
                 * int TO_BE_USER_SSO_ID = 1;
                 * int TO_BE_USER_CONTACT_ID = 2;
                 * int TO_BE_USER_CELLPHONE_NUMBER = 3;
                 * int TO_BE_USER_USERNAME = 4;
                 */
                /**"create thread"
                 * This is Invitee object
                 * ---->private int id;
                 * ---->private int idType;
                 *
                 */
                //alexi 570
                //felfeli 571
                Invitee[] invite = new Invitee[]{new Invitee(570, 2)};
                presenter.createThread(0, invite, "");

                break;
            case 9:
                presenter.getHistory(50, 0, "desc", 312);
                break;
            case 10:
                //            "mute thread",
                break;
            case 11:
//            "un mute thread"
                break;
            case 12:
//            "get contacts"
                presenter.getContact(50, 0);
                break;
            case 13:
//            , "edit message"
                break;
            case 14:
                // add contact
                presenter.addContact("", "", "09122451131", "");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v == buttonFileChoose) {
            showFileChooser();
        }
    }

    private void showFileChooser() {
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent galleryInten = new Intent();
        galleryInten.setType("image/*");
        galleryInten.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryInten, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data != null) {
                    Uri selectedFileUri = data.getData();
//                    selectedFilePath = FilePath.getPath(this, selectedFileUri);
                    Logger.i("Selected File Path:" + selectedFilePath);
                    setUri(selectedFileUri);

                }
            }
        }
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }
}
