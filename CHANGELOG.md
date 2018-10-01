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
-   [BugFix] OnError Listener's bug has been fixed.
-   [BugFix] The bug of setting `count` parameter in `GetContact` function has been fixed.
-   [Add] Async has *ASYNC_READY* state now.
-   [Add] Add `lastMessageId` and `firstMessageId` to the *GetHistory*
-   [Replace] Replace most of the *integer* params to *long*.
-   [Removed] CHAT_READY state removed from Async and added to Chat and state changes 
     to *CHAT_READY* when response of the *getUserInfo* arrives.
-   [BugFix]  Some fields has beed added to `createThread`'s response
-   [Add]  Now you can get instant unique id when you send text messages
-   [Add]  If you want to disable/hide logs for output set *isLoggable* false.
-   [Add]  If you want to disable *cache* set *cache* attribute as false.
-   [Deprecated] The `renameThread` is going to deprecated in the next version.

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