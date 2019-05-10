package ir.fanap.chat.sdk.application.asyncexample;


public interface SocketContract {

    interface view {
        void showMessage(String message);

        void messageCalled();

        void showErrorMessage(String error);

        void onStateChanged(String state);

    }

    interface presenter {
        String getMessage();

        void connect(String socketServerAddress, String appId, String serveName, String token,  String ssoHost, String deviceId);

        void sendMessage(String textMessage, int messageType, long[] receiversId);

        void sendMessage(String textMessage, int messageType);

        void getErrorMessage();

        void closeSocket();

        boolean isSocketOpen();

        String getPeerId();

        void socketLogOut();
    }
}
