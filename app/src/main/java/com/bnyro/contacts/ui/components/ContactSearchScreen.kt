package com.bnyro.contacts.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bnyro.contacts.R
import com.bnyro.contacts.obj.ContactData
import com.bnyro.contacts.obj.FilterOptions
import com.bnyro.contacts.ui.components.base.ClickableIcon
import com.bnyro.contacts.ui.components.base.FullScreenDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun ContactSearchScreen(
    contacts: List<ContactData>,
    filterOptions: FilterOptions,
    onDismissRequest: () -> Unit
) {
    FullScreenDialog(onDismissRequest) {
        val focusRequester = remember {
            FocusRequester()
        }

        LaunchedEffect(Unit) {
            delay(100)
            focusRequester.requestFocus()
        }

        var searchQuery by remember {
            mutableStateOf("")
        }
        var visibleContacts by remember {
            mutableStateOf(contacts)
        }
        LaunchedEffect(searchQuery) {
            withContext(Dispatchers.IO) {
                val query = searchQuery.lowercase()
                visibleContacts = contacts.filter {
                    val contactInfoStrings = listOf(it.numbers, it.emails, it.addresses, it.notes, it.websites, it.events)
                        .flatten()
                        .map { (value, _) -> value } + listOf(it.organization, it.nickName, it.displayName)

                    contactInfoStrings.filterNotNull().any { str ->
                        str.lowercase().contains(query)
                    }
                }
            }
        }

        Column(Modifier.fillMaxSize()) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
                    .focusRequester(focusRequester),
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(id = R.string.search)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        ClickableIcon(icon = Icons.Default.Close) {
                            searchQuery = ""
                        }
                    }
                },
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            ContactsList(
                contacts = visibleContacts,
                filterOptions = filterOptions,
                scrollConnection = null,
                selectedContacts = emptyList<ContactData>().toMutableList()
            )
        }
    }
}
