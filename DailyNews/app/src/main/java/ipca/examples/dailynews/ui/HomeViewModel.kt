// HomeViewModel.kt
package ipca.examples.dailynews.ui

import androidx.lifecycle.ViewModel
import ipca.examples.dailynews.models.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import ipca.examples.dailynews.encodeURL

data class ArticlesState(
    val articles: ArrayList<Article> = arrayListOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ArticlesState())
    val uiState: StateFlow<ArticlesState> = _uiState.asStateFlow()

    private var category: String? = null
    private var language: String? = "en"
    private var country: String? = "us"
    private var qInMeta: String? = null

    fun updateFilters(newCategory: String?, newLanguage: String?, newCountry: String?, newQInMeta: String?) {
        category = newCategory
        language = newLanguage
        country = newCountry
        qInMeta = newQInMeta
    }

    fun fetchArticles() {
        _uiState.value = ArticlesState(
            isLoading = true,
            error = null
        )
        val client = OkHttpClient()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val toDate = LocalDate.now().format(formatter)
        val fromDate = LocalDate.now().minusDays(7).format(formatter)
        val qBuilder = StringBuilder()
        qInMeta?.takeIf { it.isNotBlank() }?.let {
            qBuilder.append("qInMeta=${it.encodeURL()}&")
        }
        category?.takeIf { it.isNotBlank() }?.let {
            qBuilder.append("category=${it.encodeURL()}&")
        }
        language?.takeIf { it.isNotBlank() }?.let {
            qBuilder.append("language=${it.encodeURL()}&")
        }
        country?.takeIf { it.isNotBlank() }?.let {
            qBuilder.append("country=${it.encodeURL()}&")
        }
        qBuilder.append("&apikey=pub_64618bb883564801c3fcfa8b36d612dc9b3e8")
        val url = "https://newsdata.io/api/1/latest?$qBuilder"
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                _uiState.value = ArticlesState(
                    isLoading = false,
                    error = e.message
                )
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        _uiState.value = ArticlesState(
                            isLoading = false,
                            error = "Unexpected code $response"
                        )
                        return
                    }
                    val articlesResult = arrayListOf<Article>()
                    val result = response.body?.string() ?: ""
                    if (result.isNotEmpty()) {
                        val jsonResult = JSONObject(result)
                        val status = jsonResult.getString("status")
                        if (status == "success") {
                            val articlesJson = jsonResult.getJSONArray("results")
                            for (index in 0 until articlesJson.length()) {
                                val articleJson = articlesJson.getJSONObject(index)
                                val article = Article.fromJson(articleJson)
                                articlesResult.add(article)
                            }
                        } else {
                            val message = jsonResult.optString("message", "Unknown error")
                            _uiState.value = ArticlesState(
                                isLoading = false,
                                error = message
                            )
                            return
                        }
                    }
                    _uiState.value = ArticlesState(
                        articles = articlesResult,
                        isLoading = false,
                        error = null
                    )
                }
            }
        })
    }
}