package ir.fanap.chat.sdk.application;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ir.fanap.chat.sdk.application.asyncexample.SocketContract;
import ir.fanap.chat.sdk.application.asyncexample.SocketPresenter;

public class SocketPresenterTest {

    @Mock
    private SocketContract.view view;

    private SocketPresenter socketPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
//        socketPresenter = new SocketPresenter(view,)
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getMessage() {

//        SocketPresenter socketPresenter = new SocketPresenter("","");
//        Assert.assertEquals("",socketPresenter.getMessage());
    }

    @Test
    public void sendMessage() {
        socketPresenter.sendMessage("this is test", 3);

    }

    @Test
    public void getErrorMessage() {
    }

    @Test
    public void getState() {

    }


}