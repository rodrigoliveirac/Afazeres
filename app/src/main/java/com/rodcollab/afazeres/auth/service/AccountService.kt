package com.rodcollab.afazeres.auth.service

import kotlinx.coroutines.flow.Flow

interface AccountService {

    val currentUserId: String
    val hasUser: Boolean

    val currentUser: Flow<User>

    suspend fun signInWithEmailAndPassword(email:String, password: String, isSuccessful: (Boolean,String) -> Unit)

    suspend fun authenticate(email:String, password:String)

    suspend fun createAccount(email:String, password:String, isSuccessful: (Boolean) -> Unit)
}