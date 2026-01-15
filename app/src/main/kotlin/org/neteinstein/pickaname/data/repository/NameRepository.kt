package org.neteinstein.pickaname.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.neteinstein.pickaname.data.database.NameDao
import org.neteinstein.pickaname.data.database.NameEntity
import org.neteinstein.pickaname.domain.model.Name
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing name data.
 * Provides a clean API for accessing name data.
 */
@Singleton
class NameRepository @Inject constructor(
    private val nameDao: NameDao
) {
    
    /**
     * Get all allowed names
     */
    fun getAllowedNames(): Flow<List<Name>> {
        return nameDao.getAllowedNames().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Search names by query
     */
    fun searchNames(query: String): Flow<List<Name>> {
        return nameDao.searchNames(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get a specific name by ID
     */
    suspend fun getNameById(nameId: Long): Name? {
        return nameDao.getNameById(nameId)?.toDomainModel()
    }
    
    /**
     * Get a specific name by ID as Flow
     */
    fun getNameByIdFlow(nameId: Long): Flow<Name?> {
        return nameDao.getNameByIdFlow(nameId).map { it?.toDomainModel() }
    }
    
    /**
     * Check if database is populated
     */
    suspend fun isDatabasePopulated(): Boolean {
        return nameDao.getCount() > 0
    }
    
    /**
     * Extension function to convert NameEntity to domain model
     */
    private fun NameEntity.toDomainModel(): Name {
        return Name(
            id = id,
            name = name,
            gender = Name.Gender.fromString(gender),
            notes = notes
        )
    }
}
