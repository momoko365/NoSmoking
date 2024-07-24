package com.example.somke.ui.DB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Data::class], version = 1, exportSchema = false)

abstract class DataBase: RoomDatabase() {
    abstract fun dataDAO(): DataDao
//    companion object {
//        @Volatile
//        private var INSTANCE: DataBase? = null
//
//        fun getDatabase(context: Context): DataBase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    DataBase::class.java,
//                    "tabako.db"
//                ).fallbackToDestructiveMigration().build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
}