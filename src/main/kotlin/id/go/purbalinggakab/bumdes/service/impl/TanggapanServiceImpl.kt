package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.TanggapanEntity
import id.go.purbalinggakab.bumdes.bumdes.repository.KonsultasiBumdesRepository
import id.go.purbalinggakab.bumdes.bumdes.repository.TanggapanRepository
import id.go.purbalinggakab.bumdes.bumdes.repository.UserRepository
import id.go.purbalinggakab.bumdes.error.DeleteDataException
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.CreateTanggapanRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateTanggapanRequest
import id.go.purbalinggakab.bumdes.model.response.TanggapanResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.service.TanggapanService
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
class TanggapanServiceImpl(
    val tanggapanRepository: TanggapanRepository,
    val userRepository: UserRepository,
    val konsultasiBumdesRepository: KonsultasiBumdesRepository,
    val filterRequestUtil: FilterRequestUtil,
    val validationUtil: ValidationUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<TanggapanEntity>
) : TanggapanService {
    override fun create(createTanggapanRequest: CreateTanggapanRequest): TanggapanResponse {
        validationUtil.validate(createTanggapanRequest)

        val tanggapanEntity = TanggapanEntity(
            id = "",
            idKonsultasi = createTanggapanRequest.id_konsultasi,
            konsultasiBumdes = null,
            tipe = createTanggapanRequest.tipe,
            pesan = createTanggapanRequest.pesan,
            createdAt = Date(),
            createdBy = createTanggapanRequest.created_by,
            updatedAt = null,
            updatedBy = null,
            deletedAt = null,
            deletedBy = null
        )

        val result = tanggapanRepository.save(tanggapanEntity)

        return convertToResponse(result)
    }

    override fun list(
        requestParams: RequestParams,
        filter: Map<String, String>
    ): ListResponse<TanggapanResponse> {
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

        val list = tanggapanRepository.findAll(generateFilter(filter), pageable)

        val items: List<TanggapanEntity> = list.get().collect(Collectors.toList())

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

    override fun get(id: String): TanggapanResponse {
        val response = findByIdOrThrowNotFound(id)
        return convertToResponse(response)
    }

    override fun update(id: String, updateTanggapanRequest: UpdateTanggapanRequest): TanggapanResponse {
        val result = findByIdOrThrowNotFound(id)
        validationUtil.validate(result)

        result.apply {
            idKonsultasi = updateTanggapanRequest.id_konsultasi
            tipe = updateTanggapanRequest.tipe
            pesan = updateTanggapanRequest.pesan
            updatedBy = updateTanggapanRequest.updated_by
            updatedAt = Date()
        }
        tanggapanRepository.save(result)
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
                tanggapanRepository.save(price)
            }else{
                tanggapanRepository.delete(price)
            }
        } catch (e: Exception) {
            println("error -> $e")
            throw DeleteDataException()
        }
        return "Delete Successfully"
    }

    override fun deleteList(ids: List<String>): String {
        tanggapanRepository.deleteAllById(ids)
        return "Delete Successfully"
    }

    private fun convertToResponse(tanggapanEntity: TanggapanEntity): TanggapanResponse {
        val user = userRepository.findByIdOrNull(tanggapanEntity.createdBy)
        if (tanggapanEntity.konsultasiBumdes == null){
            tanggapanEntity.konsultasiBumdes = konsultasiBumdesRepository.findByIdOrNull(tanggapanEntity.idKonsultasi)
        }
        return TanggapanResponse(
            id = tanggapanEntity.id,
            id_konsultasi = tanggapanEntity.idKonsultasi,
            judul_konsultasi = tanggapanEntity.konsultasiBumdes!!.judul,
            tipe = tanggapanEntity.tipe,
            pesan = tanggapanEntity.pesan,
            created_name = if (user == null) null else user!!.displayName!!,
            created_at = tanggapanEntity.createdAt.formateDateTime(),
            created_by = tanggapanEntity.createdBy,
            updated_at = if (tanggapanEntity.updatedAt == null) null else tanggapanEntity.updatedAt!!.formateDateTime(),
            updated_by = tanggapanEntity.updatedBy,
            deleted_at = if (tanggapanEntity.deletedAt == null) null else tanggapanEntity.deletedAt!!.formateDateTime(),
            deleted_by = tanggapanEntity.deletedBy,
        )
    }

    private fun generateFilter(filter: Map<String, String>): Specification<TanggapanEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }

    private fun findByIdOrThrowNotFound(id: String): TanggapanEntity{
        val result = tanggapanRepository.findByIdOrNull(id)
        if (result == null){
            throw NotFoundException()
        }else{
            return result
        }
    }
}