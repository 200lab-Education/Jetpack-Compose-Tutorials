package com.smarttoolfactory.tutorial1_1basics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel


/**
 * This is a stateless TextField for searching with a Hint when query is empty,
 * and clear and loading [IconButtons]s to clear query or show progress indicator when
 * a query is in progress.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchTextField(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    searching: Boolean,
    removeFocus:Boolean,
    modifier: Modifier = Modifier
) {

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    if (removeFocus) {
        onClearQuery()
        focusManager.clearFocus()
        val keyboardController = LocalSoftwareKeyboardController.current
        keyboardController?.hide()
    }


    Surface(
        modifier = modifier
            .then(
                Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 16.dp)
            ),
        color = Color(0xffF5F5F5),
        shape = RoundedCornerShape(percent = 50),
    ) {

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = modifier
            ) {

                if (query.text.isEmpty()) {
                    SearchHint(modifier.padding(start = 24.dp, end = 8.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .onFocusChanged {
                                onSearchFocusChange(it.isFocused)
                            }
                            .focusRequester(focusRequester)
                            .padding(top = 9.dp, bottom = 8.dp, start = 24.dp, end = 8.dp),
                        singleLine = true
                    )

                    when {
                        searching -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(horizontal = 6.dp)
                                    .size(36.dp)
                            )
                        }
                        query.text.isNotEmpty() -> {
                            IconButton(onClick = onClearQuery) {
                                Icon(imageVector = Icons.Filled.Cancel, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun SearchHint(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)

    ) {
        Text(
            color = Color(0xff757575),
            text = "Search a Tag or Description",
        )
    }
}

@Composable
fun rememberSearchState(
    query: TextFieldValue = TextFieldValue(""),
    focused: Boolean = false,
    searching: Boolean = false,
    suggestions: List<TutorialSectionModel> = emptyList(),
    searchResults: List<TutorialSectionModel> = emptyList()
): SearchState {
    return remember {
        SearchState(
            query = query,
            focused = focused,
            searching = searching,
            suggestions = suggestions,
            searchResults = searchResults
        )
    }
}

@Stable
class SearchState(
    query: TextFieldValue,
    focused: Boolean,
    searching: Boolean,
    suggestions: List<TutorialSectionModel>,
    searchResults: List<TutorialSectionModel>
) {
    var query by mutableStateOf(query)
    var focused by mutableStateOf(focused)
    var searching by mutableStateOf(searching)
    var suggestions by mutableStateOf(suggestions)
    var searchResults by mutableStateOf(searchResults)

    var searchDisplay: SearchDisplay  = SearchDisplay.InitialResults

        get() = when {
            !focused && query.text.isEmpty() -> SearchDisplay.InitialResults
            focused && query.text.isEmpty() -> SearchDisplay.Suggestions
            searchResults.isEmpty() -> SearchDisplay.NoResults
            else -> SearchDisplay.Results
        }

    override fun toString(): String {
        return "🚀 State query: $query, focused: $focused, searching: $searching "+
            "suggestions: ${suggestions.size}, "+
            "searchResults: ${searchResults.size}, " +
           " searchDisplay: $searchDisplay"

    }
}

/**
 * Enum class with different values to set search state based on text, focus, initial state and
 * results from search.
 *
 * **InitialResults** represents the initial state before search is initiated. This represents
 * the whole screen
 *
 */
enum class SearchDisplay {
    InitialResults, Suggestions, Results, NoResults
}

