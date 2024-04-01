package top.kkoishi.scmonitor.website

import com.google.gson.JsonParseException
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import top.kkoishi.scmonitor.Main
import top.kkoishi.scmonitor.nio.HttpRequestHandler
import java.io.IOException

object WSServersHandler : HttpRequestHandler() {
    override fun readHttpRequest(ctx: ChannelHandlerContext, msg: HttpRequest): Pair<ByteBuf, HttpResponseStatus> {
        val ids = WSServerData.servers()
        return try {
            Unpooled.copiedBuffer(
                Main.GSON.toJson(
                    mapOf(
                        "servers" to ids.map { it to WSChatsHandler.parseServerName(it) }
                    )
                ), Charsets.UTF_8
            ) to HttpResponseStatus.OK
        } catch (jpe: JsonParseException) {
            Unpooled.copiedBuffer(
                "failed to parse json",
                Charsets.UTF_8
            ) to HttpResponseStatus.EXPECTATION_FAILED
        } catch (ioe: IOException) {
            Unpooled.copiedBuffer(
                "io error",
                Charsets.UTF_8
            ) to HttpResponseStatus.GATEWAY_TIMEOUT
        } catch (e: Exception) {
            Unpooled.copiedBuffer(
                "internal error",
                Charsets.UTF_8
            ) to HttpResponseStatus.INTERNAL_SERVER_ERROR
        }
    }

    override fun verifyRequest(uri: String, method: HttpMethod): Boolean {
        return super.verifyRequest(uri, method) && uri.startsWith("/servers")
    }
}