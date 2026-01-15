package org.neteinstein.pickaname.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.neteinstein.pickaname.data.database.NameDao
import org.neteinstein.pickaname.data.database.NameEntity
import org.neteinstein.pickaname.domain.model.Name

class NameRepositoryTest {
    
    private lateinit var nameDao: NameDao
    private lateinit var repository: NameRepository
    
    @Before
    fun setup() {
        nameDao = mockk()
        repository = NameRepository(nameDao)
    }
    
    @Test
    fun `getAllowedNames returns mapped domain models`() = runTest {
        // Given
        val entities = listOf(
            NameEntity(1, "João", "M", 1, "Portuguese name"),
            NameEntity(2, "Maria", "F", 1, "")
        )
        coEvery { nameDao.getAllowedNames() } returns flowOf(entities)
        
        // When/Then
        repository.getAllowedNames().test {
            val names = awaitItem()
            assertThat(names).hasSize(2)
            assertThat(names[0].name).isEqualTo("João")
            assertThat(names[0].gender).isEqualTo(Name.Gender.MALE)
            assertThat(names[1].name).isEqualTo("Maria")
            assertThat(names[1].gender).isEqualTo(Name.Gender.FEMALE)
            awaitComplete()
        }
    }
    
    @Test
    fun `searchNames filters correctly`() = runTest {
        // Given
        val searchQuery = "João"
        val entities = listOf(
            NameEntity(1, "João", "M", 1, "")
        )
        coEvery { nameDao.searchNames(searchQuery) } returns flowOf(entities)
        
        // When/Then
        repository.searchNames(searchQuery).test {
            val names = awaitItem()
            assertThat(names).hasSize(1)
            assertThat(names[0].name).isEqualTo("João")
            awaitComplete()
        }
    }
    
    @Test
    fun `getNameById returns correct name`() = runTest {
        // Given
        val nameId = 1L
        val entity = NameEntity(1, "João", "M", 1, "Portuguese name")
        coEvery { nameDao.getNameById(nameId) } returns entity
        
        // When
        val result = repository.getNameById(nameId)
        
        // Then
        assertThat(result).isNotNull()
        assertThat(result?.name).isEqualTo("João")
        assertThat(result?.gender).isEqualTo(Name.Gender.MALE)
        coVerify { nameDao.getNameById(nameId) }
    }
    
    @Test
    fun `getNameById returns null when not found`() = runTest {
        // Given
        val nameId = 999L
        coEvery { nameDao.getNameById(nameId) } returns null
        
        // When
        val result = repository.getNameById(nameId)
        
        // Then
        assertThat(result).isNull()
    }
    
    @Test
    fun `isDatabasePopulated returns true when count greater than zero`() = runTest {
        // Given
        coEvery { nameDao.getCount() } returns 100
        
        // When
        val result = repository.isDatabasePopulated()
        
        // Then
        assertThat(result).isTrue()
    }
    
    @Test
    fun `isDatabasePopulated returns false when count is zero`() = runTest {
        // Given
        coEvery { nameDao.getCount() } returns 0
        
        // When
        val result = repository.isDatabasePopulated()
        
        // Then
        assertThat(result).isFalse()
    }
}
