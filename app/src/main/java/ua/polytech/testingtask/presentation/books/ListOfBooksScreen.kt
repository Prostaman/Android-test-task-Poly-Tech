package ua.polytech.testingtask.presentation.books

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ua.polytech.testingtask.api.models.Book
import ua.polytech.testingtask.api.other.Status
import ua.polytech.testingtask.presentation.ui.CentralizeCircularProgressBar
import ua.polytech.testingtask.presentation.ui.SearchCategoryInput
import ua.polytech.testingtask.presentation.ui.rememberEditableUserInputState
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest.Builder
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import ua.polytech.testingtask.presentation.ui.theme.ItemShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import ua.polytech.testingtask.R
import ua.polytech.testingtask.common.checkInternet.isConnectedToInternet
import ua.polytech.testingtask.presentation.ui.ErrorScreen
import ua.polytech.testingtask.presentation.ui.theme.md_theme_light_outline
import ua.polytech.testingtask.presentation.ui.theme.md_theme_light_surfaceTint

@Composable
fun ListOfBooksScreen(
    listNameEncoded: String,
    listName:String,
    viewModel: ListOfBooksViewModel = hiltViewModel(),
    onClickBuy: (url: String) -> Unit
) {
    val context = LocalContext.current
    var errorRefreshTrigger by remember { mutableIntStateOf(0) }
    val responseState by viewModel.requestListOfBooks.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = errorRefreshTrigger) {
        if (isConnectedToInternet(context)) {
            viewModel.getListOfBooksFromNetworkDB(listNameEncoded)
        } else {
            viewModel.getListOfBooksFromLocalDB(listNameEncoded)
        }
    }

    when (responseState.status) {
        Status.SUCCESS -> {
            Screen(viewModel = viewModel, onClickBuy = onClickBuy, listName=listName)
        }

        Status.ERROR -> {
            ErrorScreen(errorMessage = responseState.message.toString()) {
                errorRefreshTrigger++
            }
        }

        Status.LOADING -> {
            CentralizeCircularProgressBar()
        }
    }

}

@Composable
fun Screen(viewModel: ListOfBooksViewModel, onClickBuy: (url: String) -> Unit, listName: String) {
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

    val suggestedBooks by viewModel.suggestedBooks.collectAsStateWithLifecycle()

    Grid(list = suggestedBooks, nestedScrollConnection = nestedScrollConnection, onClickBuy = onClickBuy)

    val editableUserInputState = rememberEditableUserInputState(hint = "")
    AnimatedVisibility(
        visible = isSearchVisible,
        enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }),
        exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }),
    ) {
        Column{
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(color = md_theme_light_outline)) {
                Text(text = listName,
                    modifier = Modifier.fillMaxWidth().padding(start=16.dp,end=16.dp, bottom = 8.dp),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold, // Set the text to bold
                        fontSize = 20.sp // Adjust the font size as needed
                    )
                )
            }

            SearchCategoryInput(
                onTextChanged = { viewModel.textOfSearchChanged(it) },
                editableUserInputState = editableUserInputState,
                hintText = "Write title or author of book",
                onTextEmpty = {viewModel.textOfSearchEmpty() }
            )
        }

    }

}

@Composable
private fun Grid(list: List<Book>, nestedScrollConnection: NestedScrollConnection, onClickBuy: (url: String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp), // Vertical spacing between items
    ) {
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
            )
        }
        items(list, key={"${it.title}-${it.author}"}) { book ->
            ExploreItem(book = book, onClickBuy = onClickBuy)
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
private fun ExploreItem(book: Book, onClickBuy: (url: String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray.copy(alpha = 0.9F), shape = ItemShape),
        horizontalAlignment = Alignment.Start
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            Row(
                modifier =
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .width(100.dp)
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ExploreImageContainer(content = {
                        Box {
                            val painter = rememberAsyncImagePainter(
                                Builder(LocalContext.current).data(data = book.bookImage)
                                    .apply(block = fun Builder.() {
                                        crossfade(true)
                                    }).build()
                            )
                            Image(
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.FillHeight,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(76.dp),
                            )
                        }

                    })
                    StarRating(rank = book.rank, maxRank = 5, activeColor = Color.Yellow)
                }

                Spacer(Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = book.title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .fillMaxWidth(),
                        fontSize = 20.sp, // Adjust the text size as desired
                        fontWeight = FontWeight.Bold, // Make the text bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Author: ${book.author}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Publisher: ${book.publisher}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            DescriptionAndBuy(book = book, onClickBuy = onClickBuy)
        }
    }


}

@Composable
private fun ExploreImageContainer(content: @Composable () -> Unit) {
    Surface(Modifier.height(height = 120.dp), RoundedCornerShape(4.dp)) {
        content()
    }
}

@Composable
fun StarRating(rank: Int, maxRank: Int = 5, activeColor: Color = Color.Yellow) {
    Row {
        repeat(maxRank) { index ->
            val starColor = if (index < rank) activeColor else Color.Gray
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier
                    .width(20.dp)
                    .then(Modifier.padding(2.dp)),
                tint = starColor
            )
        }
    }
}

@Composable
fun DescriptionAndBuy(book: Book, onClickBuy: (url: String) -> Unit) {
    var isShowingDecoding by rememberSaveable {
        mutableStateOf(false)
    }
    val angle = if (isShowingDecoding) {
        180F
    } else {
        360F
    }
    val rotationAngle by animateFloatAsState(
        targetValue = angle,
        animationSpec = tween(
            durationMillis = 300, easing = LinearEasing
        ),
        label = "",
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { isShowingDecoding = !isShowingDecoding }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Description:")
                Spacer(modifier = Modifier.width(8.dp))
                Image(painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_48),
                    contentDescription = "Description",
                    modifier = Modifier
                        .rotate(rotationAngle)
                        .drawBehind { drawCircle(color = md_theme_light_surfaceTint) }
                )
            }
        }

        Spacer(Modifier.weight(1F))
        Button(
            onClick = { onClickBuy(book.buyLinks[0].url) },
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = if (book.price != "0.00")
                    "Buy${book.price}"
                else {
                    "Buy"
                }
            )
        }
    }
    AnimatedVisibility(isShowingDecoding) {
        Text(text = book.description)
    }

}
