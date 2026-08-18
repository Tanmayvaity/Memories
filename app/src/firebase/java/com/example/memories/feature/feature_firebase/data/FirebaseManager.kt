package com.example.memories.feature.feature_firebase.data


import com.example.memories.feature.feature_firebase.domain.model.FirebaseUserData
import com.example.memories.feature.feature_firebase.domain.model.SignUpResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class FirebaseManager {
    private val auth: FirebaseAuth = Firebase.auth

    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    fun getCurrentUser(): FirebaseUserData? = auth.currentUser?.toUserData()

    fun signOut() {
        auth.signOut()
    }

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): SignUpResult {
        val normalizedEmail = email.trim()
        if (normalizedEmail.isBlank()) return SignUpResult.InvalidEmail
        if (password.isBlank()) return SignUpResult.WeakPassword

        return try {
            val result = auth.createUserWithEmailAndPassword(normalizedEmail, password).await()
            val user = result.user
            if (user == null) {
                SignUpResult.Unknown(IllegalStateException("Firebase returned no user after sign-up"))
            } else {
                SignUpResult.Success(user)
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            SignUpResult.EmailAlreadyInUse
        } catch (e: FirebaseAuthWeakPasswordException) {
            SignUpResult.WeakPassword
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            SignUpResult.InvalidEmail
        } catch (e: FirebaseNetworkException) {
            SignUpResult.NoNetwork
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            SignUpResult.Unknown(e)
        }
    }

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): SignUpResult {
        val normalizedEmail = email.trim()
        if (normalizedEmail.isBlank()) return SignUpResult.InvalidEmail
        if (password.isBlank()) return SignUpResult.InvalidEmail

        return try {
            val result = auth.signInWithEmailAndPassword(normalizedEmail, password).await()
            val user = result.user
            if (user == null) {
                SignUpResult.Unknown(IllegalStateException("Firebase returned no user after sign-in"))
            } else {
                SignUpResult.Success(user)
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            SignUpResult.InvalidEmail
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            SignUpResult.InvalidEmail
        } catch (e: FirebaseNetworkException) {
            SignUpResult.NoNetwork
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            SignUpResult.Unknown(e)
        }
    }

    private fun FirebaseUser.toUserData(): FirebaseUserData {
        return FirebaseUserData(
            uid = uid,
            email = email,
            displayName = displayName,
        )
    }
}