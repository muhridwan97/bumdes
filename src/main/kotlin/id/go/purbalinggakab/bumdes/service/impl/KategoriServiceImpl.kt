package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.KategoriEntity
import id.go.purbalinggakab.bumdes.bumdes.repository.KategoriRepository
import id.go.purbalinggakab.bumdes.error.DeleteDataException
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.KategoriRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.KategoriResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.service.KategoriService
import id.go.purbalinggakab.bumdes.service.KeycloakAuthService
import id.go.purbalinggakab.bumdes.specification.FilterMapper
import id.go.purbalinggakab.bumdes.specification.FilterRequestUtil
import id.go.purbalinggakab.bumdes.specification.FilterSpecification
import id.go.purbalinggakab.bumdes.validation.ValidationUtil
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.security.Principal
import java.util.*
import java.util.stream.Collectors

@Service
class KategoriServiceImpl(
    val kategoriRepository: KategoriRepository,
    val filterRequestUtil: FilterRequestUtil,
    val validationUtil: ValidationUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<KategoriEntity>
) : KategoriService {
    override fun create(principal: Principal, kategoriRequest: KategoriRequest): KategoriResponse {
        validationUtil.validate(kategoriRequest)

        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        val kategoriEntity = KategoriEntity(
            id = "",
            namaKategori = kategoriRequest.nama_kategori,
            createdAt = Date(),
            createdBy = user.username,
            updatedAt = null,
            updatedBy = null,
            deletedAt = null,
            deletedBy = null
        )

        val result = kategoriRepository.save(kategoriEntity)

        return convertToResponse(result)
    }

    override fun list(
        principal: Principal,
        requestParams: RequestParams,
        filter: Map<String, String>
    ): ListResponse<KategoriResponse> {
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

        val list = kategoriRepository.findAll(generateFilter(filter), pageable)

        val items: List<KategoriEntity> = list.get().collect(Collectors.toList())

        return ListResponse(
            items = items.map { convertToResponse(it) },
            paging = PagingResponse(
                item_per_page = list.size,
                page = list.number,
                total_item = list.totalElements,
                total_page = list.totalPages
            ),
            sorting = filterRequestUtil.toSortResponse(requestParams.sortBy)
        )
    }

    override fun get(principal: Principal, id: String): KategoriResponse {
        val response = findByIdOrThrowNotFound(id)
        return convertToResponse(response)
    }

    override fun update(principal: Principal, id: String, kategoriRequest: KategoriRequest): KategoriResponse {
        val result = findByIdOrThrowNotFound(id)
        validationUtil.validate(result)
        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        result.apply {
            namaKategori = kategoriRequest.nama_kategori
            updatedBy = user.username
            updatedAt = Date()
        }
        kategoriRepository.save(result)
        return convertToResponse(result)
    }

    override fun delete(principal: Principal, id: String, deleteRequest: DeleteRequest): String {
        val price = findByIdOrThrowNotFound(id)

        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        try {
            if(deleteRequest.softDelete == true || deleteRequest.softDelete == null){
                price.apply {
                    isDeleted = true
                    deletedBy = user.username
                    deletedAt = Date()
                }
                kategoriRepository.save(price)
            }else{
                kategoriRepository.delete(price)
            }
        } catch (e: Exception) {
            println("error -> $e")
            throw DeleteDataException()
        }
        return "Delete Successfully"
    }

    override fun deleteList(principal: Principal, ids: List<String>): String {
        kategoriRepository.deleteAllById(ids)
        return "Delete Successfully"
    }

    private fun convertToResponse(kategoriEntity: KategoriEntity): KategoriResponse {

        return KategoriResponse(
            id = kategoriEntity.id,
            nama_kategori = kategoriEntity.namaKategori,
            created_at = kategoriEntity.createdAt.formateDateTime(),
            created_by = kategoriEntity.createdBy,
            updated_at = if (kategoriEntity.updatedAt == null) null else kategoriEntity.updatedAt!!.formateDateTime(),
            updated_by = kategoriEntity.updatedBy,
            deleted_at = if (kategoriEntity.deletedAt == null) null else kategoriEntity.deletedAt!!.formateDateTime(),
            deleted_by = kategoriEntity.deletedBy,
        )
    }

    private fun generateFilter(filter: Map<String, String>): Specification<KategoriEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }

    private fun findByIdOrThrowNotFound(id: String): KategoriEntity{
        val result = kategoriRepository.findByIdOrNull(id)
        if (result == null){
            throw NotFoundException()
        }else{
            return result
        }
    }
}