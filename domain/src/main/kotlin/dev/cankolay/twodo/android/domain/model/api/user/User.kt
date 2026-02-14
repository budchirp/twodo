package dev.cankolay.twodo.android.domain.model.api.user

data class User(
    val id: String,

    val name: String,

    val picture: String?,

    val couple: Couple?
)
