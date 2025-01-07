// Article.kt
package ipca.examples.dailynews.models

import org.json.JSONObject

data class Article(
    val article_id: String,
    val title: String,
    val link: String,
    val keywords: List<String> = emptyList(),
    val creator: String? = null,
    val video_url: String? = null,
    val description: String,
    val content: String,
    val pubDate: String,
    val pubDateTZ: String,
    val image_url: String? = null,
    val source_id: String,
    val source_priority: Int,
    val source_name: String,
    val source_url: String,
    val source_icon: String,
    val language: String,
    val country: List<String> = emptyList(),
    val category: List<String> = emptyList(),
    val ai_tag: List<String> = emptyList(),
    val ai_region: String? = null,
    val ai_org: String? = null,
    val sentiment: String,
    val sentiment_stats: SentimentStats? = null,
    val duplicate: Boolean,
    val coin: String? = null
) {
    data class SentimentStats(
        val positive: Double,
        val neutral: Double,
        val negative: Double
    )

    companion object {
        fun fromJson(json: JSONObject): Article {
            val keywords = json.optJSONArray("keywords")?.let {
                List(it.length()) { index -> it.getString(index) }
            } ?: emptyList()

            val country = json.optJSONArray("country")?.let {
                List(it.length()) { index -> it.getString(index) }
            } ?: emptyList()

            val category = json.optJSONArray("category")?.let {
                List(it.length()) { index -> it.getString(index) }
            } ?: emptyList()

            val ai_tag = json.optJSONArray("ai_tag")?.let {
                List(it.length()) { index -> it.getString(index) }
            } ?: emptyList()

            val sentiment_stats = json.optJSONObject("sentiment_stats")?.let {
                SentimentStats(
                    positive = it.optDouble("positive", 0.0),
                    neutral = it.optDouble("neutral", 0.0),
                    negative = it.optDouble("negative", 0.0)
                )
            }

            return Article(
                article_id = json.getString("article_id"),
                title = json.getString("title"),
                link = json.getString("link"),
                keywords = keywords,
                creator = json.optString("creator"),
                video_url = json.optString("video_url"),
                description = json.getString("description"),
                content = json.getString("content"),
                pubDate = json.getString("pubDate"),
                pubDateTZ = json.getString("pubDateTZ"),
                image_url = json.optString("image_url"),
                source_id = json.getString("source_id"),
                source_priority = json.getInt("source_priority"),
                source_name = json.getString("source_name"),
                source_url = json.getString("source_url"),
                source_icon = json.getString("source_icon"),
                language = json.getString("language"),
                country = country,
                category = category,
                ai_tag = ai_tag,
                ai_region = json.optString("ai_region"),
                ai_org = json.optString("ai_org"),
                sentiment = json.getString("sentiment"),
                sentiment_stats = sentiment_stats,
                duplicate = json.getBoolean("duplicate"),
                coin = json.optString("coin")
            )
        }
    }
}