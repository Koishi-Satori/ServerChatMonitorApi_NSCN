untyped
global function server_chat_monitor_init

string REMOTE_SERVER_URL = "127.0.0.1"
string REMOTE_SERVER_PORT = "5114"
string REMOTE_SERVER_POST_ENDFIX = "new_data"
string REMOTE_SERVER_GET_ENDFIX = "heart_beat"
string LOCAL_SERVER_ID = "INVALID"

// TODO: use this to get custom player data.
//string functionref(entity) playerDataFunc

void function server_chat_monitor_init() {
    #if SERVER
    AddCallback_OnReceivedSayTextMessage(server_chat_monitor_impl)
    #endif
}

void function server_chat_monitor_loadConvars() {
    REMOTE_SERVER_URL = GetConVarString("REMOTE_SERVER_URL")
    REMOTE_SERVER_PORT = GetConVarString("REMOTE_SERVER_PORT")
    REMOTE_SERVER_POST_ENDFIX = GetConVarString("REMOTE_SERVER_POST_ENDFIX")
    REMOTE_SERVER_GET_ENDFIX = GetConVarString("REMOTE_SERVER_GET_ENDFIX")
    LOCAL_SERVER_ID = GetConVarString("LOCAL_SERVER_ID")
}

ClServer_MessageStruct function server_chat_monitor_impl(ClServer_MessageStruct message) {
    server_chat_monitor_loadConvars()
    if (LOCAL_SERVER_ID == "INVALID")
        return message
    entity player = message.player
    string content = message.message
    string playerName = player.GetPlayerName()

    // init POST data
    table data = {}
    data["chat_sender"] <- playerName
    data["chat_message"] <- content
    data["chat_time"] <- server_chat_monitor_GetFormattedTime()
    data["chat_server"] <- LOCAL_SERVER_ID


    // send POST response
    string serverURL = REMOTE_SERVER_URL + ":" + REMOTE_SERVER_PORT
    string postURL = serverURL + "/" + REMOTE_SERVER_POST_ENDFIX
    string getURL = serverURL + "/" + REMOTE_SERVER_GET_ENDFIX
    SqHttp_SendPostRequest(postURL, data)
    return message
}

table<string, string> function server_chat_monitor_GetTimeTable() {
    // year month day hour minute second
    table timeParts = GetUnixTimeParts( GetUnixTimestamp() + 8 * SECONDS_PER_HOUR )
	table<string,string> timeTable = {}
	foreach( k, v in timeParts )
	{
		timeTable[ string( k ) ] <- string( v )
		if( v < 10 )
			timeTable[ string( k ) ] = "0"+ string( v )
	}
    return timeTable
}

string function server_chat_monitor_GetFormattedTime() {
    table<string, string> times = server_chat_monitor_GetTimeTable()
    return times["year"] + "/" + times["month"] + "/" + times["day"] + " " + times["hour"] + ":" + times["minute"] + ":" + times["second"]
}
