package com.ddeeaaddllyy.zenith.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nedovolen_account")
data class NedovolenAccountEntity(
    @PrimaryKey val id: Int = 1,
    val login: String,
    val isLoggedIn: Boolean,
    val loggedInAt: Long
)
