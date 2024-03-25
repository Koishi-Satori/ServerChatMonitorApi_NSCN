untyped
global function server_chat_monitor_init

const string REMOTE_SERVER_URL = "127.0.0.1"
const string REMOTE_SERVER_PORT = "5114"
const string REMOTE_SERVER_POST_ENDFIX = "new_data"
const string REMOTE_SERVER_GET_ENDFIX = "heart_beat"

// TODO: use this to get custom player data.
//string functionref(entity) playerDataFunc

void function server_chat_monitor_init() {
    AddCallback_OnReceivedSayTextMessage(server_chat_monitor_impl)

    // TODO: read remote server heart beat
}

ClClient_MessageStruct function server_chat_monitor_impl(ClClient_MessageStruct message) {
    entity player = message.player
    string content = message.message
    string playerName = message.playerName

    // init POST data
    table data = {}
    data["chat_sender"] <- playerName
    data["chat_message"] <- content

    // send POST response
    string serverURL = REMOTE_SERVER_URL + ":" + REMOTE_SERVER_PORT
    string postURL = serverURL + "/" + REMOTE_SERVER_POST_ENDFIX
    string getURL = serverURL + "/" + REMOTE_SERVER_GET_ENDFIX
    SqHttp_SendPostRequest(postURL, data)
}
