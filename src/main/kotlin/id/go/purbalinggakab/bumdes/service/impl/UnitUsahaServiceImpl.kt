package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.UnitUsahaEntity
import id.go.purbalinggakab.bumdes.bumdes.repository.UnitUsahaRepository
import id.go.purbalinggakab.bumdes.dami.repository.DesaRepository
import id.go.purbalinggakab.bumdes.dami.repository.KecamatanRepository
import id.go.purbalinggakab.bumdes.error.DeleteDataException
import id.go.purbalinggakab.bumdes.error.NamaUnitUsahaException
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.error.NullException
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.UnitUsahaRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.UnitUsahaResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.service.UnitUsahaService
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
class UnitUsahaServiceImpl(
    val unitUsahaRepository: UnitUsahaRepository,
    val filterRequestUtil: FilterRequestUtil,
    val validationUtil: ValidationUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<UnitUsahaEntity>
) : UnitUsahaService {
    override fun create(principal: Principal, unitUsahaRequest: UnitUsahaRequest): UnitUsahaResponse {
        validationUtil.validate(unitUsahaRequest)
        val cekNoUnitUsaha = unitUsahaRepository.getUnitUsahaByNamaUnitUsaha(unitUsahaRequest.nama_unit_usaha)
        if (cekNoUnitUsaha != null){
            throw NamaUnitUsahaException(unitUsahaRequest.nama_unit_usaha)
        }

        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        val unitUsahaEntity = UnitUsahaEntity(
            id = "",
            namaUnitUsaha = unitUsahaRequest.nama_unit_usaha,
            kerjaSama = unitUsahaRequest.kerja_sama,
            createdAt = Date(),
            createdBy = user.username,
            updatedAt = null,
            updatedBy = null,
            deletedAt = null,
            deletedBy = null
        )

        val result = unitUsahaRepository.save(unitUsahaEntity)

        return convertToResponse(result)
    }

    override fun list(
        principal: Principal,
        requestParams: RequestParams,
        filter: Map<String, String>
    ): ListResponse<UnitUsahaResponse> {
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

        val list = unitUsahaRepository.findAll(generateFilter(filter), pageable)

        val items: List<UnitUsahaEntity> = list.get().collect(Collectors.toList())

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

    override fun get(principal: Principal, id: String): UnitUsahaResponse {
        val response = findByIdOrThrowNotFound(id)
        return convertToResponse(response)
    }

    override fun update(principal: Principal, id: String, unitUsahaRequest: UnitUsahaRequest): UnitUsahaResponse {
        val result = findByIdOrThrowNotFound(id)
        validationUtil.validate(result)

        val cekNoUnitUsaha = unitUsahaRepository.getUnitUsahaByNamaUnitUsaha(unitUsahaRequest.nama_unit_usaha)
        if (cekNoUnitUsaha != null){
            throw NamaUnitUsahaException(unitUsahaRequest.nama_unit_usaha)
        }

        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        result.apply {
            namaUnitUsaha = unitUsahaRequest.nama_unit_usaha
            kerjaSama = unitUsahaRequest.kerja_sama
            updatedBy = user.username
            updatedAt = Date()
        }
        unitUsahaRepository.save(result)
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
                unitUsahaRepository.save(price)
            }else{
                unitUsahaRepository.delete(price)
            }
        } catch (e: Exception) {
            println("error -> $e")
            throw DeleteDataException()
        }
        return "Delete Successfully"
    }

    override fun deleteList(principal: Principal, ids: List<String>): String {
        unitUsahaRepository.deleteAllById(ids)
        return "Delete Successfully"
    }

    private fun convertToResponse(unitUsahaEntity: UnitUsahaEntity): UnitUsahaResponse {

        return UnitUsahaResponse(
            id = unitUsahaEntity.id,
            nama_unit_usaha = unitUsahaEntity.namaUnitUsaha,
            kerja_sama = unitUsahaEntity.kerjaSama,
            created_at = unitUsahaEntity.createdAt.formateDateTime(),
            created_by = unitUsahaEntity.createdBy,
            updated_at = if (unitUsahaEntity.updatedAt == null) null else unitUsahaEntity.updatedAt!!.formateDateTime(),
            updated_by = unitUsahaEntity.updatedBy,
            deleted_at = if (unitUsahaEntity.deletedAt == null) null else unitUsahaEntity.deletedAt!!.formateDateTime(),
            deleted_by = unitUsahaEntity.deletedBy,
        )
    }

    private fun generateFilter(filter: Map<String, String>): Specification<UnitUsahaEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }

    private fun findByIdOrThrowNotFound(id: String): UnitUsahaEntity{
        val result = unitUsahaRepository.findByIdOrNull(id)
        if (result == null){
            throw NotFoundException()
        }else{
            return result
        }
    }
}