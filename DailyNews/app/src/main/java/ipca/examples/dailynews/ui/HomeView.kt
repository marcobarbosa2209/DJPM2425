// HomeView.kt
package ipca.examples.dailynews.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import ipca.examples.dailynews.Screen
import ipca.examples.dailynews.encodeURL
import ipca.examples.dailynews.models.Article
import ipca.examples.dailynews.ui.theme.DailyNewsTheme

// Import DropdownOption
import ipca.examples.dailynews.ui.DropdownOption

// Helper function to convert country code to flag emoji
fun String.toFlagEmoji(): String {
    return this.uppercase().map {
        0x1F1E6 + it.code - 'A'.code
    }.map {
        String(Character.toChars(it))
    }.joinToString("")
}

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    HomeViewContent(
        modifier = modifier,
        navController = navController,
        uiState = uiState,
        onFilterChange = { category, language, country, qInMeta ->
            viewModel.updateFilters(category, language, country, qInMeta)
            viewModel.fetchArticles()
        }
    )
    LaunchedEffect(Unit) {
        viewModel.fetchArticles()
    }
}

@Composable
fun HomeViewContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    uiState: ArticlesState,
    onFilterChange: (String?, String?, String?, String?) -> Unit
) {
    // Define Categories as DropdownOption
    val categories = listOf(
        "business", "crime", "domestic", "education", "entertainment",
        "environment", "food", "health", "lifestyle", "other",
        "politics", "science", "sports", "technology", "top",
        "tourism", "world"
    ).map { DropdownOption(displayName = it.replaceFirstChar { c -> c.uppercaseChar() }, value = it) }

    // Define Languages as DropdownOption
    val languages = listOf(
        Language("Afrikaans", "af"),
        Language("Albanian", "sq"),
        Language("Amharic", "am"),
        Language("Arabic", "ar"),
        Language("Armenian", "hy"),
        Language("Assamese", "as"),
        Language("Azerbaijani", "az"),
        Language("Bambara", "bm"),
        Language("Basque", "eu"),
        Language("Belarusian", "be"),
        Language("Bengali", "bn"),
        Language("Bosnian", "bs"),
        Language("Bulgarian", "bg"),
        Language("Burmese", "my"),
        Language("Catalan", "ca"),
        Language("Central Kurdish", "ckb"),
        Language("Chinese", "zh"),
        Language("Croatian", "hr"),
        Language("Czech", "cs"),
        Language("Danish", "da"),
        Language("Dutch", "nl"),
        Language("English", "en"),
        Language("Estonian", "et"),
        Language("Filipino", "pi"),
        Language("Finnish", "fi"),
        Language("French", "fr"),
        Language("Galician", "gl"),
        Language("Georgian", "ka"),
        Language("German", "de"),
        Language("Greek", "el"),
        Language("Gujarati", "gu"),
        Language("Hausa", "ha"),
        Language("Hebrew", "he"),
        Language("Hindi", "hi"),
        Language("Hungarian", "hu"),
        Language("Icelandic", "is"),
        Language("Indonesian", "id"),
        Language("Italian", "it"),
        Language("Japanese", "jp"),
        Language("Kannada", "kn"),
        Language("Kazakh", "kz"),
        Language("Khmer", "kh"),
        Language("Kinyarwanda", "rw"),
        Language("Korean", "ko"),
        Language("Kurdish", "ku"),
        Language("Latvian", "lv"),
        Language("Lithuanian", "lt"),
        Language("Luxembourgish", "lb"),
        Language("Macedonian", "mk"),
        Language("Malay", "ms"),
        Language("Malayalam", "ml"),
        Language("Maltese", "mt"),
        Language("Maori", "mi"),
        Language("Marathi", "mr"),
        Language("Mongolian", "mn"),
        Language("Nepali", "ne"),
        Language("Norwegian", "no"),
        Language("Oriya", "or"),
        Language("Pashto", "ps"),
        Language("Persian", "fa"),
        Language("Polish", "pl"),
        Language("Portuguese", "pt"),
        Language("Punjabi", "pa"),
        Language("Romanian", "ro"),
        Language("Russian", "ru"),
        Language("Samoan", "sm"),
        Language("Serbian", "sr"),
        Language("Shona", "sn"),
        Language("Sindhi", "sd"),
        Language("Sinhala", "si"),
        Language("Slovak", "sk"),
        Language("Slovenian", "sl"),
        Language("Somali", "so"),
        Language("Spanish", "es"),
        Language("Swahili", "sw"),
        Language("Swedish", "sv"),
        Language("Tajik", "tg"),
        Language("Tamil", "ta"),
        Language("Telugu", "te"),
        Language("Thai", "th"),
        Language("Traditional Chinese", "zht"),
        Language("Turkish", "tr"),
        Language("Turkmen", "tk"),
        Language("Ukrainian", "uk"),
        Language("Urdu", "ur"),
        Language("Uzbek", "uz"),
        Language("Vietnamese", "vi"),
        Language("Welsh", "cy"),
        Language("Zulu", "zu")
    ).map { DropdownOption(displayName = it.name, value = it.code) }

    // Define Countries as DropdownOption with Flags
    val countries = listOf(
        Country("Afghanistan", "af"),
        Country("Albania", "al"),
        Country("Algeria", "dz"),
        Country("Andorra", "ad"),
        Country("Angola", "ao"),
        Country("Argentina", "ar"),
        Country("Armenia", "am"),
        Country("Australia", "au"),
        Country("Austria", "at"),
        Country("Azerbaijan", "az"),
        Country("Bahamas", "bs"),
        Country("Bahrain", "bh"),
        Country("Bangladesh", "bd"),
        Country("Barbados", "bb"),
        Country("Belarus", "by"),
        Country("Belgium", "be"),
        Country("Belize", "bz"),
        Country("Benin", "bj"),
        Country("Bermuda", "bm"),
        Country("Bhutan", "bt"),
        Country("Bolivia", "bo"),
        Country("Bosnia And Herzegovina", "ba"),
        Country("Botswana", "bw"),
        Country("Brazil", "br"),
        Country("Brunei", "bn"),
        Country("Bulgaria", "bg"),
        Country("Burkina Faso", "bf"),
        Country("Burundi", "bi"),
        Country("Cambodia", "kh"),
        Country("Cameroon", "cm"),
        Country("Canada", "ca"),
        Country("Cape Verde", "cv"),
        Country("Cayman Islands", "ky"),
        Country("Central African Republic", "cf"),
        Country("Chad", "td"),
        Country("Chile", "cl"),
        Country("China", "cn"),
        Country("Colombia", "co"),
        Country("Comoros", "km"),
        Country("Congo", "cg"),
        Country("Cook Islands", "ck"),
        Country("Costa Rica", "cr"),
        Country("Croatia", "hr"),
        Country("Cuba", "cu"),
        Country("Curaçao", "cw"),
        Country("Cyprus", "cy"),
        Country("Czech Republic", "cz"),
        Country("Denmark", "dk"),
        Country("Djibouti", "dj"),
        Country("Dominica", "dm"),
        Country("Dominican Republic", "do"),
        Country("DR Congo", "cd"),
        Country("Ecuador", "ec"),
        Country("Egypt", "eg"),
        Country("El Salvador", "sv"),
        Country("Equatorial Guinea", "gq"),
        Country("Eritrea", "er"),
        Country("Estonia", "ee"),
        Country("Eswatini", "sz"),
        Country("Ethiopia", "et"),
        Country("Fiji", "fj"),
        Country("Finland", "fi"),
        Country("France", "fr"),
        Country("French Polynesia", "pf"),
        Country("Gabon", "ga"),
        Country("Gambia", "gm"),
        Country("Georgia", "ge"),
        Country("Germany", "de"),
        Country("Ghana", "gh"),
        Country("Gibraltar", "gi"),
        Country("Greece", "gr"),
        Country("Grenada", "gd"),
        Country("Guatemala", "gt"),
        Country("Guinea", "gn"),
        Country("Guyana", "gy"),
        Country("Haiti", "ht"),
        Country("Honduras", "hn"),
        Country("Hong Kong", "hk"),
        Country("Hungary", "hu"),
        Country("Iceland", "is"),
        Country("India", "in"),
        Country("Indonesia", "id"),
        Country("Iran", "ir"),
        Country("Iraq", "iq"),
        Country("Ireland", "ie"),
        Country("Israel", "il"),
        Country("Italy", "it"),
        Country("Ivory Coast", "ci"),
        Country("Jamaica", "jm"),
        Country("Japan", "jp"),
        Country("Jersey", "je"),
        Country("Jordan", "jo"),
        Country("Kazakhstan", "kz"),
        Country("Kenya", "ke"),
        Country("Kiribati", "ki"),
        Country("Kosovo", "xk"),
        Country("Kuwait", "kw"),
        Country("Kyrgyzstan", "kg"),
        Country("Laos", "la"),
        Country("Latvia", "lv"),
        Country("Lebanon", "lb"),
        Country("Lesotho", "ls"),
        Country("Liberia", "lr"),
        Country("Libya", "ly"),
        Country("Liechtenstein", "li"),
        Country("Lithuania", "lt"),
        Country("Luxembourg", "lu"),
        Country("Macau", "mo"),
        Country("Macedonia", "mk"),
        Country("Madagascar", "mg"),
        Country("Malawi", "mw"),
        Country("Malaysia", "my"),
        Country("Maldives", "mv"),
        Country("Mali", "ml"),
        Country("Malta", "mt"),
        Country("Marshall Islands", "mh"),
        Country("Mauritania", "mr"),
        Country("Mauritius", "mu"),
        Country("Mexico", "mx"),
        Country("Micronesia", "fm"),
        Country("Moldova", "md"),
        Country("Monaco", "mc"),
        Country("Mongolia", "mn"),
        Country("Montenegro", "me"),
        Country("Morocco", "ma"),
        Country("Mozambique", "mz"),
        Country("Myanmar", "mm"),
        Country("Namibia", "na"),
        Country("Nauru", "nr"),
        Country("Nepal", "np"),
        Country("Netherlands", "nl"),
        Country("New Caledonia", "nc"),
        Country("New Zealand", "nz"),
        Country("Nicaragua", "ni"),
        Country("Niger", "ne"),
        Country("Nigeria", "ng"),
        Country("North Korea", "kp"),
        Country("Norway", "no"),
        Country("Oman", "om"),
        Country("Pakistan", "pk"),
        Country("Palau", "pw"),
        Country("Palestine", "ps"),
        Country("Panama", "pa"),
        Country("Papua New Guinea", "pg"),
        Country("Paraguay", "py"),
        Country("Peru", "pe"),
        Country("Philippines", "ph"),
        Country("Poland", "pl"),
        Country("Portugal", "pt"),
        Country("Puerto Rico", "pr"),
        Country("Qatar", "qa"),
        Country("Romania", "ro"),
        Country("Russia", "ru"),
        Country("Rwanda", "rw"),
        Country("Saint Lucia", "lc"),
        Country("Saint Martin (Dutch)", "sx"),
        Country("Samoa", "ws"),
        Country("San Marino", "sm"),
        Country("Sao Tome and Principe", "st"),
        Country("Saudi Arabia", "sa"),
        Country("Senegal", "sn"),
        Country("Serbia", "rs"),
        Country("Seychelles", "sc"),
        Country("Sierra Leone", "sl"),
        Country("Singapore", "sg"),
        Country("Slovakia", "sk"),
        Country("Slovenia", "si"),
        Country("Solomon Islands", "sb"),
        Country("Somalia", "so"),
        Country("South Africa", "za"),
        Country("South Korea", "kr"),
        Country("Spain", "es"),
        Country("Sri Lanka", "lk"),
        Country("Sudan", "sd"),
        Country("Suriname", "sr"),
        Country("Sweden", "se"),
        Country("Switzerland", "ch"),
        Country("Syria", "sy"),
        Country("Taiwan", "tw"),
        Country("Tajikistan", "tj"),
        Country("Tanzania", "tz"),
        Country("Thailand", "th"),
        Country("Timor-Leste", "tl"),
        Country("Togo", "tg"),
        Country("Tonga", "to"),
        Country("Trinidad and Tobago", "tt"),
        Country("Tunisia", "tn"),
        Country("Turkey", "tr"),
        Country("Turkmenistan", "tk"),
        Country("Tuvalu", "tv"),
        Country("Uganda", "ug"),
        Country("Ukraine", "ua"),
        Country("United Arab Emirates", "ae"),
        Country("United Kingdom", "gb"),
        Country("United States of America", "us"),
        Country("Uruguay", "uy"),
        Country("Uzbekistan", "uz"),
        Country("Vanuatu", "vu"),
        Country("Vatican", "va"),
        Country("Venezuela", "ve"),
        Country("Vietnam", "vi"),
        Country("Virgin Islands (British)", "vg"),
        Country("World", "wo"),
        Country("Yemen", "ye"),
        Country("Zambia", "zm"),
        Country("Zimbabwe", "zw")
    ).map { DropdownOption(displayName = "${it.name} ${it.code.toFlagEmoji()}", value = it.code) }

    // States for filters
    var selectedCategory by remember { mutableStateOf<String?>(null) } // No category selected by default
    var selectedLanguage by remember { mutableStateOf<String?>(languages.find { it.value == "en" }?.value) } // English
    var selectedCountry by remember { mutableStateOf<String?>(countries.find { it.value == "us" }?.value) } // US
    var qInMeta by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Filters Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Category Filter
            DropdownMenuFilter(
                label = "Category",
                options = categories,
                selectedOption = selectedCategory,
                onOptionSelected = {
                    selectedCategory = it
                },
                includeAll = false
            )

            // Language Filter
            DropdownMenuFilter(
                label = "Language",
                options = languages,
                selectedOption = selectedLanguage,
                onOptionSelected = {
                    selectedLanguage = it
                },
                includeAll = false
            )

            // Country Filter
            DropdownMenuFilter(
                label = "Country",
                options = countries,
                selectedOption = selectedCountry,
                onOptionSelected = {
                    selectedCountry = it
                },
                includeAll = false
            )
        }

        // Keyword in Title
        OutlinedTextField(
            value = qInMeta,
            onValueChange = { qInMeta = it },
            label = { Text("Keyword in Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        // Apply Filters Button
        Button(
            onClick = {
                onFilterChange(selectedCategory, selectedLanguage, selectedCountry, qInMeta)
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp)
        ) {
            Text("Apply Filters")
        }

        // Articles List or Loading/Error States
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.error != null -> {
                    Text(text = "Error: ${uiState.error}")
                }
                uiState.articles.isEmpty() -> {
                    Text(text = "No articles found!")
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        itemsIndexed(uiState.articles) { _, article ->
                            BannerArticle(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        if (article.link.isNotEmpty()) {
                                            Log.d("dailynews", article.link)
                                            navController.navigate(
                                                "articleDetail/${article.link.encodeURL()}"
                                            )
                                        }
                                    },
                                article = article,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A single DropdownMenuFilter composable that can handle any type of DropdownOption.
 */
@Composable
fun DropdownMenuFilter(
    label: String,
    options: List<DropdownOption>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit,
    includeAll: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            val displayText = if (selectedOption != null) {
                options.find { it.value == selectedOption }?.displayName ?: label
            } else {
                label
            }
            Text(text = displayText)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (includeAll) {
                DropdownMenuItem(
                    text = { Text(text = "All") },
                    onClick = { onOptionSelected(null); expanded = false }
                )
            }
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.displayName) },
                    onClick = {
                        onOptionSelected(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun BannerArticle(
    modifier: Modifier = Modifier,
    article: Article
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            article.image_url?.let {
                AsyncImage(
                    model = it,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = article.title,
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
