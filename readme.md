## Server Chat Monitor API
-----

* This API can be used to forward the chat messages from the NorstStar Game Server to other backend platforms.
* Such as: Chatbots, websites and so on.

### Convars
* You must edit the Convars in the mod.json.
* Try to make sure the LOCAL_SERVER_ID is the ONLY server id, this can be used to differ different servers.

### Deployment
* After calling the API, packaging the jar file and deploy it as a backend server, and then packaging the ```sq_mod``` directory as a NorthStar Game Server mod.
* You need to have JDK 17 or later installed on the server.
* To ensure stability, you can write a script to automatically restart the service.
