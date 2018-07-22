package ir.fanap.chat.sdk.application.chatexample;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.fanap.podchat.mainmodel.Invitee;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class ChatTest {

    private static ChatContract.presenter presenter;
    @Mock
    private static ChatContract.view view;
    @Mock
    private Activity activity;
    private Context appContext;

    //    @Rule
//    public ActivityTestRule<ChatActivity> mActivityRule = new ActivityTestRule<>(ChatActivity.class);
    //TOKEN = ALEXI
    private static String TOKEN = "1fcecc269a8949d6b58312cab66a4926";

    //    private static String TOKEN = "bebc31c4ead6458c90b607496dae25c6";
    private static String NAME = "ALEXI";

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getTargetContext();
        MockitoAnnotations.initMocks(this);
        presenter = new ChatPresenter(appContext, view);
        presenter.connect("ws://172.16.106.26:8003/ws",
                "POD-Chat", "chat-server", TOKEN, "http://172.16.110.76",
                "http://172.16.106.26:8080/hamsam/", "http://172.16.106.26:8080/hamsam/");
    }

    @Test
    @MediumTest
    public void getUserInfo() {
        presenter.getUserInfo();
        view.onGetUserInfo();
        Mockito.verify(view, Mockito.times(1)).onGetUserInfo();
    }

    @Test
    @MediumTest
    public void getThreadList() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.getThread(10, 0, null);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        view.onGetThreadList();
        Mockito.verify(view, Mockito.times(1)).onGetThreadList();
    }

    @Test
    @MediumTest
    public void getThreadHistory() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.getHistory(50, 0, null, 381);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onGetThreadHistory();
    }

    @Test
    @MediumTest
    public void getContacts() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.getContact(50, 0);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onGetContacts();
    }

    @Test
    @MediumTest
    public void getThreadParticipant() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.getThreadParticipant(10, 0, 352);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onGetThreadParticipant();
    }

    @Test
    @MediumTest
    public void sendTestMessageOnSent() {
        presenter.sendTextMessage("this is test", 381, null);
        view.onSentMessage();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onSentMessage();
    }

    @Test
    @MediumTest
    public void sendTestMessageOnDeliver() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.sendTextMessage("this is test", 381, null);
        view.onSentMessage();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onGetDeliverMessage();
    }

    @Test
    @LargeTest
    public void sendTestMessageOnSeen() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.sendTextMessage("this is test", 381, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onGetSeenMessage();
    }


    @Test
    @MediumTest
    public void editMessage() {
        view.onSentMessage();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.sendTextMessage("this is test", 381, null);
        Mockito.verify(view, Mockito.times(1)).onSentMessage();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.editMessage(1350, "salam this is edite" + new Date().getTime() + "by" + NAME);
        Mockito.verify(view, Mockito.times(1)).onEditMessage();
    }

    /**
     * int TO_BE_USER_SSO_ID = 1;
     * int TO_BE_USER_CONTACT_ID = 2;
     * int TO_BE_USER_CELLPHONE_NUMBER = 3;
     * int TO_BE_USER_USERNAME = 4;
     */
    /**
     * "create thread"
     * This is Invitee object
     * ---->private int id;
     * ---->private int idType;
     */
    @Test
    @MediumTest
    public void createThread() {
        //alexi 570
        //felfeli 571
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Invitee[] invite = new Invitee[]{new Invitee(566, 2)};
        presenter.createThread(0, invite, "yes");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onCreateThread();
    }

    @Test
    @MediumTest
    public void muteThread() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.muteThread(381);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onMuteThread();
    }

    @Test
    @MediumTest
    public void unMuteThread() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.unMuteThread(352);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onUnMuteThread();
    }

    @Test
    @MediumTest
    public void replyMessage() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        presenter.replyMessage("this is reply to all of you at" + new Date().getTime()
                , 381, 1353);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        Mockito.verify(view, Mockito.times(1)).
    }

    //fifi is the admin of groupId = 632
    @Test
    @MediumTest
    public void renameThread() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.renameThread(632, "5_گروه خودمونی");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onRenameGroupThread();
    }

    @Test
    @MediumTest
    public void addContact() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.addContact("maman", "sadeghi", "09122488169", "dev55@gmail.com");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onAddContact();
    }

    //    fifiUser
    @Test
    @MediumTest
    public void updateContact() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.updateContact(861, "masoud", "rahimi2", "09122488169"
                , "dev@gmail.com");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onUpdateContact();
    }

    @Test
    @MediumTest
    public void removeContact(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.removeContact(861);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onRemoveContact();
    }

    @Test
    @MediumTest
    public void uploadFile() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse("content://com.android.providers.downloads.documents/document/230");
        String fileUri = "/storage/emulated/0/Download/Manager.v6.31.Build.3.Crack.Only_pd.zip";
        presenter.uploadFile(appContext, activity, fileUri, uri);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onUploadFile();
    }

    @Test
    @MediumTest
    public void UploadImageFile(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse("content://media/external/images/media/781");
        presenter.uploadImage(appContext,activity,uri);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onUploadImageFile();
    }

    @Test
    @MediumTest
    public void syncContact(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.syncContact(activity);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view,Mockito.times(1)).onAddContact();
    }
}
