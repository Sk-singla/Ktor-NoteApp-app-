package com.example.mynotes.data.remote.model

import java.io.Serializable

data class RemoteResponse(
        val success:Boolean,
        val message:String
):Serializable