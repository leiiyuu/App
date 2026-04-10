package com.example.application2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RecipeApp()
                }
            }
        }
    }
}

enum class RecipeStatus {
    want_to_cook,
    cooking,
    cooked,
}

data class RecipeStats(
    val total: Int,
    val want_to_cook: Int,
    val cooking: Int,
    val cooked: Int
)

data class Recipe(
    val id: Int,
    val title: String,
    val category: String,
    val time: String,
    val difficulty: String,
    var status: RecipeStatus
)

data class RecipeDetails(
    val id: Int,
    val title: String,
    val category: String,
    val description: String,
    val ingredients: String,
    val time: String,
    val difficulty: String,
    var status: RecipeStatus
)

val sampleRecipeList = listOf(
    Recipe(1, "pancakes", "десерт", "30 мин", "средне", RecipeStatus.want_to_cook),
    Recipe(2, "cheesecake", "десерт", "2 часа", "легко", RecipeStatus.cooked),
    Recipe(3, "caesar", "салат", "20 мин", "легко", RecipeStatus.cooking),
    Recipe(4, "cutlets", "ужин", "45 мин", "средне", RecipeStatus.want_to_cook),
    Recipe(5, "honey cake", "десерт", "2 часа", "сложно", RecipeStatus.cooking),
)

val sampleRecipeDetailsList = listOf(
    RecipeDetails(
        id = 1,
        title = "сырники",
        category = "десерт",
        description = "классический рецепт на сковороде",
        ingredients = "творог, яйцо, сахар, мука",
        time = "30 мин",
        difficulty = "средне",
        status = RecipeStatus.want_to_cook,
    ),
    RecipeDetails(
        id = 2,
        title = "чизкейк",
        category = "десерт",
        description = "творожный десерт без выпечки",
        ingredients = "печенье, масло, творожный сыр, сливки, сахар, желатин",
        time = "2 часа",
        difficulty = "легко",
        status = RecipeStatus.cooked,
    ),
    RecipeDetails(
        id = 3,
        title = "цезарь",
        category = "салат",
        description = "классический салат с курицей и сухариками",
        ingredients = "курица, салат, черри, яйцо, пармезан, сухари, соус цезарь",
        time = "20 мин",
        difficulty = "легко",
        status = RecipeStatus.cooking,
    ),
    RecipeDetails(
        id = 4,
        title = "отбивные",
        category = "ужин",
        description = "свиные отбивные на сковороде",
        ingredients = "свинина, яйцо, сухари, соль, перец, масло",
        time = "45 мин",
        difficulty = "средне",
        status = RecipeStatus.want_to_cook,
    ),
    RecipeDetails(
        id = 5,
        title = "медовик",
        category = "десерт",
        description = "классический медовый торт",
        ingredients = "мука, мед, сахар, яйца, сметана, масло",
        time = "2 часа",
        difficulty = "сложно",
        status = RecipeStatus.cooking,
    ),
)

data class RecipeListUiState(
    val searchQuery: String = "",
    val statusFilter: RecipeStatus? = null,
    val recipeList: List<Recipe> = sampleRecipeList
)

@SuppressLint("MutableCollectionMutableState")
class RecipeViewModel : ViewModel() {

    private var _recipes by mutableStateOf(sampleRecipeList.toMutableList())

    var uiState by mutableStateOf(RecipeListUiState())
        private set

    val filteredRecipeList: List<Recipe>
        get() {
            var result: List<Recipe> = _recipes

            if (uiState.searchQuery.isNotBlank()) {
                result = result.filter { recipe ->
                    recipe.title.contains(uiState.searchQuery, ignoreCase = true)
                }
            }
            if (uiState.statusFilter != null) {
                result = result.filter { recipe ->
                    recipe.status == uiState.statusFilter
                }
            }

            return result
        }

    val stats: RecipeStats
        get() {
            val total = _recipes.size
            val want_to_cook = _recipes.count { it.status == RecipeStatus.want_to_cook }
            val cooking = _recipes.count { it.status == RecipeStatus.cooking }
            val cooked = _recipes.count { it.status == RecipeStatus.cooked }

            return RecipeStats(total, want_to_cook, cooking, cooked)
        }

    fun onSearchChange(newValue: String) {
        uiState = uiState.copy(
            searchQuery = newValue
        )
    }

    fun onStatusFilterChange(newFilter: RecipeStatus?) {
        uiState = uiState.copy(statusFilter = newFilter)
    }

    fun getRecipeDetailsById(id: Int): RecipeDetails? {
        val recipe = _recipes.find { it.id == id } ?: return null
        val details = sampleRecipeDetailsList.find { it.id == id } ?: return null
        return details.copy(status = recipe.status)
    }

    private fun filterRecipe(query: String): List<Recipe> {
        if (query.isBlank()) return _recipes

        return _recipes.filter { recipe ->
            recipe.title.contains(query, ignoreCase = true)
        }
    }

    fun updateStatus(recipeId: Int, newStatus: RecipeStatus) {
        val index = _recipes.indexOfFirst { it.id == recipeId }
        if (index != -1) {
            val currentRecipe = _recipes[index]
            _recipes[index] = currentRecipe.copy(status = newStatus)

        }
    }
}
object RecipeRoutes {
    const val LIST_ROUTE = "recipe_list"
    const val DETAILS_ROUTE = "recipe_details"
    const val RECIPE_ID_ARG = "recipeId"
    const val DETAILS_ROUTE_PATTERN = "$DETAILS_ROUTE/{$RECIPE_ID_ARG}"
    fun details(recipeId: Int): String = "$DETAILS_ROUTE/$recipeId"
}


@Composable
fun RecipeApp() {
    val recipeViewModel: RecipeViewModel = viewModel()
    val uiState = recipeViewModel.uiState
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RecipeRoutes.LIST_ROUTE) {
        composable(RecipeRoutes.LIST_ROUTE) {
            RecipeListScreen(
                recipeList = recipeViewModel.filteredRecipeList,
                searchQuery = uiState.searchQuery,
                statusFilter = uiState.statusFilter,
                onSearchChange = recipeViewModel::onSearchChange,
                onStatusFilterChange = recipeViewModel::onStatusFilterChange,
                onRecipeClick = { recipeId -> navController.navigate(RecipeRoutes.details(recipeId)) },
                stats = recipeViewModel.stats
            )
        }

        composable(
            route = RecipeRoutes.DETAILS_ROUTE_PATTERN,
            arguments = listOf(
                navArgument(RecipeRoutes.RECIPE_ID_ARG) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val recipeId: Int? = backStackEntry.arguments?.getInt(RecipeRoutes.RECIPE_ID_ARG)

            RecipeDetailsScreen(
                recipeDetails = recipeId?.let(recipeViewModel::getRecipeDetailsById),
                onBackClick = { navController.navigateUp() },
                onStatusUpdate = { newStatus ->
                    if (recipeId != null) {
                        recipeViewModel.updateStatus(recipeId, newStatus)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    recipeList: List<Recipe>,
    searchQuery: String,
    statusFilter: RecipeStatus?,
    onSearchChange: (String) -> Unit,
    onStatusFilterChange: (RecipeStatus?) -> Unit,
    onRecipeClick: (Int) -> Unit,
    stats: RecipeStats,

) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Recipe Book") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search by title") },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            RecipeStatsRow(stats = stats)

            Spacer(modifier = Modifier.height(16.dp))

            RecipeFiltersRow(
                currentFilter = statusFilter,
                onFilterChange = onStatusFilterChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (recipeList.isEmpty()) {
                Text("  пусто   ")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items = recipeList, key = { it.id }) { item ->
                        RecipeCard(
                            recipe = item,
                            onClick = { onRecipeClick(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeFiltersRow(
    currentFilter: RecipeStatus?,
    onFilterChange: (RecipeStatus?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChipSimple(
            text = "all",
            isSelected = currentFilter == null,
            onClick = { onFilterChange(null) }
        )
        FilterChipSimple(
            text = "want to cook",
            isSelected = currentFilter == RecipeStatus.want_to_cook,
            onClick = { onFilterChange(RecipeStatus.want_to_cook) }
        )
        FilterChipSimple(
            text = "cooking",
            isSelected = currentFilter == RecipeStatus.cooking,
            onClick = { onFilterChange(RecipeStatus.cooking) }
        )
        FilterChipSimple(
            text = "cooked",
            isSelected = currentFilter == RecipeStatus.cooked,
            onClick = { onFilterChange(RecipeStatus.cooked) }
        )
    }
}

@Composable
fun FilterChipSimple(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isSelected) 0.dp else 2.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun RecipeStatsRow(stats: RecipeStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "all: ${stats.total} | want_to_cook: ${stats.want_to_cook} | cooking: ${stats.cooking} | cooked: ${stats.cooked}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = recipe.title, fontWeight = FontWeight.Bold)
            Text("${recipe.category} • ${recipe.time}")
            Text("Сложность: ${recipe.difficulty}")
            RecipeStatusBadge(status = recipe.status)
        }
    }
}

@Composable
fun RecipeStatusBadge(status: RecipeStatus) {
    val text = when (status) {
        RecipeStatus.want_to_cook -> "want to cook"
        RecipeStatus.cooking -> "cooking"
        RecipeStatus.cooked -> "cooked"
    }
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    recipeDetails: RecipeDetails?,
    onBackClick: () -> Unit,
    onStatusUpdate: (RecipeStatus) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Recipe Details") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Button(onClick = onBackClick) {
                Text("Back")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (recipeDetails == null) {
                Text("  пусто   ")
            } else {
                Text(
                    text = recipeDetails.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Категория: ${recipeDetails.category}")
                Text("Время: ${recipeDetails.time}")
                Text("Сложность: ${recipeDetails.difficulty}")

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Статус:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                RecipeStatusBadge(status = recipeDetails.status)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusButton(
                        text = "want_to_cook",
                        isSelected = recipeDetails.status == RecipeStatus.want_to_cook,
                        onClick = { onStatusUpdate(RecipeStatus.want_to_cook) }
                    )
                    StatusButton(
                        text = "cooking",
                        isSelected = recipeDetails.status == RecipeStatus.cooking,
                        onClick = { onStatusUpdate(RecipeStatus.cooking) }
                    )
                    StatusButton(
                        text = "cooked",
                        isSelected = recipeDetails.status == RecipeStatus.cooked,
                        onClick = { onStatusUpdate(RecipeStatus.cooked) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ингредиенты:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(recipeDetails.ingredients)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Описание:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(recipeDetails.description)
            }
        }
    }
}

@Composable
fun StatusButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text = text)
    }
}