package com.rodcollab.afazeres.auth.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : AccountService {

    override val currentUserId: String get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean get() = auth.currentUser != null

    override val currentUser: Flow<User>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                this.trySend(auth.currentUser?.let { User(it.uid, it.displayName) } ?: User())
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
        isSuccessful: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LOGIN_USER", "signInWithEmail:success")
                    isSuccessful(task.isSuccessful, "Welcome, ${task.result.user?.displayName}!")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LOGIN_USER", "signInWithEmail:failure", task.exception)
                    isSuccessful(task.isSuccessful, task.exception?.message.toString())
                }
            }
    }

    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun createAccount(
        email: String,
        password: String,
        isSuccessful: (Boolean) -> Unit
    ) {
        auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("CREATE_USER_ACCOUNT", "createUserWithEmail:success")
                    isSuccessful(true)
                } else {
                    Log.w("CREATE_USER_ACCOUNT", "createUserWithEmail:failure", task.exception)
                    isSuccessful(false)
                }
            }
    }
}