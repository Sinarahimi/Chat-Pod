package ir.fanap.chat.sdk.application.chat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChatPresenterTest {
    private ChatContract.presenter presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new ChatPresenter();
    }

    @Test
    public void connect() {

    }

    @Test
    public void getThread() {
        presenter.getThread(50,0);
        Assert.assertEquals(1,1);
    }

    @Test
    public void getHistory() {
    }

    @Test
    public void createThread() {
    }

    @Test
    public void sendTextMessage() {
    }
}