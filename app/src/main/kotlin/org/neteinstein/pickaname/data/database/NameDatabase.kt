package org.neteinstein.pickaname.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Room database for Pick-A-Name app.
 */
@Database(
    entities = [NameEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NameDatabase : RoomDatabase() {
    
    abstract fun nameDao(): NameDao
    
    companion object {
        @Volatile
        private var INSTANCE: NameDatabase? = null
        
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): NameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NameDatabase::class.java,
                    "names.db"
                )
                    .addCallback(NameDatabaseCallback(context, scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        private class NameDatabaseCallback(
            private val context: Context,
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Populate database on creation
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.nameDao(), context)
                    }
                }
            }
        }
        
        /**
         * Populate database from assets file
         */
        private suspend fun populateDatabase(nameDao: NameDao, context: Context) {
            var successCount = 0
            var failureCount = 0
            
            try {
                val inputStream = InputStreamReader(
                    context.assets.open("database.data"),
                    "UTF-8"
                )
                val reader = BufferedReader(inputStream)
                val names = mutableListOf<NameEntity>()
                
                reader.useLines { lines ->
                    lines.forEach { line ->
                        if (line.isNotBlank() && line.startsWith("INSERT INTO TABLE_NAMES")) {
                            // Parse INSERT statement
                            // Format: INSERT INTO TABLE_NAMES(%1, %2, %3, %4, %5) VALUES (id,'name','gender',allowed,'notes');
                            val valuesStart = line.indexOf("VALUES (") + 8
                            val valuesEnd = line.lastIndexOf(");")
                            if (valuesStart > 8 && valuesEnd > valuesStart) {
                                val values = line.substring(valuesStart, valuesEnd)
                                val parts = parseValues(values)
                                
                                if (parts.size >= 5) {
                                    try {
                                        val id = parts[0].toLong()
                                        val name = parts[1]
                                        val gender = parts[2]
                                        val allowed = parts[3].toInt()
                                        val notes = parts[4]
                                        
                                        names.add(
                                            NameEntity(
                                                id = id,
                                                name = name,
                                                gender = gender,
                                                allowed = allowed,
                                                notes = notes
                                            )
                                        )
                                        successCount++
                                    } catch (e: NumberFormatException) {
                                        // Log parsing error but continue processing
                                        android.util.Log.w("NameDatabase", "Failed to parse entry: ${e.message}")
                                        failureCount++
                                    }
                                } else {
                                    android.util.Log.w("NameDatabase", "Invalid entry format: insufficient parts")
                                    failureCount++
                                }
                            }
                        }
                    }
                }
                
                // Insert all names in batches
                names.chunked(500).forEach { batch ->
                    nameDao.insertNames(batch)
                }
                
                android.util.Log.i("NameDatabase", "Database populated: $successCount entries succeeded, $failureCount failed")
            } catch (e: Exception) {
                android.util.Log.e("NameDatabase", "Error populating database", e)
            }
        }
        
        /**
         * Parse VALUES clause from SQL INSERT statement
         * Handles quoted strings with commas inside
         */
        private fun parseValues(values: String): List<String> {
            val result = mutableListOf<String>()
            var current = StringBuilder()
            var inQuotes = false
            var i = 0
            
            while (i < values.length) {
                val char = values[i]
                when {
                    char == '\'' -> {
                        inQuotes = !inQuotes
                    }
                    char == ',' && !inQuotes -> {
                        result.add(current.toString().trim())
                        current = StringBuilder()
                    }
                    else -> {
                        current.append(char)
                    }
                }
                i++
            }
            
            // Add the last value
            if (current.isNotEmpty()) {
                result.add(current.toString().trim())
            }
            
            return result
        }
    }
}
