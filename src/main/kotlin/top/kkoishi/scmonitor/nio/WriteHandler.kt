package top.kkoishi.scmonitor.nio

import com.google.gson.JsonParseException
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.*
import top.kkoishi.scmonitor.Main
import top.kkoishi.scmonitor.api.ServerChats
import java.io.IOException
import java.lang.Exception

object WriteHandler : HttpRequestHandler() {
    override fun readHttpRequest(
        ctx: ChannelHandlerContext,
        msg: HttpRequest
    ): Pair<ByteBuf, HttpResponseStatus> =
        if (msg.method() == HttpMethod.POST)
            readPost(msg)
        else
            Unpooled.copiedBuffer(
                "bad request method: this should not happen",
                Charsets.UTF_8
            ) to HttpResponseStatus.BAD_REQUEST

    override fun verifyRequest(uri: String, method: HttpMethod): Boolean =
        uri.startsWith("/new_data") && (method == HttpMethod.POST)

    private fun readPost(msg: HttpRequest): Pair<ByteBuf, HttpResponseStatus> {
        val contentType = msg.headers().get("Content-Type").trim().lowercase()
        if (contentType.contains("json")) {
            if (msg is FullHttpRequest) {
                return try {
                    val content = msg.content().toString(Charsets.UTF_8)
                    val data = Main.GSON.fromJson(content, Main.dataMutableMapType)
                    ServerChats.addServerChat(ServerChats.buildServerChat(data))
                    Unpooled.copiedBuffer("ok", Charsets.UTF_8) to HttpResponseStatus.OK
                } catch (je: JsonParseException) {
                    je.printStackTrace()
                    Unpooled.copiedBuffer("bad json", Charsets.UTF_8) to HttpResponseStatus.BAD_REQUEST
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                    Unpooled.copiedBuffer("io error", Charsets.UTF_8) to HttpResponseStatus.INSUFFICIENT_STORAGE
                } catch (e: Exception) {
                    e.printStackTrace()
                    Unpooled.copiedBuffer("internal error", Charsets.UTF_8) to HttpResponseStatus.INTERNAL_SERVER_ERROR
                }
            } else
                return Unpooled.copiedBuffer("non-full content", Charsets.UTF_8) to HttpResponseStatus.ACCEPTED
        } else
            return Unpooled.copiedBuffer(
                "bad request content: requires json type.",
                Charsets.UTF_8
            ) to HttpResponseStatus.BAD_REQUEST
    }
}
