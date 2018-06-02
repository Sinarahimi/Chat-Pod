# Chat-Pod
A Chat helepr that works with Fanap's POD Chat service.

### Prerequisites

What things you need to Add this module to your project and after that set the `internet` permision in the manifest.
```
<uses-permission android:name="android.permission.INTERNET" />
```

The first step is to init Chat.
```
chat.init(context);
```
Then you need to connect.
```
chat.connect(serverAddress, appId, severName, token);
```
And now it ready for chat .

#The table below is the list of  methods defined in Chat 

| Method                        | Description                                          |
|:------------------------------|:-----------------------------------------------------|
| `connect(socketServerAddress, appId, serverName, token)`         | Called when we want to connect to async. |
| `sendTextMessage(String textMessage, long threadId)`               | Called when we want to send a message.             |
| `getThread(int count, int offset)`             | gets the thread.           |
| `getHistory(int count, int offset, String order, long threadId)`                |  gets the history of the thread.                |
| `getContacts(int count, int offset)`                 | gets the contacts.        |
| `getPeerId()`         | gets the peer id       |
| `getMessage`         | gets message       |
