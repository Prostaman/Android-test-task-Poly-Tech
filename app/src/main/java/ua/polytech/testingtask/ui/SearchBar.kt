package ua.polytech.testingtask.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.filter
import ua.polytech.testingtask.R
import ua.polytech.testingtask.ui.theme.md_theme_light_onSurfaceVariant

@Composable
fun SearchCategoryInput(onTextChanged: (String) -> Unit,
                        editableUserInputState: EditableUserInputState,
                        hintText: String,
                        onTextEmpty: () -> Unit) {
    EditableUserInput(
        state = editableUserInputState,
        hintText =  hintText,
        onTextEmpty =  onTextEmpty
    )

    val currentOnTextChanged by rememberUpdatedState(onTextChanged)
    LaunchedEffect(editableUserInputState) {
        snapshotFlow { editableUserInputState.text }
            .filter { !editableUserInputState.isHint }
            .collect {
                currentOnTextChanged(editableUserInputState.text)
            }
    }
}

@Composable
fun EditableUserInput(
    state: EditableUserInputState = rememberEditableUserInputState(""),
    hintText: String = "",
    onTextEmpty: () -> Unit
) {
    BaseUserInput {
        Box {
            BasicTextField(
                value = state.text,
                onValueChange = { state.updateText(it) },
                cursorBrush = SolidColor(LocalContentColor.current),
                textStyle = TextStyle(color = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .alpha(if (state.text.isEmpty()) 0.5f else 1f)
            )
            if (state.text.isEmpty()) {
                onTextEmpty()
                Text(
                    text = hintText,
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BaseUserInput(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
        onClick = onClick,
        color = md_theme_light_onSurfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(Modifier.padding(all = 12.dp)) {
            Icon(
                modifier = Modifier.size(24.dp, 24.dp),
                painter = painterResource(id = R.drawable.baseline_search_24),
                tint = Color(0x80FFFFFF),
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Row(
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                content()
            }
        }
    }
}
@Composable
fun rememberEditableUserInputState(hint: String): EditableUserInputState =
    rememberSaveable(hint, saver = EditableUserInputState.Saver) {
        EditableUserInputState(hint, hint)
    }

class EditableUserInputState(private val hint: String, initialText: String) {

    var text by mutableStateOf(initialText)
        private set

    fun updateText(newText: String) {
        text = newText
    }

    val isHint: Boolean
        get() = text == hint

    companion object {
        val Saver: Saver<EditableUserInputState, *> = listSaver(
            save = { listOf(it.hint, it.text) },
            restore = {
                EditableUserInputState(
                    hint = it[0],
                    initialText = it[1],
                )
            }
        )
    }
}