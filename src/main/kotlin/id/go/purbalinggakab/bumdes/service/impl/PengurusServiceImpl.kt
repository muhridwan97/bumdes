package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.PengurusEntity
import id.go.purbalinggakab.bumdes.bumdes.repository.PengurusRepository
import id.go.purbalinggakab.bumdes.error.DeleteDataException
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.PengurusRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.PengurusResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.service.PengurusService
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
class PengurusServiceImpl(
    val pengurusRepository: PengurusRepository,
    val filterRequestUtil: FilterRequestUtil,
    val validationUtil: ValidationUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<PengurusEntity>
) : PengurusService {
    override fun create(principal: Principal, pengurusRequest: PengurusRequest): PengurusResponse {
        validationUtil.validate(pengurusRequest)

        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        val pengurusEntity = PengurusEntity(
            id = "",
            namaPengurus = pengurusRequest.nama_pengurus,
            createdAt = Date(),
            createdBy = user.username,
            updatedAt = null,
            updatedBy = null,
            deletedAt = null,
            deletedBy = null
        )

        val result = pengurusRepository.save(pengurusEntity)

        return convertToResponse(result)
    }

    override fun list(
        principal: Principal,
        requestParams: RequestParams,
        filter: Map<String, String>
    ): ListResponse<PengurusResponse> {
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

        val list = pengurusRepository.findAll(generateFilter(filter), pageable)

        val items: List<PengurusEntity> = list.get().collect(Collectors.toList())

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

    override fun get(principal: Principal, id: String): PengurusResponse {
        val response = findByIdOrThrowNotFound(id)
        return convertToResponse(response)
    }

    override fun update(principal: Principal, id: String, pengurusRequest: PengurusRequest): PengurusResponse {
        val result = findByIdOrThrowNotFound(id)
        validationUtil.validate(result)
        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        result.apply {
            namaPengurus = pengurusRequest.nama_pengurus
            updatedBy = user.username
            updatedAt = Date()
        }
        pengurusRepository.save(result)
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
                pengurusRepository.save(price)
            }else{
                pengurusRepository.delete(price)
            }
        } catch (e: Exception) {
            println("error -> $e")
            throw DeleteDataException()
        }
        return "Delete Successfully"
    }

    override fun deleteList(principal: Principal, ids: List<String>): String {
        pengurusRepository.deleteAllById(ids)
        return "Delete Successfully"
    }

    private fun convertToResponse(pengurusEntity: PengurusEntity): PengurusResponse {

        return PengurusResponse(
            id = pengurusEntity.id,
            nama_pengurus = pengurusEntity.namaPengurus,
            created_at = pengurusEntity.createdAt.formateDateTime(),
            created_by = pengurusEntity.createdBy,
            updated_at = if (pengurusEntity.updatedAt == null) null else pengurusEntity.updatedAt!!.formateDateTime(),
            updated_by = pengurusEntity.updatedBy,
            deleted_at = if (pengurusEntity.deletedAt == null) null else pengurusEntity.deletedAt!!.formateDateTime(),
            deleted_by = pengurusEntity.deletedBy,
        )
    }

    private fun generateFilter(filter: Map<String, String>): Specification<PengurusEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }

    private fun findByIdOrThrowNotFound(id: String): PengurusEntity{
        val result = pengurusRepository.findByIdOrNull(id)
        if (result == null){
            throw NotFoundException()
        }else{
            return result
        }
    }
}