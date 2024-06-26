package top.kkoishi.scmonitor

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import top.kkoishi.scmonitor.api.ServerChats
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    GNUTypeOptions.handleArguments(*args)
    // debug codes
    ServerChats.addServerChatReceiveCallback {
        println("Receive new chat, sender: ${it.first.name}, message: ${it.second}")
    }
    thread {
        HttpServer(Main.port).run()
    }
}

object Main {
    @JvmStatic
    val GSON: Gson = GsonBuilder().setPrettyPrinting().create()

    @JvmStatic
    var port: Int = 5114

    @JvmStatic
    val dataMutableMapType = object : TypeToken<MutableMap<String, String>>() {}
}