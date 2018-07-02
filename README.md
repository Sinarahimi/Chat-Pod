# Chat-Pod
A Chat helepr that works with Fanap's POD Chat service.

### Prerequisites

What things you need to Add this module to your project and after that set the `internet` permision in the manifest.
```java
<uses-permission android:name="android.permission.INTERNET" />
```

The first step is to init Chat.
```java
chat.init(context);
```
Then you need to connect.
```java
chat.connect("ws://172.16.106.26:8003/ws",
                "POD-Chat", "chat-server", TOKEN, "http://172.16.110.76",
                "http://172.16.106.26:8080")
```
And now it ready for chat .

#The table below is the list of  methods defined in Chat 

| Method                        | Description                                                                            |
|:------------------------------|:---------------------------------------------------------------------------------------|
| `connect(socketServerAddress, appId, serverName, token`   | connect to async.       |
| `String ssoHost, String platformHost)`
| `logOutSocket()`                                           | log out of socket.      |
| `sendTextMessage(String textMessage, long threadId)`       | Send text message to thread.           |
| `renameThread(long threadId, String title)`                |  Rename the owner thread.                |
| `createThread(int threadType, Invitee[] invitee, String threadTitle)`                |  Create the thread.                |
| `forwardMessage(long threadId, ArrayList<Long> messageIds)`                 | Forward the message or messages.        |
| `replyMessage(String messageContent, long threadId, long messageId)`         | Reply the message in the thread       |
| `editMessage(int messageId, String messageContent)`         | Edit the message      |
| `getThreads(int count, int offset, ArrayList<Integer> threadIds)`         | gets the list of thread       |
| `getHistory(int count, int offset, String order, long threadId)`         | get the history of the specific thread       |
| `getContacts(int count, int offset)`         | get contact list      |
| `removeContact(long userId)`         | remove user in contact list      |
| `updateContact(String firstName, String lastName, String cellphoneNumber, String email)` | update user info in contact list      |
| `removeContact(long userId)`         | remove user in contact list      |
| `addContact(String firstName, String lastName, String cellphoneNumber, String email)`         | Add contact      |
| `getThreadParticipant(int count, int offset, long threadId)`         | Get the participant list      |
| `getUserInfo()`         | Get the information of the current user      |
| `muteThread(int threadId)`         | Mute the thread      |
| `unmuteThread(int threadId)`         | Un Mute the thread      |


###getUserInfo

###createThread

###getThreads

###getHistory

###getThreadParticipant

###renameThread

###muteThread

###unmuteThread

###sendTextMessage

###forwardMessage

###replyMessage

###editMessage

###getContacts

###removeContact

###addContact

for getting call back you should extend your class from `ChatAdapter` 
