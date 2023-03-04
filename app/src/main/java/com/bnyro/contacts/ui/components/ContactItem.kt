package com.bnyro.contacts.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bnyro.contacts.enums.SortOrder
import com.bnyro.contacts.ext.contentColor
import com.bnyro.contacts.ext.notAName
import com.bnyro.contacts.obj.ContactData
import com.bnyro.contacts.ui.models.ContactsModel
import com.bnyro.contacts.ui.screens.SingleContactScreen
import com.bnyro.contacts.util.Preferences
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactItem(
    contact: ContactData,
    sortOrder: SortOrder,
    selected: Boolean,
    onSinglePress: () -> Boolean,
    onLongPress: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val viewModel: ContactsModel = viewModel()

    var showContactScreen by remember {
        mutableStateOf(false)
    }

    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .fillMaxWidth()
            .clip(shape)
            .combinedClickable(
                onClick = {
                    if (!onSinglePress()) showContactScreen = true
                },
                onLongClick = {
                    onLongPress.invoke()
                }
            ),
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val colorfulIcons = Preferences.getBoolean(Preferences.colorfulContactIconsKey, false)

            val backgroundColor = if (colorfulIcons) {
                remember {
                    Color(getRandomColor())
                }
            } else {
                MaterialTheme.colorScheme.primary
            }
            val contentColor = when {
                !colorfulIcons -> MaterialTheme.colorScheme.onPrimary
                else -> backgroundColor.contentColor()
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        shape = CircleShape,
                        color = backgroundColor
                    )
            ) {
                val thumbnail = contact.thumbnail ?: contact.photo
                if (selected) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = contentColor
                    )
                } else if (thumbnail == null) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = (contact.displayName?.firstOrNull() ?: "").toString(),
                        color = contentColor // MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        bitmap = thumbnail.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                when {
                    sortOrder == SortOrder.FIRSTNAME -> "${contact.firstName ?: ""} ${contact.surName ?: ""}"
                    sortOrder == SortOrder.LASTNAME && !contact.surName.notAName() && !contact.firstName.notAName() -> "${contact.surName}, ${contact.firstName}"
                    else -> contact.displayName.orEmpty()
                }.trim()
            )
        }
    }

    if (showContactScreen) {
        val data = runBlocking {
            viewModel.loadAdvancedContactData(contact)
        }
        SingleContactScreen(data) {
            showContactScreen = false
        }
    }
}

fun getRandomColor(): Int = (Math.random() * 16777215).toInt() or (0xFF shl 24)
