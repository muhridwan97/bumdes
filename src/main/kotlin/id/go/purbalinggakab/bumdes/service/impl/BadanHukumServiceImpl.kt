package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.BadanHukumEntity
import id.go.purbalinggakab.bumdes.bumdes.repository.BadanHukumRepository
import id.go.purbalinggakab.bumdes.dami.repository.DesaRepository
import id.go.purbalinggakab.bumdes.dami.repository.KecamatanRepository
import id.go.purbalinggakab.bumdes.error.DeleteDataException
import id.go.purbalinggakab.bumdes.error.NoBadanHukumException
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.error.NullException
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.BadanHukumRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.BadanHukumResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.service.BadanHukumService
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
class BadanHukumServiceImpl(
    val badanHukumRepository: BadanHukumRepository,
    val desaRepository: DesaRepository,
    val kecamatanRepository: KecamatanRepository,
    val filterRequestUtil: FilterRequestUtil,
    val validationUtil: ValidationUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<BadanHukumEntity>
) : BadanHukumService {
    override fun create(principal: Principal, badanHukumRequest: BadanHukumRequest): BadanHukumResponse {
        validationUtil.validate(badanHukumRequest)

        val cekDesa = desaRepository.findByIdOrNull(badanHukumRequest.id_desa)
        val cekKecamatan = kecamatanRepository.findByIdOrNull(badanHukumRequest.id_kecamatan)

        if (cekDesa == null || cekKecamatan == null){
            throw NullException()
        }
        val cekNoBadanHukum = badanHukumRepository.getBadanHukumByNoBadanHukum(badanHukumRequest.no_badan_hukum)
        if (cekNoBadanHukum != null){
            throw NoBadanHukumException(badanHukumRequest.no_badan_hukum)
        }

        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        val badanHukumEntity = BadanHukumEntity(
            id = "",
            idDesa = badanHukumRequest.id_desa,
//            desa = null,
            idKecamatan = badanHukumRequest.id_kecamatan,
//            kecamatan = null,
            noBadanHukum = badanHukumRequest.no_badan_hukum,
            namaBumdes = badanHukumRequest.nama_bumdes,
            createdAt = Date(),
            createdBy = user.username,
            updatedAt = null,
            updatedBy = null,
            deletedAt = null,
            deletedBy = null
        )

        val result = badanHukumRepository.save(badanHukumEntity)

        return convertToResponse(result)
    }

    override fun list(
        principal: Principal,
        requestParams: RequestParams,
        filter: Map<String, String>
    ): ListResponse<BadanHukumResponse> {
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

        val list = badanHukumRepository.findAll(generateFilter(filter), pageable)

        val items: List<BadanHukumEntity> = list.get().collect(Collectors.toList())

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

    override fun get(principal: Principal, id: String): BadanHukumResponse {
        val response = findByIdOrThrowNotFound(id)
        return convertToResponse(response)
    }

    override fun update(principal: Principal, id: String, badanHukumRequest: BadanHukumRequest): BadanHukumResponse {
        val result = findByIdOrThrowNotFound(id)
        validationUtil.validate(result)

        val cekDesa = desaRepository.findByIdOrNull(badanHukumRequest.id_desa)
        val cekKecamatan = kecamatanRepository.findByIdOrNull(badanHukumRequest.id_kecamatan)

        if (cekDesa == null || cekKecamatan == null){
            throw NullException()
        }

        val cekNoBadanHukum = badanHukumRepository.getBadanHukumByNoBadanHukum(badanHukumRequest.no_badan_hukum)
        if (cekNoBadanHukum != null){
            throw NoBadanHukumException(badanHukumRequest.no_badan_hukum)
        }

        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        result.apply {
            idDesa = badanHukumRequest.id_desa
            idKecamatan = badanHukumRequest.id_kecamatan
            noBadanHukum = badanHukumRequest.no_badan_hukum
            namaBumdes = badanHukumRequest.nama_bumdes
            updatedBy = user.username
            updatedAt = Date()
        }
        badanHukumRepository.save(result)
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
                badanHukumRepository.save(price)
            }else{
                badanHukumRepository.delete(price)
            }
        } catch (e: Exception) {
            println("error -> $e")
            throw DeleteDataException()
        }
        return "Delete Successfully"
    }

    override fun deleteList(principal: Principal, ids: List<String>): String {
        badanHukumRepository.deleteAllById(ids)
        return "Delete Successfully"
    }

    private fun convertToResponse(badanHukumEntity: BadanHukumEntity): BadanHukumResponse {
//        if (badanHukumEntity.desa == null) {
//            badanHukumEntity.desa = desaRepository.findByIdOrNull(badanHukumEntity.idDesa)
//        }
//        if (badanHukumEntity.kecamatan == null) {
//            badanHukumEntity.kecamatan = kecamatanRepository.findByIdOrNull(badanHukumEntity.idKecamatan)
//        }
        val desa = desaRepository.findByIdOrNull(badanHukumEntity.idDesa)
        val kecamatan = kecamatanRepository.findByIdOrNull(badanHukumEntity.idKecamatan)

        return BadanHukumResponse(
            id = badanHukumEntity.id,
            id_desa = badanHukumEntity.idDesa,
            nama_desa = desa!!.namaDesa,
            id_kecamatan = badanHukumEntity.idKecamatan,
            nama_kecamatan = kecamatan!!.namaKecamatan,
            no_badan_hukum = badanHukumEntity.noBadanHukum,
            nama_bumdes = badanHukumEntity.namaBumdes,
            created_at = badanHukumEntity.createdAt.formateDateTime(),
            created_by = badanHukumEntity.createdBy,
            updated_at = if (badanHukumEntity.updatedAt == null) null else badanHukumEntity.updatedAt!!.formateDateTime(),
            updated_by = badanHukumEntity.updatedBy,
            deleted_at = if (badanHukumEntity.deletedAt == null) null else badanHukumEntity.deletedAt!!.formateDateTime(),
            deleted_by = badanHukumEntity.deletedBy,
        )
    }

    private fun generateFilter(filter: Map<String, String>): Specification<BadanHukumEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }

    private fun findByIdOrThrowNotFound(id: String): BadanHukumEntity{
        val result = badanHukumRepository.findByIdOrNull(id)
        if (result == null){
            throw NotFoundException()
        }else{
            return result
        }
    }
}