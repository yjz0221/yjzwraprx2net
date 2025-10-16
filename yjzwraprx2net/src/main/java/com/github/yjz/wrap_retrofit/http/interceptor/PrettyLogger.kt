package com.github.yjz.wrap_retrofit.http.interceptor


import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import android.util.Log
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource


/**
 * 作者:yjz
 * 创建日期：2025/10/16
 * 描述: 一个用于 OkHttp 的、格式化输出网络日志的拦截器。
 * - 自动格式化 JSON、XML、HTML 等常见文本类型的 Body。
 * - 支持多种日志级别 (NONE, BASIC, HEADERS, BODY)。
 *
 * @param level 日志打印的详细程度。
 */
class PrettyLogger(var level: Level = Level.BODY,var tag : String = "YjzLogger") : Interceptor {

    enum class Level {
        NONE,  // 不打印
        BASIC, // 只打印请求行和响应行 (e.g., "--> GET /user/1" and "<-- 200 OK")
        HEADERS, // 打印行 + Headers
        BODY // 打印行 + Headers + Body
    }

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (level == Level.NONE) {
            return chain.proceed(request)
        }

        logRequest(request)

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            log("<-- HTTP FAILED: $e")
            throw e
        }
        val tookMs = (System.nanoTime() - startNs) / 1_000_000L

        logResponse(response, tookMs)

        return response
    }

    private fun logRequest(request: Request) {
        val logBody = level == Level.BODY
        val logHeaders = level >= Level.HEADERS

        val sb = StringBuilder()
        sb.appendLine("╔══════════════════════════════ Request ═══════════════════════════════")
        sb.appendLine("║ ${request.method()} ${request.url()}")

        if (logHeaders) {
            sb.appendLine("╟─────── Headers ───────")
            // 使用索引循环遍历请求头，以兼容旧版 OkHttp
            for (i in 0 until request.headers().size()) {
                val name = request.headers().name(i)
                val value = request.headers().value(i)
                sb.appendLine("║ $name: $value")
            }
        }

        if (logBody && request.body() != null) {
            sb.appendLine("╟──────── Body ─────────")
            try {
                val requestBody = request.body()!!
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                val charset = requestBody.contentType()?.charset(Charset.forName("UTF-8")) ?: Charset.forName("UTF-8")
                if (isPrintable(requestBody.contentType()?.toString())) {
                    sb.appendLine(formatBody(buffer.readString(charset), requestBody.contentType()?.toString()))
                } else {
                    sb.appendLine("║ Binary data (${requestBody.contentLength()}-byte body omitted)")
                }
            } catch (e: IOException) {
                sb.appendLine("║ Error reading request body: ${e.message}")
            }
        }
        sb.appendLine("╚══════════════════════════════════════════════════════════════════════")
        log(sb.toString())
    }

    private fun logResponse(response: Response, tookMs: Long) {
        val logBody = level == Level.BODY
        val logHeaders = level >= Level.HEADERS

        val sb = StringBuilder()
        sb.appendLine("╔══════════════════════════════ Response ══════════════════════════════")
        sb.appendLine("║ ${response.protocol()} ${response.code()} ${response.message()} (${tookMs}ms)")
        sb.appendLine("║ URL: ${response.request().url()}")

        if (logHeaders) {
            sb.appendLine("╟─────── Headers ───────")
            // 使用索引循环遍历响应头，以兼容旧版 OkHttp
            for (i in 0 until response.headers().size()) {
                val name = response.headers().name(i)
                val value = response.headers().value(i)
                sb.appendLine("║ $name: $value")
            }
        }

        if (logBody) {
            sb.appendLine("╟──────── Body ─────────")
            val responseBody = response.body()
            // 直接检查 responseBody 是否为 null
            if (responseBody != null) {
                try {
                    // 使用 peekBody 不会消耗掉 body，下游可以继续读取
                    val bodyString = response.peekBody(1024 * 1024).string()
                    if (isPrintable(responseBody.contentType()?.toString())) {
                        sb.appendLine(formatBody(bodyString, responseBody.contentType()?.toString()))
                    } else {
                        sb.appendLine("║ Binary data (${responseBody.contentLength()}-byte body omitted)")
                    }
                } catch (e: Exception) {
                    sb.appendLine("║ Error reading response body: ${e.message}")
                }
            } else {
                sb.appendLine("║ Body is null")
            }
        }
        sb.appendLine("╚══════════════════════════════════════════════════════════════════════")
        log(sb.toString())
    }

    private fun formatBody(body: String, contentType: String?): String {
        return try {
            when {
                contentType?.contains("json", ignoreCase = true) == true -> {
                    // 格式化 JSON
                    val jsonElement = JsonParser().parse(body)
                    gson.toJson(jsonElement).split("\n").joinToString("\n") { "║ $it" }
                }
                // 格式化 XML
                 contentType?.contains("xml", ignoreCase = true) == true -> formatXml(body)
                else -> {
                    // 其他文本类型直接输出
                    body.split("\n").joinToString("\n") { "║ $it" }
                }
            }
        } catch (e: Exception) {
            // 如果格式化失败 (例如 JSON 格式错误)，则直接输出原始字符串
            body.split("\n").joinToString("\n") { "║ $it" }
        }
    }

    /**
     * 使用标准的 javax.xml 库来格式化 XML 字符串。
     *
     * @param xml 未经格式化的 XML 字符串。
     * @return 格式化后的 XML 字符串，如果格式化失败则返回原始字符串。
     */
    private fun formatXml(xml: String): String {
        return try {
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

            val source = StreamSource(StringReader(xml))
            val writer = StringWriter()
            val result = StreamResult(writer)

            transformer.transform(source, result)
            writer.toString()
        } catch (e: Exception) {
            // 如果 XML 格式不正确导致解析失败，则直接返回原始字符串
            xml
        }
    }

    private fun isPrintable(contentType: String?): Boolean {
        if (contentType == null) return false
        val type = contentType.lowercase()
        return type.contains("text") ||
                type.contains("json") ||
                type.contains("xml") ||
                type.contains("html") ||
                type.contains("x-www-form-urlencoded")
    }

    private fun log(message: String) {
        Log.d(tag, message)
    }
}