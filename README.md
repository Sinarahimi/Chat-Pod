
# Pod-Async
A webSocket helepr that works with Fanap's POD Async service (DIRANA)

## Getting Started

This library allows you to connect to **Pod-Async** and use their services.

### Prerequisites

What things you need to Add this module to your project and after that set the `internet` permision in the manifest.

```
<uses-permission android:name="android.permission.INTERNET" />
```

 The first step is to create Async instance.


``` @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Async async = Async.getInstance(context);
```

# The table below is the list of  methods defined in Async class

| Method                        | Description                                          |
|:------------------------------|:-----------------------------------------------------|
| `connect(socketServerAddress, appId, serverName, token)`         | Called when we want to connect to async. |
| `sendMessage(String textMessage, int messageType)`               | Called when we want to send a message.             |
| `getLiveState()` (Deprecated)            | gets the state of the socket.           |
| `getState()`                |  gets the state of the socket.                |
| `getErrorMessage()`                 | gets the error of the async.        |
| `getPeerId()`         | gets the peer id       |
| `getMessage`         | gets message       |


## Register Listener
After creating a Async instance, you should call addListener method to register a AsyncListener that receives Async events
. AsyncAdapter is an empty implementation of AsyncListener interface.

```
// Register a listener to receive Async events.
  async.addListener(new AsyncAdapter(){
             @Override
             public void onReceivedMessage(String textMessage) throws IOException {
                 super.onReceivedMessage(textMessage);
                         // Received a text message.
                         ...
             }
         });
 ```


## Built With

* [websocket-client](https://github.com/TakahikoKawasaki/nv-websocket-client) - Websocket

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags).

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details


