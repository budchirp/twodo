package dev.cankolay.twodo.android.domain.model.api

object ApiConstants {
    const val API_URL = "192.168.1.8:8081"

    object Endpoints {
        const val USER = "user"

        const val INITIALIZE = "$USER/initialize"

        const val COUPLE = "couple"
        const val COUPLE_LEAVE = "$COUPLE/leave"
        
        const val INVITE = "$USER/invite"
        const val INVITES = "$INVITE/all"

        const val TODO = "todo"
        const val TODOS = "$TODO/all"
    }
}

object AuthApiConstants {
    private const val AUTH_APPLICATION_ID = "9c0c3097-6442-44af-b159-384423c287f0"
    const val AUTH_URL =
        "http://192.168.1.8:3000/en/authorize?id=${AUTH_APPLICATION_ID}&permissions=user:read&callback=twodo://authenticate"
}