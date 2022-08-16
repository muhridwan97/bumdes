package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.BumdesBersamaEntity
import id.go.purbalinggakab.bumdes.bumdes.repository.BumdesRepository
import id.go.purbalinggakab.bumdes.bumdes.repository.BumdesBersamaRepository
import id.go.purbalinggakab.bumdes.dami.repository.DesaRepository
import id.go.purbalinggakab.bumdes.dami.repository.KecamatanRepository
import id.go.purbalinggakab.bumdes.error.DeleteDataException
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.BumdesBersamaRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.BumdesBersamaResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.service.BumdesBersamaService
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
class BumdesBersamaServiceImpl(
    val bumdesBersamaRepository: BumdesBersamaRepository,
    val bumdesRepository: BumdesRepository,
    val desaRepository: DesaRepository,
    val kecamatanRepository: KecamatanRepository,
    val filterRequestUtil: FilterRequestUtil,
    val validationUtil: ValidationUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<BumdesBersamaEntity>
) : BumdesBersamaService {
    override fun create(principal: Principal, bumdesBersamaRequest: BumdesBersamaRequest): BumdesBersamaResponse {
        validationUtil.validate(bumdesBersamaRequest)

        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        val bumdesBersamaEntity = BumdesBersamaEntity(
            id = "",
            idBumdes = bumdesBersamaRequest.id_bumdes,
            bumdes = null,
            idDesa = bumdesBersamaRequest.id_desa,
            idKecamatan = bumdesBersamaRequest.id_kecamatan,
            createdAt = Date(),
            createdBy = user.username,
            updatedAt = null,
            updatedBy = null,
            deletedAt = null,
            deletedBy = null
        )

        val result = bumdesBersamaRepository.save(bumdesBersamaEntity)
        changeTipeBumdes(principal,bumdesBersamaRequest.id_bumdes)

        return convertToResponse(result)
    }

    override fun list(
        principal: Principal,
        requestParams: RequestParams,
        filter: Map<String, String>
    ): ListResponse<BumdesBersamaResponse> {
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

        val list = bumdesBersamaRepository.findAll(generateFilter(filter), pageable)

        val items: List<BumdesBersamaEntity> = list.get().collect(Collectors.toList())

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

    override fun get(principal: Principal, id: String): BumdesBersamaResponse {
        val response = findByIdOrThrowNotFound(id)
        return convertToResponse(response)
    }

    override fun update(principal: Principal, id: String, bumdesBersamaRequest: BumdesBersamaRequest): BumdesBersamaResponse {
        val result = findByIdOrThrowNotFound(id)
        validationUtil.validate(result)
        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        result.apply {
            idBumdes = bumdesBersamaRequest.id_bumdes
            idDesa = bumdesBersamaRequest.id_desa
            idKecamatan = bumdesBersamaRequest.id_kecamatan
            updatedBy = user.username
            updatedAt = Date()
        }
        bumdesBersamaRepository.save(result)
        return convertToResponse(result)
    }

    override fun delete(principal: Principal, id: String, deleteRequest: DeleteRequest): String {
        val price = findByIdOrThrowNotFound(id)
        bumdesBersamaRepository.delete(price)
        changeTipeBumdes(principal,price.idBumdes)
        return "Delete Successfully"
    }

    override fun deleteList(principal: Principal, ids: List<String>): String {
        val id = ids.get(0)
        val bumdesBersama = findByIdOrThrowNotFound(id)
        bumdesBersamaRepository.deleteAllById(ids)
        changeTipeBumdes(principal,bumdesBersama.idBumdes)
        return "Delete Successfully"
    }

    private fun convertToResponse(bumdesBersamaEntity: BumdesBersamaEntity): BumdesBersamaResponse {

        if(bumdesBersamaEntity.bumdes == null){
            bumdesBersamaEntity.bumdes = bumdesRepository.findByIdOrNull(bumdesBersamaEntity.idBumdes)
        }

        val desa = desaRepository.findByIdOrNull(bumdesBersamaEntity.idDesa)
        val kecamatan = kecamatanRepository.findByIdOrNull(bumdesBersamaEntity.idKecamatan)

        return BumdesBersamaResponse(
            id = bumdesBersamaEntity.id,
            id_bumdes = bumdesBersamaEntity.idBumdes,
            nama_bumdes = bumdesBersamaEntity.bumdes!!.nama,
            id_desa = bumdesBersamaEntity.idDesa,
            nama_desa = desa!!.namaDesa,
            id_kecamatan = bumdesBersamaEntity.idKecamatan,
            nama_kecamatan = kecamatan!!.namaKecamatan,
            created_at = bumdesBersamaEntity.createdAt.formateDateTime(),
            created_by = bumdesBersamaEntity.createdBy,
            updated_at = if (bumdesBersamaEntity.updatedAt == null) null else bumdesBersamaEntity.updatedAt!!.formateDateTime(),
            updated_by = bumdesBersamaEntity.updatedBy,
            deleted_at = if (bumdesBersamaEntity.deletedAt == null) null else bumdesBersamaEntity.deletedAt!!.formateDateTime(),
            deleted_by = bumdesBersamaEntity.deletedBy,
        )
    }

    private fun generateFilter(filter: Map<String, String>): Specification<BumdesBersamaEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }

    private fun findByIdOrThrowNotFound(id: String): BumdesBersamaEntity{
        val result = bumdesBersamaRepository.findByIdOrNull(id)
        if (result == null){
            throw NotFoundException()
        }else{
            return result
        }
    }

    private fun changeTipeBumdes (principal: Principal, id: String){
        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)
        val bumdes = bumdesRepository.getReferenceById(id)
        val cekBumdesSama = bumdesBersamaRepository.existsBumdesBersamaByIdBumdesAndIdDesaAndIdKecamatan(id, bumdes.idDesa, bumdes.idKecamatan)
        val countBumdes = bumdesBersamaRepository.countBumdesBersamaByIdBumdes(id)

        if((cekBumdesSama && countBumdes>1) || (!cekBumdesSama && countBumdes>0)){
            val bumdes = bumdesRepository.findByIdOrNull(id)
            if (bumdes == null){
                throw NotFoundException()
            }else{
                bumdes.apply {
                    tipe = "bersama"
                    updatedBy = user.username
                    updatedAt = Date()
                }
                bumdesRepository.save(bumdes)
            }
        }else if((cekBumdesSama && countBumdes==1) || countBumdes == 0){
            val bumdes = bumdesRepository.findByIdOrNull(id)
            if (bumdes == null){
                throw NotFoundException()
            }else{
                bumdes.apply {
                    tipe = "sendiri"
                    updatedBy = user.username
                    updatedAt = Date()
                }
                bumdesRepository.save(bumdes)
            }
        }
    }
}