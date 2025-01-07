package ipca.examples.dailynews

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun String.encodeURL(): String {
    return URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
}