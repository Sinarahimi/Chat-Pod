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

|Num|Method                        | Description                                                                            |
|:--|:------------------------------|:---------------------------------------------------------------------------------------|
|1|`connect(socketServerAddress, appId, serverName, token`   | connect to async.       |
|2|`String ssoHost, String platformHost, String fileServer)`
|3|`logOutSocket()`                                           | log out of socket.      |
|4|`sendTextMessage(String textMessage, long threadId, String metaData)`       | Send text message to thread.           |
|5|`renameThread(long threadId, String title)`                |  Rename the owner thread.                |
|6|`createThread(int threadType, Invitee[] invitee, String threadTitle)`                |  Create the thread.                |
|7|`forwardMessage(long threadId, ArrayList<Long> messageIds)`                 | Forward the message or messages.        |
|8|`replyMessage(String messageContent, long threadId, long messageId)`         | Reply the message in the thread       |
|9|`editMessage(int messageId, String messageContent)`         | Edit the message      |
|10|`getThreads(int count, int offset, ArrayList<Integer> threadIds, String threadName)`         | gets the list of thread       |
|11|`getHistory(int count, int offset, String order, long threadId)`         | get the history of the specific thread       |
|12|`getContacts(int count, int offset)`         | get contact list      |
|13|`removeContact(long userId)`         | remove user in contact list      |
|14|`updateContact(String userId,String firstName, String lastName, String cellphoneNumber, String email)`| update user info in contact list      |
|15|`removeContact(long userId)`         | remove user in contact list      |
|16|`addContact(String firstName, String lastName, String cellphoneNumber, String email)`         | Add contact      |
|17|`getThreadParticipants(int count, int offset, long threadId)`         | Get the participant list      |
|18|`getUserInfo()`         | Get the information of the current user      |
|19|`muteThread(int threadId)`         | Mute the thread      |
|20|`unmuteThread(int threadId)`         | Un Mute the thread      |
|21|`sendFileMessage(Context context, String description, long threadId, Uri fileUri, String metadata)`         | Send file      |
|22|`syncContact(Context context, Activity activity)`         | Sync Contact      |
|23|`uploadFile(Context context, Activity activity, String fileUri, Uri uri)`         | Upload file      |
|24|`uploadImage(Context context, Activity activity, Uri fileUri)`         | Upload image      |
|25|`deleteMessage(long messageId, Boolean deleteForAll)`         |      |
|26|`addParticipants(long threadId, List<Long> contactIds)`         |      |
|27|`removeParticipants(long threadId, List<Long> participantIds)`         |      |
|28|`leaveThread(long threadId)`         |      |
|29|`block(Long contactId)`         |      |
|30|`unblock(long blockId)`         |      |
|31|`getBlockList(Integer count, Integer offset)`         |      |
|32|`mapSearch(String searchTerm, Double latitude, Double longitude)`         |      |
|33|`mapRouting(String origin, String destination)`         |      |
|34|`searchContact(SearchContact searchContact)`         |      |

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
|Num|Method                        | Description                                                                            |
|:--|:------------------------------|:---------------------------------------------------------------------------------------|
|1| `onDeliver()`   | Called when message is delivered.       |
|2| `onGetContacts()`| Called when get contacts is return.      |
|3| `onGetHistory()`| Called when history of the thread is return.      |
|4| `onGetThread()`       |  Called when get threads is return.          |
|5| `onSeen()`                |  Called when message is seen.               |
|6| `onMuteThread()`                 |Called when thread is muted.         |
|7| `onUnmuteThread()`         | Called when message is un muted.      |
|8| `onUserInfo()`         | Called when information of the user is return.     |
|9| `onSent()`         | Called when message is sent.       |
|10| `onCreateThread()`         |Called when thread is created.         |
|11| `onGetThreadParticipant()`         |Called when you want participants of the specific thread.         |
|12| `onEditedMessage()`         |Called when message edited       |
|13| `onContactAdded()` |Called when contact added to your contact       |
|14| `onRemoveContact()`         |Called when you want to remove contact       |
|15| `onRenameThread()`         |Called when you rename of the thread that you are admin of that       |
|16| `onSyncContact()`         |Called your phone contact sync to the server contact       |
|17| `onThreadAddParticipant()`         |       |
|18| `onThreadRemoveParticipant()`         |       |
|19| `onThreadLeaveParticipant()`         |       |
|20| `onDeleteMessage()`         |       |
|21| `onError()`         |       |
|22| `onThreadInfoUpdated()`         |       |
|23| `onNewMessage()`         |       |
|24| `onUpdateContact()`         |       |
|25| `onUploadFile()`         |       |
|26| `onUploadImageFile()`         |       |
|27| `onChatState()`         |       |
|28| `onMapSearch()`         |       |
|29| `onMapRouting()`         |       |
|30| `onGetBlockList()`         |       |
|31| `onBlock()`         |       |
|32| `onUnBlock()`         |       |
|33| `void onSearchContact()`         |       |

## Built With :heart:

* [moshi](https://github.com/square/moshi) - Moshi
* [websocket-client](https://github.com/TakahikoKawasaki/nv-websocket-client) - Websocket
* [lifecycle](https://developer.android.com/reference/android/arch/lifecycle/LiveData) - LiveData
* [Retrofit2](https://square.github.io/retrofit/) - Retrofit2
* [Rxjava](https://github.com/ReactiveX/RxAndroid) - Rxjava

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
