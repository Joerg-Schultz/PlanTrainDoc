package de.tierwohlteam.android.plantraindoc.repositories.remote.requests

data class AccountRequest (
    val name: String,
    val eMail: String,
    val password: String,
    val id: String
)