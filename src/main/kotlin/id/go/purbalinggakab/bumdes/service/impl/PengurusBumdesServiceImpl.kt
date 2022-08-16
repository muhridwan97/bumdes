package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.PengurusBumdesEntity
import id.go.purbalinggakab.bumdes.bumdes.repository.BumdesRepository
import id.go.purbalinggakab.bumdes.bumdes.repository.PengurusBumdesRepository
import id.go.purbalinggakab.bumdes.bumdes.repository.PengurusRepository
import id.go.purbalinggakab.bumdes.error.DeleteDataException
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.PengurusBumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.PengurusBumdesResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.service.PengurusBumdesService
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
class PengurusBumdesServiceImpl(
    val pengurusBumdesRepository: PengurusBumdesRepository,
    val bumdesRepository: BumdesRepository,
    val pengurusRepository: PengurusRepository,
    val filterRequestUtil: FilterRequestUtil,
    val validationUtil: ValidationUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<PengurusBumdesEntity>
) : PengurusBumdesService {
    override fun create(principal: Principal, pengurusBumdesRequest: PengurusBumdesRequest): PengurusBumdesResponse {
        validationUtil.validate(pengurusBumdesRequest)

        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        val pengurusBumdesEntity = PengurusBumdesEntity(
            id = "",
            idBumdes = pengurusBumdesRequest.id_bumdes,
            bumdes = null,
            idPengurus= pengurusBumdesRequest.id_pengurus,
            pengurus = null,
            jabatan = pengurusBumdesRequest.jabatan,
            createdAt = Date(),
            createdBy = user.username,
            updatedAt = null,
            updatedBy = null,
            deletedAt = null,
            deletedBy = null
        )

        val result = pengurusBumdesRepository.save(pengurusBumdesEntity)

        return convertToResponse(result)
    }

    override fun list(
        principal: Principal,
        requestParams: RequestParams,
        filter: Map<String, String>
    ): ListResponse<PengurusBumdesResponse> {
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

        val list = pengurusBumdesRepository.findAll(generateFilter(filter), pageable)

        val items: List<PengurusBumdesEntity> = list.get().collect(Collectors.toList())

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

    override fun get(principal: Principal, id: String): PengurusBumdesResponse {
        val response = findByIdOrThrowNotFound(id)
        return convertToResponse(response)
    }

    override fun update(principal: Principal, id: String, pengurusBumdesRequest: PengurusBumdesRequest): PengurusBumdesResponse {
        val result = findByIdOrThrowNotFound(id)
        validationUtil.validate(result)
        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        result.apply {
            idBumdes = pengurusBumdesRequest.id_bumdes
            idPengurus = pengurusBumdesRequest.id_pengurus
            jabatan = pengurusBumdesRequest.jabatan
            updatedBy = user.username
            updatedAt = Date()
        }
        pengurusBumdesRepository.save(result)
        return convertToResponse(result)
    }

    override fun delete(principal: Principal, id: String, deleteRequest: DeleteRequest): String {
        val price = findByIdOrThrowNotFound(id)
        //always hardcode
        pengurusBumdesRepository.delete(price)
        return "Delete Successfully"
    }

    override fun deleteList(principal: Principal, ids: List<String>): String {
        pengurusBumdesRepository.deleteAllById(ids)
        return "Delete Successfully"
    }

    private fun convertToResponse(pengurusBumdesEntity: PengurusBumdesEntity): PengurusBumdesResponse {

        if(pengurusBumdesEntity.bumdes == null){
            pengurusBumdesEntity.bumdes = bumdesRepository.findByIdOrNull(pengurusBumdesEntity.idBumdes)
        }

        if (pengurusBumdesEntity.pengurus == null){
            pengurusBumdesEntity.pengurus = pengurusRepository.findByIdOrNull(pengurusBumdesEntity.idPengurus)
        }

        return PengurusBumdesResponse(
            id = pengurusBumdesEntity.id,
            id_bumdes = pengurusBumdesEntity.idBumdes,
            nama_bumdes = pengurusBumdesEntity.bumdes!!.nama,
            id_pengurus = pengurusBumdesEntity.idPengurus,
            nama_pengurus = pengurusBumdesEntity.pengurus!!.namaPengurus,
            jabatan = pengurusBumdesEntity.jabatan,
            created_at = pengurusBumdesEntity.createdAt.formateDateTime(),
            created_by = pengurusBumdesEntity.createdBy,
            updated_at = if (pengurusBumdesEntity.updatedAt == null) null else pengurusBumdesEntity.updatedAt!!.formateDateTime(),
            updated_by = pengurusBumdesEntity.updatedBy,
            deleted_at = if (pengurusBumdesEntity.deletedAt == null) null else pengurusBumdesEntity.deletedAt!!.formateDateTime(),
            deleted_by = pengurusBumdesEntity.deletedBy,
        )
    }

    private fun generateFilter(filter: Map<String, String>): Specification<PengurusBumdesEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }

    private fun findByIdOrThrowNotFound(id: String): PengurusBumdesEntity{
        val result = pengurusBumdesRepository.findByIdOrNull(id)
        if (result == null){
            throw NotFoundException()
        }else{
            return result
        }
    }
}