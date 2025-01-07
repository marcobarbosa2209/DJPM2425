// Screen.kt
package ipca.examples.dailynews

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ArticleDetail : Screen("articleDetail/{articleUrl}") {
        fun createRoute(articleUrl: String) = "articleDetail/$articleUrl"
    }
}