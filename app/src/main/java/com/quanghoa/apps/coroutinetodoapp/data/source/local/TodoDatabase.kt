package com.quanghoa.apps.coroutinetodoapp.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.quanghoa.apps.coroutinetodoapp.data.Task

/**
 * The Room Database that contains the Tasks table.
 */
@Database(entities = arrayOf(Task::class), version = 1)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun taskDao(): TasksDao

    companion object {
        private var INSTANCE: TodoDatabase? = null

        private val lock = Any()

        fun getIntance(context: Context): TodoDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        TodoDatabase::class.java, "Tasks.db"
                    )
                        .build()
                }
            }
            return INSTANCE!!
        }
    }

}