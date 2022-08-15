package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.dami.entity.DesaEntity
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.DesaResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.dami.repository.DesaRepository
import id.go.purbalinggakab.bumdes.model.response.ItemResponse
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

    override fun get(id: Long): DesaResponse {
        val result = findById(id)

        return convertResponse(result)
    }

    override fun listAll(): List<ItemResponse<String>> {
        val list = desaRepository.findAll()

        return list.map {
            ItemResponse(
                id = it.id.toString(),
                value = it.id.toString(),
                label = it.namaDesa
            )
        }
    }

    override fun listByKec(idKec: Long): List<ItemResponse<String>> {
        val list = desaRepository.findAllByKodeKec(idKec)

        return list.map {
            ItemResponse(
                id = it.id.toString(),
                value = it.id.toString(),
                label = it.namaDesa
            )
        }
    }


    private fun convertResponse(desaEntity: DesaEntity): DesaResponse {
        return DesaResponse(
            id = desaEntity.id,
            kode_kab = desaEntity.kodeKab,
            kode_kec = desaEntity.kodeKec,
            kode_desa = desaEntity.kodeDesa,
            nama_desa = desaEntity.namaDesa,
            created_at = desaEntity.createdAt.formateDateTime(),
        )
    }

    private fun findById(id: Long): DesaEntity {
        val result =  desaRepository.findByIdOrNull(id)
        if (result == null) {
            throw NotFoundException()
        } else {
            return result;
        }

    }


    private fun generateFilter(filter: Map<String, String>): Specification<DesaEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }
}