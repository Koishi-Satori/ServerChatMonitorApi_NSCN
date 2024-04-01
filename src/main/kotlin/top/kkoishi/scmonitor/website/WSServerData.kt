package top.kkoishi.scmonitor.website

import top.kkoishi.scmonitor.api.ServerChats

object WSServerData {
    class SingleChat(val serverId: String, val chat: String, val time: String, val sender: String)

    private class Message(val message: String, val time: String, val sender: String)

    @JvmStatic
    private var initialed = false

    @JvmStatic
    private val data = HashMap<String, ArrayDeque<Message>>()

    @JvmStatic
    private fun messageToString(
        player: ServerChats.Player,
        message: ServerChats.Message,
    ): String {
        return "[${message.timeStamp}]${player.name}: ${message.content}"
    }

    @JvmStatic
    fun all(): Array<SingleChat> {
        val result: ArrayDeque<SingleChat>
        synchronized(data) {
            result = ArrayDeque(data.size)
            data.keys.forEach { key ->
                result.addAll(data[key]!!.map { SingleChat(key, it.message, it.time, it.sender) })
            }
        }
        val temp = result.toTypedArray()
        result.clear()
        return temp
    }

    @JvmStatic
    fun servers(): Array<String> = synchronized(data) {
        return data.keys.toTypedArray()
    }

    @JvmStatic
    fun init() {
        if (!initialed) {
            ServerChats.addServerChatReceiveCallback {
                val player = it.first
                val message = it.second
                var serverChats = data[message.sourceServerID]
                if (serverChats == null) {
                    serverChats = ArrayDeque<Message>(1000)
                    data[message.sourceServerID] = serverChats
                }
                while (serverChats.size >= 1000)
                    serverChats.removeFirst()
                serverChats.addLast(Message(message.content, message.timeStamp, player.name))
            }
        }
        initialed = true
    }
}
