package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.BumdesEntity
import id.go.purbalinggakab.bumdes.bumdes.repository.BadanHukumRepository
import id.go.purbalinggakab.bumdes.bumdes.repository.BumdesRepository
import id.go.purbalinggakab.bumdes.bumdes.repository.UnitUsahaRepository
import id.go.purbalinggakab.bumdes.dami.repository.DesaRepository
import id.go.purbalinggakab.bumdes.dami.repository.KecamatanRepository
import id.go.purbalinggakab.bumdes.error.*
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.BumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateFileRequest
import id.go.purbalinggakab.bumdes.model.response.BumdesResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.service.BumdesService
import id.go.purbalinggakab.bumdes.service.KeycloakAuthService
import id.go.purbalinggakab.bumdes.specification.FilterMapper
import id.go.purbalinggakab.bumdes.specification.FilterRequestUtil
import id.go.purbalinggakab.bumdes.specification.FilterSpecification
import id.go.purbalinggakab.bumdes.validation.ValidationUtil
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.io.InputStream
import java.security.Principal
import java.util.*
import java.util.stream.Collectors

@Service
class BumdesServiceImpl(
    val bumdesRepository: BumdesRepository,
    val desaRepository: DesaRepository,
    val kecamatanRepository: KecamatanRepository,
    val badanHukumRepository: BadanHukumRepository,
    val unitUsahaRepository: UnitUsahaRepository,
    val filterRequestUtil: FilterRequestUtil,
    val validationUtil: ValidationUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<BumdesEntity>
) : BumdesService {

    @Autowired
    lateinit var minioClient: MinioClient

    @Value("\${minio.bucket.name}")
    lateinit var bucketName: String

    @Value("\${tes.ip}")
    lateinit var ipTes: String

    override fun create(principal: Principal, bumdesRequest: BumdesRequest): BumdesResponse {
        validationUtil.validate(bumdesRequest)

        val cekDesa = desaRepository.findByIdOrNull(bumdesRequest.id_desa)
        val cekKecamatan = kecamatanRepository.findByIdOrNull(bumdesRequest.id_kecamatan)

        if (cekDesa == null || cekKecamatan == null){
            throw NullException()
        }
        var fileName = ""
        if (bumdesRequest.file !== null) {
            var extension = bumdesRequest.file!!.contentType!!.substringAfterLast("/")

            if(extension.isEmpty()){
                extension = ".png"
            }else{
                extension = "."+extension
            }
            fileName = Date().time.toString()+extension
            try {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .`object`("/foto-bumdes/" + fileName)
                        .stream(bumdesRequest.file!!.inputStream, bumdesRequest.file!!.size, -1)
                        .contentType(bumdesRequest.file!!.contentType)
                        .build()
                )
            } catch (e: Exception) {
                println("error -> $e")
                throw UploadException()
            }
        }
        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        val bumdesEntity = BumdesEntity(
            id = "",
            idDesa = bumdesRequest.id_desa,
            idKecamatan = bumdesRequest.id_kecamatan,
            idBadanHukum = bumdesRequest.id_badan_hukum,
            badanHukum = null,
            idUnitUsaha = bumdesRequest.id_unit_usaha,
            unitUsaha = null,
            nama = bumdesRequest.nama,
            jumlahKontribusiPad = bumdesRequest.jumlah_kontribusi_pad,
            klasifikasi = bumdesRequest.klasifikasi,
            tipe = bumdesRequest.tipe,
            foto = fileName,
            createdAt = Date(),
            createdBy = user.username,
            updatedAt = null,
            updatedBy = null,
            deletedAt = null,
            deletedBy = null
        )

        val result = bumdesRepository.save(bumdesEntity)

        return convertToResponse(result)
    }

    override fun list(
        principal: Principal,
        requestParams: RequestParams,
        filter: Map<String, String>
    ): ListResponse<BumdesResponse> {
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

        val list = bumdesRepository.findAll(generateFilter(filter), pageable)

        val items: List<BumdesEntity> = list.get().collect(Collectors.toList())

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

    override fun getObject(filename: String): InputStream? {
        val stream: InputStream = try {
            minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`("/foto-bumdes/"+filename)
                    .build()
            )
        } catch (e: Exception) {
            println("error getObject -> $e")
            return null
        }
        return stream
    }

    override fun get(principal: Principal, id: String): BumdesResponse {
        val response = findByIdOrThrowNotFound(id)
        return convertToResponse(response)
    }

    override fun update(principal: Principal, id: String, bumdesRequest: BumdesRequest): BumdesResponse {
        val result = findByIdOrThrowNotFound(id)
        validationUtil.validate(result)

        val cekDesa = desaRepository.findByIdOrNull(bumdesRequest.id_desa)
        val cekKecamatan = kecamatanRepository.findByIdOrNull(bumdesRequest.id_kecamatan)

        if (cekDesa == null || cekKecamatan == null){
            throw NullException()
        }

        var fileName = result.foto
        if (bumdesRequest.file !== null) {
            var extension = bumdesRequest.file!!.contentType!!.substringAfterLast("/")

            if(extension.isEmpty()){
                extension = ".png"
            }else{
                extension = "."+extension
            }
            fileName = Date().time.toString()+extension
            try {
                minioClient.removeObject(
                    RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .`object`("/foto-bumdes/" + result.foto)
                        .build()
                )
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .`object`("/foto-bumdes/" + fileName)
                        .stream(bumdesRequest.file!!.inputStream, bumdesRequest.file!!.size, -1)
                        .contentType(bumdesRequest.file!!.contentType)
                        .build()
                )
            } catch (e: Exception) {
                println("error -> $e")
                throw UploadException()
            }
        }
        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        result.apply {
            idDesa = bumdesRequest.id_desa
            idKecamatan = bumdesRequest.id_kecamatan
            idBadanHukum = bumdesRequest.id_badan_hukum
            idUnitUsaha = bumdesRequest.id_unit_usaha
            nama = bumdesRequest.nama
            jumlahKontribusiPad = bumdesRequest.jumlah_kontribusi_pad
            klasifikasi = bumdesRequest.klasifikasi
            tipe = bumdesRequest.tipe
            foto = fileName
            updatedBy = user.username
            updatedAt = Date()
        }
        bumdesRepository.save(result)
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
                bumdesRepository.save(price)
            }else{
                bumdesRepository.delete(price)
            }
        } catch (e: Exception) {
            println("error -> $e")
            throw DeleteDataException()
        }
        return "Delete Successfully"
    }

    override fun deleteList(principal: Principal, ids: List<String>): String {
        bumdesRepository.deleteAllById(ids)
        return "Delete Successfully"
    }

    override fun updateFile(principal: Principal, id: String, updateFileRequest: UpdateFileRequest): BumdesResponse {
        val bumdes = findByIdOrThrowNotFound(id)
        validationUtil.validate(bumdes)
        var fileName = bumdes.foto
        if (updateFileRequest.file !== null) {
            var extension = updateFileRequest.file!!.contentType!!.substringAfterLast("/")

            if(extension.isEmpty()){
                extension = ".png"
            }else{
                extension = "."+extension
            }
            fileName = Date().time.toString()+extension
            try {
                minioClient.removeObject(
                    RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .`object`("/foto-bumdes/" + bumdes.foto)
                        .build()
                )
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .`object`("/foto-bumdes/" + fileName)
                        .stream(updateFileRequest.file!!.inputStream, updateFileRequest.file!!.size, -1)
                        .contentType(updateFileRequest.file!!.contentType)
                        .build()
                )
            } catch (e: Exception) {
                println("error -> $e")
                throw UploadException()
            }
        }

        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)
        bumdes.apply {
            foto = fileName
            updatedBy = user.username
            updatedAt = Date()
        }
        bumdesRepository.save(bumdes)
        return convertToResponse(bumdes)
    }

    private fun convertToResponse(bumdesEntity: BumdesEntity): BumdesResponse {
        if (bumdesEntity.badanHukum == null) {
            bumdesEntity.badanHukum = badanHukumRepository.findByIdOrNull(bumdesEntity.idBadanHukum)
        }
        if (bumdesEntity.unitUsaha == null) {
            bumdesEntity.unitUsaha = unitUsahaRepository.findByIdOrNull(bumdesEntity.idUnitUsaha)
        }
        val desa = desaRepository.findByIdOrNull(bumdesEntity.idDesa)
        val kecamatan = kecamatanRepository.findByIdOrNull(bumdesEntity.idKecamatan)

        return BumdesResponse(
            id = bumdesEntity.id,
            id_desa = bumdesEntity.idDesa,
            nama_desa = desa!!.namaDesa,
            id_kecamatan = bumdesEntity.idKecamatan,
            nama_kecamatan = kecamatan!!.namaKecamatan,
            id_badan_hukum = bumdesEntity.idBadanHukum,
            no_badan_hukum = bumdesEntity.badanHukum!!.noBadanHukum,
            id_unit_usaha = bumdesEntity.idUnitUsaha,
            nama_unit_usaha = bumdesEntity.unitUsaha!!.namaUnitUsaha,
            nama = bumdesEntity.nama,
            jumlah_kontribusi_pad = bumdesEntity.jumlahKontribusiPad,
            klasifikasi = bumdesEntity.klasifikasi,
            tipe = bumdesEntity.tipe,
            foto = ipTes+"/api/v1/bumdes/attachment/" + bumdesEntity.foto,
            created_at = bumdesEntity.createdAt.formateDateTime(),
            created_by = bumdesEntity.createdBy,
            updated_at = if (bumdesEntity.updatedAt == null) null else bumdesEntity.updatedAt!!.formateDateTime(),
            updated_by = bumdesEntity.updatedBy,
            deleted_at = if (bumdesEntity.deletedAt == null) null else bumdesEntity.deletedAt!!.formateDateTime(),
            deleted_by = bumdesEntity.deletedBy,
        )
    }

    private fun generateFilter(filter: Map<String, String>): Specification<BumdesEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }

    private fun findByIdOrThrowNotFound(id: String): BumdesEntity{
        val result = bumdesRepository.findByIdOrNull(id)
        if (result == null){
            throw NotFoundException()
        }else{
            return result
        }
    }
}