package de.tierwohlteam.android.plantraindoc_v1.repositories.remote.requests

import kotlinx.serialization.Serializable

// This is the WebLoginRequest on Server side
@Serializable
data class IDRequest (
    val name: String,
    val eMail: String,
    val password: String,
)

