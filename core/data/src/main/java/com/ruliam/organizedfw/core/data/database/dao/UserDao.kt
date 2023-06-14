//package com.odisby.organizedfw.core.data.database.dao
//
//import androidx.room.*
//import com.odisby.organizedfw.core.data.database.entity.User
//
//@Dao
//internal interface UserDao {
//
//    @Query("SELECT * FROM users WHERE uuid = :userId")
//    suspend fun getUserById(userId: String): User
//
//    @Query("SELECT * FROM users WHERE isMainUser = 0")
//    suspend fun getGroupUsers(): List<User>
//
//    @Query("SELECT * FROM users")
//    suspend fun getAllUsers(): List<User>
//
//    @Query("SELECT * FROM users WHERE isMainUser = 1")
//    suspend fun getMainUserId(): User?
//
//    @Insert
//    suspend fun insert(user: User)
//
//    @Query("UPDATE users SET balance = :newBalance WHERE uuid = :userId")
//    suspend fun updateBalance(userId: String, newBalance: Double)
//
//    @Query("DELETE FROM users")
//    suspend fun delete()
//}