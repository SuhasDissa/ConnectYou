package com.bnyro.contacts.enums

import androidx.annotation.StringRes
import com.bnyro.contacts.R

enum class ContactsSource(@StringRes val stringRes: Int) {
    DEVICE(R.string.device),
    LOCAL(R.string.local)
}
