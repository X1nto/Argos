package dev.xinto.argos.domain.semester

import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.DomainResponseSource
import dev.xinto.argos.network.ArgosApi
import kotlinx.coroutines.flow.mapNotNull

class SemesterRepository(
    private val argosApi: ArgosApi
) {

    private val semesters = DomainResponseSource(
        fetch = {
            argosApi.getSemesters()
        },
        transform = { response ->
            response.data!!.map { (id, attributes) ->
                DomainSemester(
                    name = attributes.name,
                    id = id!!,
                    active = attributes.isActive
                )
            }
        }
    )

    fun getSemesters() = semesters.asFlow()
    fun getActiveSemester() = getSemesters().mapNotNull {
        when (it) {
            is DomainResponse.Loading -> it
            is DomainResponse.Error -> it
            is DomainResponse.Success -> {
                it.value.firstOrNull { it.active }?.let {
                    DomainResponse.Success(it)
                }
            }
        }
    }

}