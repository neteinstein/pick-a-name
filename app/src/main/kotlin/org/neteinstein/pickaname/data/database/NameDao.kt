package org.neteinstein.pickaname.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Name entities.
 */
@Dao
interface NameDao {
    
    /**
     * Get all allowed names (where NAMES_ALLOWED = 1)
     */
    @Query("SELECT * FROM TABLE_NAMES WHERE NAMES_ALLOWED = 1 ORDER BY _id ASC")
    fun getAllowedNames(): Flow<List<NameEntity>>
    
    /**
     * Search names by name pattern
     */
    @Query("SELECT * FROM TABLE_NAMES WHERE NAMES_NAME LIKE '%' || :searchQuery || '%' ORDER BY _id ASC")
    fun searchNames(searchQuery: String): Flow<List<NameEntity>>
    
    /**
     * Get a specific name by ID
     */
    @Query("SELECT * FROM TABLE_NAMES WHERE _id = :nameId")
    suspend fun getNameById(nameId: Long): NameEntity?
    
    /**
     * Get a specific name by ID as Flow
     */
    @Query("SELECT * FROM TABLE_NAMES WHERE _id = :nameId")
    fun getNameByIdFlow(nameId: Long): Flow<NameEntity?>
    
    /**
     * Insert names into the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNames(names: List<NameEntity>)
    
    /**
     * Check if database has data
     */
    @Query("SELECT COUNT(*) FROM TABLE_NAMES")
    suspend fun getCount(): Int
}
