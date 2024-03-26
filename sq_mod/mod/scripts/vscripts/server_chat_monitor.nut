untyped
global function server_chat_monitor_init

const string REMOTE_SERVER_URL = "127.0.0.1"
const string REMOTE_SERVER_PORT = "5114"
const string REMOTE_SERVER_POST_ENDFIX = "new_data"
const string REMOTE_SERVER_GET_ENDFIX = "heart_beat"

// TODO: use this to get custom player data.
//string functionref(entity) playerDataFunc

void function server_chat_monitor_init() {
    #if SERVER
    AddCallback_OnReceivedSayTextMessage(server_chat_monitor_impl)
    #endif
}

ClServer_MessageStruct function server_chat_monitor_impl(ClServer_MessageStruct message) {
    entity player = message.player
    string content = message.message
    string playerName = player.GetPlayerName()

    // init POST data
    table data = {}
    data["chat_sender"] <- playerName
    data["chat_message"] <- content
    data["chat_time"] <- server_chat_monitor_GetFormattedTime()


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
