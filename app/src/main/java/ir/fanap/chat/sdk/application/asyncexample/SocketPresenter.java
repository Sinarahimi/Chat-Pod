package ir.fanap.chat.sdk.application.asyncexample;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.fanap.podasync.Async;
import com.fanap.podasync.AsyncAdapter;

public class SocketPresenter extends AsyncAdapter implements SocketContract.presenter {

    private Async async;
    private SocketContract.view view;

    public SocketPresenter(SocketContract.view view, Context context) {
        this.view = view;
        async = Async.getInstance(context);
        async.isLoggable(true);
        async.addListener(this);
    }

    @Override
    public String getMessage() {
        String message = async.getMessage();
        view.showMessage(message);
        view.messageCalled();
        return message;
    }

    @Override
    public void connect(String socketServerAddress, String appId, String serverName
            , String token, String ssoHost, String deviceId) {
        async.connect(socketServerAddress, appId, serverName, token, ssoHost,deviceId);
    }

    @Override
    public void sendMessage(String textMessage, int messageType, long[] receiversId) {
        try {
            async.sendMessage(textMessage, messageType, receiversId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String textMessage, int messageType) {
        try {
            async.sendMessage(textMessage, messageType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getLiveState() {
        async.getStateLiveData();
    }

    @Override
    public String getState() {
        return async.getState();
    }

    @Override
    public boolean isSocketOpen() {
        boolean isSocketOpen = false;
        if ((async.getState()) != null) {
            if (async.getState().equals("OPEN")) {
                isSocketOpen = true;
            }
        }
        return isSocketOpen;
    }

    @Override
    public String getPeerId() {
        return async.getPeerId();
    }

    @Override
    public void socketLogOut() {
        async.logOut();
    }

    @Override
    public LiveData<String> getLiveData() {
        return async.getStateLiveData();
    }

    @Override
    public void getErrorMessage() {
        String error = async.getErrorMessage();
        view.showErrorMessage(error);
    }

    @Override
    public void closeSocket() {
        async.closeSocket();
    }
}
