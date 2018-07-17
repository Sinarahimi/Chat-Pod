package ir.fanap.chat.sdk.application.chatexample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fanap.podchat.mainmodel.Invitee;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.util.ArrayList;
import java.util.Date;

import ir.fanap.chat.sdk.R;

public class ChatActivity extends AppCompatActivity implements ChatContract.view, AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final int FILE_REQUEST_CODE = 2;
    private ChatContract.presenter presenter;
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
    private Uri uri;

//    fel token
//    private static String TOKEN = "a11768091eac48f2a7b84ed6a241f9c3";
    //Fifi
//    private String name = "Fifi";
//    private static String TOKEN = "1fcecc269a8949d6b58312cab66a4926";
    //Token Alexi
    private static String TOKEN = "bebc31c4ead6458c90b607496dae25c6";
    private static String name = "Alexi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView textViewState = findViewById(R.id.textViewStateChat);
        TextView textViewToken = findViewById(R.id.textViewUserId);
        editText = findViewById(R.id.editTextMessage);
        editTextThread = findViewById(R.id.editTextThread);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        buttonFileChoose = findViewById(R.id.buttonFileChoose);
        buttonFileChoose.setOnClickListener(this);

        textViewToken.setText(TOKEN + name);
        Spinner spinner = findViewById(R.id.spinner);
        Spinner spinnerSecond = findViewById(R.id.spinnerSecond);

        presenter = new ChatPresenter(this, this);
        presenter.getLiveState().observe(this, textViewState::setText);

        setupSpinner(spinner);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, funcSecond);

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

                        presenter.sendFile(ChatActivity.this, "test file", 381
                                , getUri());
                        break;
                    case 3:
                        presenter.syncContact();
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
    public void onGetUserInfo() {

    }

    @Override
    public void onGetThreadList() {

    }

    @Override
    public void onGetThreadHistory() {

    }

    @Override
    public void onGetContacts() {
    }

    @Override
    public void onGetThreadParticipant() {

    }

    @Override
    public void onSentMessage() {

    }

    @Override
    public void onGetDeliverMessage() {

    }

    @Override
    public void onGetSeenMessage() {

    }

    @Override
    public void onEditMessage() {

    }

    @Override
    public void onCreateThread() {

    }

    @Override
    public void onMuteThread() {

    }

    @Override
    public void onUnMuteThread() {

    }

    @Override
    public void onRenameGroupThread() {

    }

    public void sendMessage(View view) {
        presenter.sendTextMessage("test at" + new Date().getTime() + name, 381, null);

//        String text = editText.getText().toString();
////        long textThread = Long.valueOf(editTextThread.getText().toString());
////        if (!text.equals("")) {
////            presenter.sendTextMessage(text, 381, null);
////        } else {
////            Toast.makeText(this, "Message is Empty", Toast.LENGTH_SHORT).show();
////        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
//            "Choose function",
                break;
            case 1:
                //"get thread"
//                ArrayList<Integer> threadIds = new ArrayList<>();
//                threadIds.add(312);
//                threadIds.add(351);
                presenter.getThread(2, 0, null);
                break;
            case 2:
                //"rename thread",
                presenter.renameThread(632, "new group name");
                break;
            case 3:
                //"get user info",
                presenter.getUserInfo();
                break;
            case 4:
                //"reply message",
                presenter.replyMessage("this is reply", 231, 1544);
                break;
            case 5:
                /**forward message */
                ArrayList<Long> messageIds = new ArrayList<>();
                messageIds.add(470L);
                messageIds.add(1353L);
                presenter.forwardMessage(381, messageIds);
                break;
            case 6:
                //"send text message",
                break;
            case 7:
                //"get thread participant",
                presenter.getThreadParticipant(50, 0, 577);
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
                Invitee[] invite = new Invitee[]{new Invitee(567,2)
                        ,new Invitee(571,2)
                        ,new Invitee(566,2)
                };
                presenter.createThread(0, invite, null);
                break;
            case 9:
                //get thread history
                presenter.getHistory(5, 0, null, 381);
                break;
            case 10:
                //"mute thread",
                presenter.muteThread(352);
                break;
            case 11:
                //"un mute thread"
                presenter.unMuteThread(352);
                break;
            case 12:
                //"get contacts"
                presenter.getContact(50, 0);
                break;
            case 13:
                //"edit message"
                presenter.editMessage(13530, "hi this is edit at" + new Date().getTime() + "by" + name);
                break;
            case 14:
                // add contact
                presenter.addContact("masoud", "sadeghi", "09122981131", "deviant@gmail.com");
                break;
            case 15:
                // remove contact
                presenter.removeContact(802);
                break;
            case 16:
                /**UPDATE CONTACTS*/
                presenter.updateContact(682, "sina", "amjadi", "09122964316", "dev@gmail.com"
                );
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        if (v == buttonFileChoose) {
            showPicChooser();
//            showFileChooser();
        }
    }

    private void showPicChooser() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_FILE_REQUEST);
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (requestCode == PICK_FILE_REQUEST) {
                    Uri selectedFileUri = data.getData();
                    String path = selectedFileUri.toString();
                    setUri(Uri.parse(path));

                } else if (requestCode == FILE_REQUEST_CODE) {
                    ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                    String path = files.get(0).getPath();
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
