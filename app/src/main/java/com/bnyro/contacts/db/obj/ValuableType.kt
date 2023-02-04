package com.bnyro.contacts.db.obj

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "valuableTypes")
data class ValuableType(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactId: Long,
    val category: Int,
    val value: String,
    val type: Int?
)
