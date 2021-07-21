package com.example.mynotes.data.remote.model

import java.io.Serializable

data class User(
    val name: String?=null,
    val email:String,
    val password: String
): Serializable