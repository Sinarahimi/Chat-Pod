# Chat-Pod
A Chat helepr that works with Fanap's POD Chat service.

### Prerequisites

You need to Add this module to your project and after that set the `internet` permission in the manifest.
```java
<uses-permission android:name="android.permission.INTERNET" />
```
There is two another permission you need to add to your manifest 
```java
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```
The first step is to initialize the Chat module.

```java
chat.init(context);
```
Then you need to connect.
```java
chat.connect("ws://172.16.106.26:8003/ws",
                "POD-Chat", "chat-server", TOKEN, "http://172.16.110.76",
                "http://172.16.106.26:8080","http://172.16.106.26:8080")

```
And now it's ready for chat .

| Method                        | Description                                                                            |
|:------------------------------|:---------------------------------------------------------------------------------------|
| `connect(socketServerAddress, appId, serverName, token`   | connect to async.       |
| `String ssoHost, String platformHost, String fileServer)`
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
| `updateContact(String userId,String firstName, String lastName, String cellphoneNumber, String email)`| update user info in contact list      |
| `removeContact(long userId)`         | remove user in contact list      |
| `addContact(String firstName, String lastName, String cellphoneNumber, String email)`         | Add contact      |
| `getThreadParticipants(int count, int offset, long threadId)`         | Get the participant list      |
| `getUserInfo()`         | Get the information of the current user      |
| `muteThread(int threadId)`         | Mute the thread      |
| `unmuteThread(int threadId)`         | Un Mute the thread      |
| `sendFile(Context context, String description, long threadId, Uri fileUri)`         | Send file      |
| `syncContact(Context context, Activity activity)`         | Sync Contact      |
| `uploadFile(Context context, Activity activity, String fileUri, Uri uri)`         | Upload file      |
| `uploadImage(Context context, Activity activity, Uri fileUri)`         | Upload image      |
| `deleteMessage(long messageId, Boolean deleteForAll)`         |      |
| `addParticipants(long threadId, List<Long> contactIds)`         |      |
| `removeParticipants(long threadId, List<Long> participantIds)`         |      |
| `leaveThread(long threadId)`         |      |

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
### syncContact
```java
chat.syncContact(this, this);
```


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
chat.getThreadParticipants(50, 5, 235);
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
chat.replyMessage("Reply to the text", 235, 532);
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

#### The following callback methods of ChatListener are called
| Method                        | Description                                                                            |
|:------------------------------|:---------------------------------------------------------------------------------------|
| `onDeliver()`   | Called when message is delivered.       |
| `onGetContacts()`| Called when get history of the thread is return.      |
| `onGetThread()`       |  Called when get threads is return.          |
| `onInvitation()`                |                 |
| `onSeen()`                |  Called when message is seen.               |
| `onMuteThread()`                 |Called when thread is muted.         |
| `onUnmuteThread()`         | Called when message is un muted.      |
| `onUserInfo()`         | Called when information of the user is return.     |
| `onSent()`         | Called when message is sent.       |
| `onCreateThread()`         |Called when thread is created.         |
| `onGetThreadParticipant()`         |Called when you want participants of the specific thread.         |
| `onEditedMessage()`         |Called when message edited       |
| `onContactAdded()` |Called when contact added to your contact       |
| `onRemoveContact()`         |Called when you want to remove contact       |
| `onRenameThread()`         |Called when you rename of the thread that you are admin of that       |
| `onSyncContact()`         |Called your phone contact sync to the server contact       |
| `onThreadAddParticipant()`         |       |
| `onThreadRemoveParticipant()`         |       |
| `onThreadLeaveParticipant()`         |       |
| `onDeleteMessage()`         |       |

## Built With :heart:

* [moshi](https://github.com/square/moshi) - Moshi
* [websocket-client](https://github.com/TakahikoKawasaki/nv-websocket-client) - Websocket
* [lifecycle](https://developer.android.com/reference/android/arch/lifecycle/LiveData) - LiveData
* [Retrofit2](https://square.github.io/retrofit/) - Retrofit2
* [Rxjava](https://github.com/ReactiveX/RxAndroid) - Rxjava

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

