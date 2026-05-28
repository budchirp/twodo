package dev.cankolay.twodo.android.domain.model.api.user

data class User(
    val id: String,

    val username: String,

    val name: String,

    val picture: String?,

    val gender: Gender?,

    val profileCompleted: Boolean,

    val couple: Couple?
)
