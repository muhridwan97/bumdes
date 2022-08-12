package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.DesaEntity
import id.go.purbalinggakab.bumdes.exception.NotFoundException
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.DesaRequest
import id.go.purbalinggakab.bumdes.model.response.DesaResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.bumdes.repository.DesaRepository
import id.go.purbalinggakab.bumdes.service.KeycloakAuthService
import id.go.purbalinggakab.bumdes.service.DesaService
import id.go.purbalinggakab.bumdes.specification.FilterMapper
import id.go.purbalinggakab.bumdes.specification.FilterRequestUtil
import id.go.purbalinggakab.bumdes.specification.FilterSpecification
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.security.Principal
import java.util.*
import java.util.stream.Collectors

@Service
class DesaServiceImpl(
    val desaRepository: DesaRepository,
    val filterRequestUtil: FilterRequestUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<DesaEntity>
) : DesaService {

    override fun list(principal: Principal, requestParams: RequestParams, filter: Map<String, String>): ListResponse<DesaResponse> {
        val size = if (requestParams.size!! == -1) {
            Integer.MAX_VALUE
        } else {
            requestParams.size
        }
        val pageable = PageRequest.of(
            requestParams.page!!,
            size,
            filterRequestUtil.toSortBy(requestParams.sortBy!!)
        )

        val user = keycloakAuthService.getUserInfo(principal)
        val filters = mutableMapOf<String, String>()
//        filters.put("created_by", "equal:${user.username}")
        filters.putAll(filter)

        val list = desaRepository.findAll(generateFilter(filters), pageable)
//        val list = desaRepository.findAllByCreatedBy(user.username, pageable)

        val items: List<DesaEntity> = list.get().collect(Collectors.toList())
        return ListResponse(
            items = items.map { convertResponse(it) },
            paging = PagingResponse(
                item_per_page = list.size,
                page = list.number,
                total_item = list.totalElements,
                total_page = list.totalPages
            ),
            sorting = filterRequestUtil.toSortResponse(requestParams.sortBy)
        )
    }

    override fun get(id: String): DesaResponse {
        val result = findById(id)

        return convertResponse(result)
    }

    override fun create(principal: Principal, desaRequest: DesaRequest): DesaResponse {
        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        val entity = DesaEntity(
            id = "",
            namaDesa = desaRequest.nama_desa,
            createdAt = Date(),
            createdBy = user.username,
            updatedAt = null,
            updatedBy = null,
            deletedAt = null,
            deletedBy = null
        )

        val result = desaRepository.save(entity)

        return convertResponse(result)
    }

    override fun update(id: String, desaRequest: DesaRequest): DesaResponse {
        val result = findById(id)

        result.apply {
            namaDesa = desaRequest.nama_desa
            updatedAt = Date()
        }

        desaRepository.save(result)

        return convertResponse(result)
    }

    override fun delete(id: String) {
        val result = findById(id)

        desaRepository.delete(result)
    }

    override fun deleteList(ids: List<String>): String {
        desaRepository.deleteAllById(ids)
        return "Delete Successfully"
    }

    private fun convertResponse(desaEntity: DesaEntity): DesaResponse {
        return DesaResponse(
            id = desaEntity.id,
            nama_desa = desaEntity.namaDesa,
            created_at = desaEntity.createdAt.formateDateTime(),
            created_by = desaEntity.createdBy,
            updated_at = if (desaEntity.updatedAt == null) null else desaEntity.updatedAt!!.formateDateTime(),
            updated_by = desaEntity.updatedBy,
            deleted_at = if (desaEntity.deletedAt == null) null else desaEntity.deletedAt!!.formateDateTime(),
            deleted_by = desaEntity.deletedBy,
        )
    }

    private fun findById(id: String): DesaEntity {
        return desaRepository.findByIdOrNull(id) ?: throw NotFoundException(id)
    }


    private fun generateFilter(filter: Map<String, String>): Specification<DesaEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }
}