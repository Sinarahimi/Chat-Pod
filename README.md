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


### getUserInfo
```java
chat.getUserInfo();
```

### createThread
```java
Invitee[] invite = new Invitee[]{new Invitee(textThread, 2)};
chat.createThread(0, invite, "");
```

### getThreads
```java
chat.getThread(10, 0, [235,589]);
chat.getThread(10, 0, null);
```

### getHistory
```java
presenter.getHistory(50, 0, null, 312);
presenter.getHistory(50, 0, "desc", 312);
```

### getThreadParticipant
```java
chat.getThreadParticipant(50, 5, 235);
```

### renameThread
```java
chat.renameThread(379, "new group name");
```

### muteThread
```java
chat.muteThread(232);
```

### unmuteThread
```java
chat.unmuteThread(232);
```

### sendTextMessage
```java
chat.sendTextMessage("This is test", 235);
```

### forwardMessage
```java
ArrayList<Long> messageIds = new ArrayList<>();
messageIds.add(11956L);
chat.forwardMessage(312, messageIds);
```

### replyMessage
```java
chat.sendReplyMessage("Reply to the text", 235, 532);
```

### editMessage
```java
chat.editMessage(533, "edited_at" + new Date().getTime());
```

### getContacts
```java
chat.getContact(50,0);
```

### removeContact
```java
chat.removeContact(long userId)
```

### addContact
```java
chat.addContact("Sina", "Rahimi", "0912131", "Develop.rahimi95@gmail.com");
```
### Register Listener
After creating a Chat instance, you should call addListener method to register a ChatListener that receives Chat events.
ChatAdapter is an empty implementation of ChatListener interface.
For getting call back you should extend your class from `ChatAdapter`. 

#### The following callack methods of ChatListener are called
| Method                        | Description                                                                            |
|:------------------------------|:---------------------------------------------------------------------------------------|
| `onDeliver()`   | Called when message was deliverd.       |
| `onGetContacts()`| Called when get contact respons was return. |
| `onGetHistory()`                                           |       |
| `onGetThread()`       |            |
| `onInvitation()`                |                 |
| `onSeen()`                |                 |
| `onMuteThread()`                 |         |
| `onUnmuteThread()`         |        |
| `onUserInfo()`         |      |
| `onSent()`         |        |
| `onCreateThread()`         |        |
| `onGetThreadParticipant()`         |       |
| `onEditedMessage()`         |       |
| `onContactAdded()` |       |
| `onRemoveContact()`         |       |
| `onRenameThread()`         |       |

