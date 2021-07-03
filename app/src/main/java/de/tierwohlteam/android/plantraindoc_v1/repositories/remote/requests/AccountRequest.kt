package de.tierwohlteam.android.plantraindoc_v1.repositories.remote.requests

data class AccountRequest (
    val name: String,
    val eMail: String,
    val password: String,
    val id: String
)