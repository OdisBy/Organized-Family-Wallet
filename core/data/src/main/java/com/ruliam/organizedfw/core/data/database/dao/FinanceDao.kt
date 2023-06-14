//package com.odisby.organizedfw.core.data.database.dao
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.odisby.organizedfw.core.data.database.entity.Finance
//
//@Dao
//internal interface FinanceDao {
//
//    @Query(
//        """
//        SELECT * FROM finance
//        WHERE strftime('%m', date(date/1000, 'unixepoch')) = :month
//        """
//    )
//    suspend fun getMonthFinances(month: String): List<Finance>
//
//    @Query(
//        """
//        SELECT * FROM finance WHERE userId = :userId
//        AND strftime('%m', date(date/1000, 'unixepoch')) = :month
//        """
//    )
//    suspend fun getMonthByUser(userId: String, month: String): List<Finance>
//
//
//    @Query("SELECT * FROM finance WHERE userId LIKE '%'||:userId||'%'")
//    suspend fun fetchAllByUser(userId: String): List<Finance>
//
//    @Query("SELECT * FROM finance")
//    suspend fun getAllFinances(): List<Finance>
//
//    @Insert(onConflict = OnConflictStrategy.ABORT)
//    suspend fun insert(finance: Finance)
//}