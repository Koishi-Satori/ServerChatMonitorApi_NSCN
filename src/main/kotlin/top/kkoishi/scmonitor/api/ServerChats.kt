package top.kkoishi.scmonitor.api

/**
 * A server chat.
 */
typealias ServerChat = Pair<ServerChats.Player, ServerChats.Message>

object ServerChats {
    data class Player(val name: String, val externalData: Array<Any>) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Player

            if (name != other.name) return false
            if (!externalData.contentEquals(other.externalData)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + externalData.contentHashCode()
            return result
        }
    }

    /**
     * The chat message.
     */
    data class Message(val content: String, val timeStamp: String, val sourceServerID: String = "UNKNOWN") {
        override fun toString(): String {
            return "[$timeStamp] $content"
        }
    }

    /**
     * Server chat message cache, max 64.
     */
    @JvmStatic
    private val cachedData = ArrayDeque<ServerChat>(64)

    @JvmStatic
    private val receiveCallbacks = ArrayDeque<(ServerChat) -> Unit>()

    @JvmStatic
    private val removeCallbacks = ArrayDeque<(ServerChat) -> Unit>()

    @JvmStatic
    fun addServerChat(chat: ServerChat) {
        while (cachedData.size >= 64) {
            val removed = cachedData.removeFirst()
            removeCallbacks.forEach { it(removed) }
        }
        receiveCallbacks.forEach { it(chat) }
        cachedData.addLast(chat)
    }

    @JvmStatic
    fun addServerChatReceiveCallback(callback: (ServerChat) -> Unit) {
        receiveCallbacks.addLast(callback)
    }

    @JvmStatic
    fun addServerChatRemoveCallback(callback: (ServerChat) -> Unit) {
        removeCallbacks.addLast(callback)
    }

    @JvmStatic
    fun buildServerChat(data: Map<String, String>): ServerChat {
        val playerName = data["chat_sender"]
        val content = data["chat_message"]
        val time = data["chat_time"]
        val server = data["chat_server"]
        if (playerName == null || content == null || time == null)
            throw RuntimeException("find empty fields.")
        return if (server != null)
            Player(playerName, emptyArray()) to Message(content, time, server)
        else
            Player(playerName, emptyArray()) to Message(content, time)
    }
}
