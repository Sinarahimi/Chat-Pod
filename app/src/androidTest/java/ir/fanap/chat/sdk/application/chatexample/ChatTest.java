package ir.fanap.chat.sdk.application.chatexample;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.model.Invitee;

import org.junit.Before;
import org.junit.Rule;
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

    @Rule
    public ActivityTestRule<ChatActivity> mActivityRule = new ActivityTestRule<>(ChatActivity.class);
    //TOKEN = ALEXI
    private static String TOKEN = "bebc31c4ead6458c90b607496dae25c6";
    private static String NAME = "ALEXI";

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        MockitoAnnotations.initMocks(this);
        presenter = new ChatPresenter(appContext,view);
        presenter.connect("ws://172.16.106.26:8003/ws",
                "POD-Chat", "chat-server", TOKEN, "http://172.16.110.76",
                "http://172.16.106.26:8080/hamsam/");
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
        presenter.getThread(20, 0, null);
        view.onGetThreadList();
        Mockito.verify(view, Mockito.times(1)).onGetThreadList();
    }

    @Test
    @MediumTest
    public void getThreadHistory() {
        presenter.getHistory(20, 0);
        view.onGetThreadHistory();
        Mockito.verify(view, Mockito.times(1)).onGetThreadHistory();
    }

    @Test
    @MediumTest
    public void getContacts() {
        presenter.getContact(20, 0);
        view.onGetContacts();
        Mockito.verify(view, Mockito.times(1)).onGetContacts();
    }

    @Test
    @MediumTest
    public void getThreadParticipant() {
        presenter.getThreadParticipant(10, 0, 352);
        view.onGetThreadParticipant();
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
//        view.onSentMessage();
//        Mockito.verify(view,Mockito.times(1)).onGetSeenMessage();
//        Mockito.doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Thread.sleep(5000);
//                return null;
//            }
//        });
    }

    @Test
    @MediumTest
    public void sendTestMessageOnDeliver() {
        presenter.sendTextMessage("this is test", 381, null);
        view.onSentMessage();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        view.onGetDeliverMessage();
        Mockito.verify(view, Mockito.times(1)).onGetDeliverMessage();
    }

    @Test
    @LargeTest
    public void sendTestMessageOnSeen() {
        presenter.sendTextMessage("this is test", 381, null);
        view.onSentMessage();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        view.onGetSeenMessage();
        Mockito.verify(view, Mockito.times(1)).onGetSeenMessage();
    }


    @Test
    @MediumTest
    public void editMessage() {
        presenter.sendTextMessage("this is test", 381, null);
        view.onSentMessage();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onSentMessage();

        presenter.editMessage(1350, "salam this is edite" + new Date().getTime() + "by" + NAME);
        view.onEditMessage();
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
        Invitee[] invite = new Invitee[]{new Invitee(570, 2),new Invitee(571,2)};
        presenter.createThread(0, invite, "yes");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view,Mockito.times(1)).onCreateThread();
    }


}
