package com.example.mapchat.model

data class Users(
    val uuid: String,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val friends: List<String>?,
    val imageUrl: String?,
    val latitude: Double?,
    val longitude: Double?
) {
    constructor() : this("", "", "", "", listOf<String>(), "", 0.0, 0.0)
}