//package com.odisby.organizedfw.core.data.database
//
//import android.content.Context
//import androidx.room.*
//import com.odisby.organizedfw.core.data.database.dao.FinanceDao
//import com.odisby.organizedfw.core.data.database.dao.UserDao
//import com.odisby.organizedfw.core.data.database.entity.Finance
//import com.odisby.organizedfw.core.data.database.entity.User
//
//@Database(entities = [Finance::class, User::class], version = 1, exportSchema = false)
////@TypeConverters(GroupUserConverter::class)
//internal abstract class AppDatabase : RoomDatabase() {
//
//    abstract fun financeDao(): FinanceDao
//
//    abstract fun userDao(): UserDao
//
//    companion object {
//
//        private var instance: AppDatabase? = null
//
//        fun getInstance(context: Context): AppDatabase {
//            if (instance == null) {
//                synchronized(AppDatabase::class) {
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        AppDatabase::class.java, DATABASE_NAME
//                    )
//                        .build()
//                }
//            }
//            return instance!!
//        }
//
//        private const val DATABASE_NAME = "app-database.db"
//    }
//}