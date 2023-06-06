package com.odisby.organizedfw.core.data.session

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.odisby.organizedfw.core.data.model.UserDomain
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class SessionManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
    ) {


    fun createLoginSession(userId: String){
        sharedPreferences.edit().putBoolean(IS_LOGGED, true).apply()
        sharedPreferences.edit().putString(USER_ID, userId).apply()
    }
    fun getUserId(): String{
        val userId =  sharedPreferences.getString(USER_ID, "")
        if(!userId.isNullOrEmpty()){
            return userId
        }else {
            Log.w(TAG, "Acessando getUserId sem ter um userId disponível")
            throw IllegalStateException("UserId not found")
        }
    }

    fun checkLogin(): Boolean {
        return isLogged()
    }

    private fun isLogged(): Boolean {
        return if(firebaseAuth.currentUser != null){
            sharedPreferences.getBoolean(IS_LOGGED, false)
        } else{
            logoutUser()
            false
        }
    }

    fun logoutUser(): Boolean {
        return try{
            sharedPreferences.edit().remove(USER_ID).apply()
            sharedPreferences.edit().putBoolean(IS_LOGGED, false).apply()
            firebaseAuth.signOut()
            true
        } catch (e: java.lang.Exception){
            Log.w(TAG, "Fail in log out, error $e")
            false
        }
    }

    suspend fun getMainUser(): UserDomain {
        val userId = getUserId()
        val result = firebaseFirestore.collection("users")
            .document(userId)
            .get()
            .await()
        return result.toObject(UserDomain::class.java)!!
    }




    companion object{
        val USER_ID = "userId"
        val IS_LOGGED = "isLogged"
        const val TAG = "SessionManager"
    }
}