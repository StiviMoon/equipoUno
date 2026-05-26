package com.example.pb.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pb.model.Reto


@Database(entities = [Reto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract fun RetoDao(): RetoDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pico_botella_db"
                ).build().also{INSTANCE=it }
            }
        }
    }
}