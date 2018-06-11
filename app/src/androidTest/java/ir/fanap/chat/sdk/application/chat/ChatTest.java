package ir.fanap.chat.sdk.application.chat;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ChatTest {

    private static ChatContract.presenter presenter;

    @Rule
    public ActivityTestRule<ChatActivity> mActivityRule = new ActivityTestRule<>(ChatActivity.class);

    @BeforeClass
    public static void setUp() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        presenter = new ChatPresenter(appContext);
    }
    @SmallTest
    public void getThread(){
        presenter.connect("ws://172.16.106.26:8003/ws", "POD-Chat",
                "chat-server","a11768091eac48f2a7b84ed6a241f9c3","http://172.16.110.76");
        presenter.getThread(50,0);
    }

    @SmallTest
    public void getThreadHistory(){
        presenter.connect("ws://172.16.106.26:8003/ws", "POD-Chat",
                "chat-server","a11768091eac48f2a7b84ed6a241f9c3","http://172.16.110.76");
        presenter.getThread(50,0);
    }

    @SmallTest
    public void getThreadParticipant(){

    }

}
