## Synopsis

**Fanap's POD** Chat service

# Changelog
All notable changes to this project will be documented here.

## Future Version [*] -2018-00-00
-   [Add] Support SSL for Pre Android 4.4 for socket and Request
-   [Add] Implement Cache for Message
-   [Add] Spam
-   [Add] Add project to Maven
-   [BugFix] Sync Contact

##  Version [0.1.2.5] -09/26/2018
-   [BugFix]OnError Listener
-   [BugFix] Bug is fixed when client want to set count for *GetContact*
-   [Add] Async has *ASYNC_READY* state
-   [Add] Add lastMessageId and firstMessageId to the *GetHistory*
-   [Replace] Replace most of the *integer* params to *long*.
-   [Removed] CHAT_READY state was removed from Async and added to Chat and state changes 
     to *CHAT_READY* when respond of the *getUserInfo* is received.
-   [BugFix]  Add some field that added to the respond of the create thread
-   [Add]  Now u can get instance unique id when u send text message.

## Version [0.1.2.4] -2018-09-15
-   [BugFix]OnError Listener
-   [BugFix]Create Thread 


## Version [0.1.2.3] -2018-09-03
-   [Add]Implement Cache for get Contact
-   [Add]Update Thread Info
-   [Add]Get file
-   [Add]Get Image
-   [BugFix]You can get CHAT_READY on Live State


## Version [0.1.2.1] -2018-08-19
-   [Add]Map Routing
-   [Add]Map Search
-   [Add]Block
-   [Add]Unblock
-   [Add]GetBlockList
-   [Add]Search Contact
-   [Add]Search History


## Version [0.0.7.1] -2018-07-30

-   [Add]Delete Message
-   [Add]onThreadInfoUpdated listener
-   [Add]onLastSeenUpdated listener
-   [Add]Search in threads with name:
    We sdd a new param to the getThread so you can search through threads by their name. 
-   [Add]Remove participant
-   [Add]Add participant
-   [Add]Sync Contact listener
-   [Add]onChatState listener

## Version [0.0.7.0] -2018-07-22

-   Check Permission on SendFile and SyncContact 
-   UploadImage 
-   UploadFile 
-   Refactor SyncContact
-   Add Permission Class for request permission and check permission
-   Add FileServer param to Connect 

## Version [0.6.6.0] - 2018-07-18
