package top.kkoishi.scmonitor

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import top.kkoishi.scmonitor.api.ServerChats
import top.kkoishi.scmonitor.website.WSServerData
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    GNUTypeOptions.handleArguments(*args)
    // debug codes
    ServerChats.addServerChatReceiveCallback {
        println("Receive new chat, sender: ${it.first.name}, message: ${it.second}")
    }

    WSServerData.init()
    thread {
        HttpServer(Main.port).run()
    }

    // test code
    ServerChats.addServerChat(
        ServerChats.buildServerChat(
            mapOf(
                "chat_sender" to "Satori_KKoishi",
                "chat_message" to "test",
                "chat_time" to "2024/4/1 11:45:14",
                "chat_server" to "[myf]server_rogue"
            )
        )
    )
    ServerChats.addServerChat(
        ServerChats.buildServerChat(
            mapOf(
                "chat_sender" to "Satori_KKoishi",
                "chat_message" to "测试",
                "chat_time" to "2024/4/1 11:45:14",
                "chat_server" to "[myf]server_noob"
            )
        )
    )
}

object Main {
    @JvmStatic
    val GSON: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .serializeNulls()
        .create()

    @JvmStatic
    var port: Int = 5114

    @JvmStatic
    val dataMutableMapType = object : TypeToken<MutableMap<String, String>>() {}
}
