package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.KonsultasiBumdesEntity
import id.go.purbalinggakab.bumdes.bumdes.repository.KonsultasiBumdesRepository
import id.go.purbalinggakab.bumdes.error.DeleteDataException
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.CreateKonsultasiBumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateKonsultasiBumdesRequest
import id.go.purbalinggakab.bumdes.model.response.KonsultasiBumdesResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.service.KonsultasiBumdesService
import id.go.purbalinggakab.bumdes.service.KeycloakAuthService
import id.go.purbalinggakab.bumdes.specification.FilterMapper
import id.go.purbalinggakab.bumdes.specification.FilterRequestUtil
import id.go.purbalinggakab.bumdes.specification.FilterSpecification
import id.go.purbalinggakab.bumdes.validation.ValidationUtil
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
class KonsultasiBumdesServiceImpl(
    val konsultasiBumdesRepository: KonsultasiBumdesRepository,
    val filterRequestUtil: FilterRequestUtil,
    val validationUtil: ValidationUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<KonsultasiBumdesEntity>
) : KonsultasiBumdesService {
    override fun create(createKonsultasiBumdesRequest: CreateKonsultasiBumdesRequest): KonsultasiBumdesResponse {
        validationUtil.validate(createKonsultasiBumdesRequest)

        val konsultasiBumdesEntity = KonsultasiBumdesEntity(
            id = "",
            judul = createKonsultasiBumdesRequest.judul,
            deskripsi = createKonsultasiBumdesRequest.deskripsi,
            createdAt = Date(),
            createdBy = createKonsultasiBumdesRequest.created_by,
            updatedAt = null,
            updatedBy = null,
            deletedAt = null,
            deletedBy = null
        )

        val result = konsultasiBumdesRepository.save(konsultasiBumdesEntity)

        return convertToResponse(result)
    }

    override fun list(
        requestParams: RequestParams,
        filter: Map<String, String>
    ): ListResponse<KonsultasiBumdesResponse> {
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

        val list = konsultasiBumdesRepository.findAll(generateFilter(filter), pageable)

        val items: List<KonsultasiBumdesEntity> = list.get().collect(Collectors.toList())

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

    override fun get(id: String): KonsultasiBumdesResponse {
        val response = findByIdOrThrowNotFound(id)
        return convertToResponse(response)
    }

    override fun update(id: String, updateKonsultasiBumdesRequest: UpdateKonsultasiBumdesRequest): KonsultasiBumdesResponse {
        val result = findByIdOrThrowNotFound(id)
        validationUtil.validate(result)

        result.apply {
            judul = updateKonsultasiBumdesRequest.judul
            deskripsi = updateKonsultasiBumdesRequest.deskripsi
            updatedBy = updateKonsultasiBumdesRequest.updated_by
            updatedAt = Date()
        }
        konsultasiBumdesRepository.save(result)
        return convertToResponse(result)
    }

    override fun delete(id: String, deleteRequest: DeleteRequest): String {
        val price = findByIdOrThrowNotFound(id)

        try {
            if(deleteRequest.softDelete == true || deleteRequest.softDelete == null){
                price.apply {
                    isDeleted = true
                    deletedBy = deleteRequest.deletedBy
                    deletedAt = Date()
                }
                konsultasiBumdesRepository.save(price)
            }else{
                konsultasiBumdesRepository.delete(price)
            }
        } catch (e: Exception) {
            println("error -> $e")
            throw DeleteDataException()
        }
        return "Delete Successfully"
    }

    override fun deleteList(ids: List<String>): String {
        konsultasiBumdesRepository.deleteAllById(ids)
        return "Delete Successfully"
    }

    private fun convertToResponse(konsultasiBumdesEntity: KonsultasiBumdesEntity): KonsultasiBumdesResponse {

        return KonsultasiBumdesResponse(
            id = konsultasiBumdesEntity.id,
            judul = konsultasiBumdesEntity.judul,
            deskripsi = konsultasiBumdesEntity.deskripsi,
            created_at = konsultasiBumdesEntity.createdAt.formateDateTime(),
            created_by = konsultasiBumdesEntity.createdBy,
            updated_at = if (konsultasiBumdesEntity.updatedAt == null) null else konsultasiBumdesEntity.updatedAt!!.formateDateTime(),
            updated_by = konsultasiBumdesEntity.updatedBy,
            deleted_at = if (konsultasiBumdesEntity.deletedAt == null) null else konsultasiBumdesEntity.deletedAt!!.formateDateTime(),
            deleted_by = konsultasiBumdesEntity.deletedBy,
        )
    }

    private fun generateFilter(filter: Map<String, String>): Specification<KonsultasiBumdesEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }

    private fun findByIdOrThrowNotFound(id: String): KonsultasiBumdesEntity{
        val result = konsultasiBumdesRepository.findByIdOrNull(id)
        if (result == null){
            throw NotFoundException()
        }else{
            return result
        }
    }
}