package org.neteinstein.pickaname.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.neteinstein.pickaname.data.local.database.NameDao
import org.neteinstein.pickaname.data.mapper.toDomain
import org.neteinstein.pickaname.data.mapper.toEntityCode
import org.neteinstein.pickaname.data.mapper.toFilterInitial
import org.neteinstein.pickaname.domain.model.NameEntry
import org.neteinstein.pickaname.domain.model.NameFilter
import org.neteinstein.pickaname.domain.repository.NameRepository

class NameRepositoryImpl(
    private val nameDao: NameDao
) : NameRepository {

    override fun observeNames(filter: NameFilter): Flow<List<NameEntry>> =
        nameDao.observeNames(
            gender = filter.gender?.toEntityCode(),
            initial = filter.initial?.let { "$it".toFilterInitial() },
            query = filter.query.trim().ifBlank { null }
        ).map { entities -> entities.map { it.toDomain() } }

    override fun observeNameCount(filter: NameFilter): Flow<Int> =
        nameDao.observeCount(
            gender = filter.gender?.toEntityCode(),
            initial = filter.initial?.let { "$it".toFilterInitial() },
            query = filter.query.trim().ifBlank { null }
        )

    override fun observeIsEmpty(): Flow<Boolean> =
        nameDao.observeTotalCount().map { total -> total == 0 }
}
