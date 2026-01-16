package org.neteinstein.pickaname.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a name in the database.
 * 
 * Table structure matches the legacy SQLite database:
 * - _id: Primary key
 * - NAMES_NAME: The name itself
 * - NAMES_GENDER: Gender (M/F or empty)
 * - NAMES_ALLOWED: Whether the name is allowed (1) or not (0)
 * - NAMES_NOTES: Optional notes about the name
 */
@Entity(tableName = "TABLE_NAMES")
data class NameEntity(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long,
    
    @ColumnInfo(name = "NAMES_NAME")
    val name: String,
    
    @ColumnInfo(name = "NAMES_GENDER")
    val gender: String,
    
    @ColumnInfo(name = "NAMES_ALLOWED")
    val allowed: Int,
    
    @ColumnInfo(name = "NAMES_NOTES")
    val notes: String
)
