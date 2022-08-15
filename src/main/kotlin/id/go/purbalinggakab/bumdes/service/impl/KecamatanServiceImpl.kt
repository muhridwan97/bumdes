package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.dami.entity.KecamatanEntity
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.KecamatanResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.dami.repository.KecamatanRepository
import id.go.purbalinggakab.bumdes.model.response.ItemResponse
import id.go.purbalinggakab.bumdes.service.KeycloakAuthService
import id.go.purbalinggakab.bumdes.service.KecamatanService
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
class KecamatanServiceImpl(
    val kecamatanRepository: KecamatanRepository,
    val filterRequestUtil: FilterRequestUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<KecamatanEntity>
) : KecamatanService {

    override fun list(principal: Principal, requestParams: RequestParams, filter: Map<String, String>): ListResponse<KecamatanResponse> {
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

        val list = kecamatanRepository.findAll(generateFilter(filters), pageable)
//        val list = kecamatanRepository.findAllByCreatedBy(user.username, pageable)

        val items: List<KecamatanEntity> = list.get().collect(Collectors.toList())
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

    override fun get(id: Long): KecamatanResponse {
        val result = findById(id)

        return convertResponse(result)
    }

    override fun listAll(): List<ItemResponse<String>> {
        val list = kecamatanRepository.findAll()

        return list.map {
            ItemResponse(
                id = it.id.toString(),
                value = it.id.toString(),
                label = it.namaKecamatan
            )
        }
    }


    private fun convertResponse(kecamatanEntity: KecamatanEntity): KecamatanResponse {
        return KecamatanResponse(
            id = kecamatanEntity.id,
            kode_kab = kecamatanEntity.kodeKab,
            kode_kec = kecamatanEntity.kodeKec,
            kode_skpd = kecamatanEntity.kodeSkpd,
            nama_kecamatan = kecamatanEntity.namaKecamatan,
            created_at = kecamatanEntity.createdAt.formateDateTime(),
        )
    }

    private fun findById(id: Long): KecamatanEntity {
        val result =  kecamatanRepository.findByIdOrNull(id)
        if (result == null) {
            throw NotFoundException()
        } else {
            return result;
        }

    }


    private fun generateFilter(filter: Map<String, String>): Specification<KecamatanEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }
}