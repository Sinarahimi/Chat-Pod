package ir.fanap.chat.sdk.application.chatexample;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.chat.ChatListener;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class ChatTest extends ChatAdapter {

    private static ChatContract.presenter presenter;
    @Mock
    private static ChatContract.view view;

    @Rule
    public ActivityTestRule<ChatActivity> mActivityRule = new ActivityTestRule<>(ChatActivity.class);

    private static Chat chat;
    private static String TOKEN = "bebc31c4ead6458c90b607496dae25c6";

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        MockitoAnnotations.initMocks(this);
        chat = Chat.init(appContext).addListener(this);
        presenter = Mockito.spy(new ChatPresenter(appContext,view));
        chat.connect("ws://172.16.106.26:8003/ws",
                "POD-Chat", "chat-server", TOKEN, "http://172.16.110.76",
                "http://172.16.106.26:8080/hamsam/");
    }

    @Test
    @MediumTest
    public void getUserInfo() {
        presenter.getUserInfo();
        view.onGetUserInfo();
        Mockito.verify(view,Mockito.times(1)).onGetUserInfo();
    }

}
