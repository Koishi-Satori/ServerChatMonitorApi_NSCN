package top.kkoishi.scmonitor.website

import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import top.kkoishi.scmonitor.Main
import top.kkoishi.scmonitor.nio.HttpRequestHandler
import java.io.IOException
import kotlin.io.path.Path
import kotlin.io.path.readText

object WSChatsHandler : HttpRequestHandler() {
    @JvmStatic
    private val typeToken = object : TypeToken<HashMap<String, HashMap<String, String>>>() {}

    @JvmStatic
    fun parseServerName(id: String): String {
        val data = Main.GSON.fromJson(Path("./data.json").readText(), typeToken)
        val serverNames = data["server_names"] ?: return "ERROR"
        return serverNames.getOrDefault(id, "NOT_MAPPED_ID")
    }

    override fun readHttpRequest(ctx: ChannelHandlerContext, msg: HttpRequest): Pair<ByteBuf, HttpResponseStatus> {
        val ids = WSServerData.servers()
        val names = ids.map { it to parseServerName(it) }
        val chats = WSServerData.all()
        return try {
            Unpooled.copiedBuffer(
                Main.GSON.toJson(mapOf("server_name" to names, "server_chat" to chats)),
                Charsets.UTF_8
            ) to HttpResponseStatus.OK
        } catch (jpe: JsonParseException) {
            jpe.printStackTrace()
            Unpooled.copiedBuffer(
                "failed to parse json",
                Charsets.UTF_8
            ) to HttpResponseStatus.EXPECTATION_FAILED
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            Unpooled.copiedBuffer(
                "io error",
                Charsets.UTF_8
            ) to HttpResponseStatus.GATEWAY_TIMEOUT
        } catch (e: Exception) {
            e.printStackTrace()
            Unpooled.copiedBuffer(
                "internal error",
                Charsets.UTF_8
            ) to HttpResponseStatus.INTERNAL_SERVER_ERROR
        }
    }

    override fun verifyRequest(uri: String, method: HttpMethod): Boolean {
        return super.verifyRequest(uri, method) && uri.startsWith("/chats")
    }
}
