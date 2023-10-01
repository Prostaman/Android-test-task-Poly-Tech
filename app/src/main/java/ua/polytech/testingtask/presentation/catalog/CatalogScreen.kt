package ua.polytech.testingtask.presentation.catalog


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ua.polytech.testingtask.api.models.Catalog
import ua.polytech.testingtask.api.other.Status
import ua.polytech.testingtask.presentation.ui.CentralizeCircularProgressBar
import ua.polytech.testingtask.presentation.ui.SearchCategoryInput
import ua.polytech.testingtask.presentation.ui.rememberEditableUserInputState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.LocalContext
import ua.polytech.testingtask.common.checkInternet.isConnectedToInternet
import ua.polytech.testingtask.presentation.ui.ErrorScreen

@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel = hiltViewModel(),
    onClickCategory: (listNameEncoded: String,listName:String) -> Unit
) {
    val context = LocalContext.current
    var errorRefreshTrigger by remember { mutableIntStateOf(0) }
    val catalogState by viewModel.requestCatalog.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = errorRefreshTrigger) {
        if (isConnectedToInternet(context)) {
            viewModel.getCatalogFromNetwork()
        } else {
            viewModel.getCatalogFromLocalBD()
        }
    }

    when (catalogState.status) {
        Status.SUCCESS -> {
            Screen(viewModel = viewModel, onClickCategory)
        }

        Status.ERROR -> {
            ErrorScreen(errorMessage = catalogState.message.toString()) {
                errorRefreshTrigger++
            }
        }

        Status.LOADING -> {
            CentralizeCircularProgressBar()
        }
    }
}


@Composable
fun Screen(viewModel: CatalogViewModel, onClickCategory: (listNameEncoded: String, listName:String) -> Unit) {
    var isSearchVisible by remember { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -1) {
                    isSearchVisible = false
                }

                if (available.y > 1) {
                    isSearchVisible = true
                }

                return Offset.Zero
            }
        }
    }
    val scrollState = rememberLazyListState()
    val suggestedCategories by viewModel.suggestedCategories.collectAsStateWithLifecycle()

    Grid(
        list = suggestedCategories, nestedScrollConnection = nestedScrollConnection, onClickCategory,
        scrollState = scrollState
    )

    val editableUserInputState = rememberEditableUserInputState(hint = "")
    AnimatedVisibility(
        visible = isSearchVisible,
        enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }),
        exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }),
    ) {
        SearchCategoryInput(
            onTextChanged = { viewModel.textOfSearchChanged(it) },
            onTextEmpty = { viewModel.onTextEmpty() },
            editableUserInputState = editableUserInputState,
            hintText = "Write category"
        )
    }

}

@Composable
private fun Grid(
    list: List<Catalog>,
    nestedScrollConnection: NestedScrollConnection,
    onClickCategory: (listNameEncoded: String,listName:String) -> Unit,
    scrollState: LazyListState
) {
    LazyColumn(
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = scrollState
        ) {
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
        items(list, key = { it.listName }) { category ->
            Category(catalog = category, onClickCategory)
        }
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
        }
    }
}

@Composable
private fun Category(catalog: Catalog, onClickCategory: (listNameEncoded: String,listName:String) -> Unit) {
    Button(
        onClick = { onClickCategory(catalog.listNameEncoded,catalog.listName) },
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20)
    ) {
        Column {
            Text(
                text = catalog.displayName,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.weight(1f)) // Push the following Row to the bottom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Updates: ${catalog.updated}",
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    color = Color.Black,
                )
                Text(
                    text = "Last Update: ${catalog.newestPublishedDate}",
                    textAlign = TextAlign.End,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }

        }

    }
}



