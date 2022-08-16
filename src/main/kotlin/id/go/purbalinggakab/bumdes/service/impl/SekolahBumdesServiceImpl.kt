package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.SekolahBumdesEntity
import id.go.purbalinggakab.bumdes.bumdes.repository.KategoriRepository
import id.go.purbalinggakab.bumdes.bumdes.repository.SekolahBumdesRepository
import id.go.purbalinggakab.bumdes.error.DeleteDataException
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.error.UploadException
import id.go.purbalinggakab.bumdes.extensions.formateDate
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.SekolahBumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateFileRequest
import id.go.purbalinggakab.bumdes.model.response.SekolahBumdesResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.service.SekolahBumdesService
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
import java.sql.Time
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors

@Service
class SekolahBumdesServiceImpl(
    val sekolahBumdesRepository: SekolahBumdesRepository,
    val kategoriRepository: KategoriRepository,
    val filterRequestUtil: FilterRequestUtil,
    val validationUtil: ValidationUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<SekolahBumdesEntity>
) : SekolahBumdesService {

    @Autowired
    lateinit var minioClient: MinioClient

    @Value("\${minio.bucket.name}")
    lateinit var bucketName: String

    @Value("\${tes.ip}")
    lateinit var ipTes: String

    override fun create(principal: Principal, sekolahBumdesRequest: SekolahBumdesRequest): SekolahBumdesResponse {
        validationUtil.validate(sekolahBumdesRequest)

        var fileName = ""
        if (sekolahBumdesRequest.file !== null) {
            var extension = sekolahBumdesRequest.file!!.contentType!!.substringAfterLast("/")

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
                        .`object`("/foto-sekolah-bumdes/" + fileName)
                        .stream(sekolahBumdesRequest.file!!.inputStream, sekolahBumdesRequest.file!!.size, -1)
                        .contentType(sekolahBumdesRequest.file!!.contentType)
                        .build()
                )
            } catch (e: Exception) {
                println("error -> $e")
                throw UploadException()
            }
        }
        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)

        val formatter: DateFormat = SimpleDateFormat("HH:mm:ss")
        val timeSekolahBumdes = Time(formatter.parse(sekolahBumdesRequest.waktu!!).time)

        val formatter2: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val tanggalSekolahBumdes = Date(formatter2.parse(sekolahBumdesRequest.tanggal!!).time)

        val sekolahBumdesEntity = SekolahBumdesEntity(
            id = "",
            idKategori = sekolahBumdesRequest.id_kategori,
            kategori = null,
            judul = sekolahBumdesRequest.judul,
            isi = sekolahBumdesRequest.isi,
            tanggal = tanggalSekolahBumdes,
            waktu = timeSekolahBumdes,
            penulis = sekolahBumdesRequest.penulis,
            foto = fileName,
            createdAt = Date(),
            createdBy = user.username,
            updatedAt = null,
            updatedBy = null,
            deletedAt = null,
            deletedBy = null
        )

        val result = sekolahBumdesRepository.save(sekolahBumdesEntity)

        return convertToResponse(result)
    }

    override fun list(
        principal: Principal,
        requestParams: RequestParams,
        filter: Map<String, String>
    ): ListResponse<SekolahBumdesResponse> {
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

        val list = sekolahBumdesRepository.findAll(generateFilter(filter), pageable)

        val items: List<SekolahBumdesEntity> = list.get().collect(Collectors.toList())

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

    override fun get(principal: Principal, id: String): SekolahBumdesResponse {
        val response = findByIdOrThrowNotFound(id)
        return convertToResponse(response)
    }

    override fun update(principal: Principal, id: String, sekolahBumdesRequest: SekolahBumdesRequest): SekolahBumdesResponse {
        val result = findByIdOrThrowNotFound(id)
        validationUtil.validate(result)
        var fileName = result.foto
        if (sekolahBumdesRequest.file !== null) {
            var extension = sekolahBumdesRequest.file!!.contentType!!.substringAfterLast("/")

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
                        .`object`("/foto-sekolah-bumdes/" + result.foto)
                        .build()
                )
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .`object`("/foto-sekolah-bumdes/" + fileName)
                        .stream(sekolahBumdesRequest.file!!.inputStream, sekolahBumdesRequest.file!!.size, -1)
                        .contentType(sekolahBumdesRequest.file!!.contentType)
                        .build()
                )
            } catch (e: Exception) {
                println("error -> $e")
                throw UploadException()
            }
        }
        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)
        var timeSekolahBumdes = result.waktu
        if(sekolahBumdesRequest.waktu != null){
            val formatter: DateFormat = SimpleDateFormat("HH:mm:ss")
            timeSekolahBumdes = Time(formatter.parse(sekolahBumdesRequest.waktu!!).time)
        }
        var tanggalSekolahBumdes = result.tanggal
        if(sekolahBumdesRequest.tanggal != null) {
            val formatter2: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            tanggalSekolahBumdes = Date(formatter2.parse(sekolahBumdesRequest.tanggal!!).time)
        }
        result.apply {
            idKategori = sekolahBumdesRequest.id_kategori
            judul = sekolahBumdesRequest.judul
            isi = sekolahBumdesRequest.isi
            tanggal = tanggalSekolahBumdes
            waktu = timeSekolahBumdes
            penulis = sekolahBumdesRequest.penulis
            foto = fileName
            updatedBy = user.username
            updatedAt = Date()
        }
        sekolahBumdesRepository.save(result)
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
                sekolahBumdesRepository.save(price)
            }else{
                sekolahBumdesRepository.delete(price)
            }
        } catch (e: Exception) {
            println("error -> $e")
            throw DeleteDataException()
        }
        return "Delete Successfully"
    }

    override fun deleteList(principal: Principal, ids: List<String>): String {
        sekolahBumdesRepository.deleteAllById(ids)
        return "Delete Successfully"
    }

    override fun getObject(filename: String): InputStream? {
        val stream: InputStream = try {
            minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`("/foto-sekolah-bumdes/"+filename)
                    .build()
            )
        } catch (e: Exception) {
            println("error getObject -> $e")
            return null
        }
        return stream
    }

    override fun updateFile(
        principal: Principal,
        id: String,
        updateFileRequest: UpdateFileRequest
    ): SekolahBumdesResponse {
        val sekolahBumdes = findByIdOrThrowNotFound(id)
        validationUtil.validate(sekolahBumdes)
        var fileName = sekolahBumdes.foto
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
                        .`object`("/foto-sekolah-bumdes/" + sekolahBumdes.foto)
                        .build()
                )
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .`object`("/foto-sekolah-bumdes/" + fileName)
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
        sekolahBumdes.apply {
            foto = fileName
            updatedBy = user.username
            updatedAt = Date()
        }
        sekolahBumdesRepository.save(sekolahBumdes)
        return convertToResponse(sekolahBumdes)
    }

    private fun convertToResponse(sekolahBumdesEntity: SekolahBumdesEntity): SekolahBumdesResponse {

        if(sekolahBumdesEntity.kategori == null){
            sekolahBumdesEntity.kategori = kategoriRepository.findByIdOrNull(sekolahBumdesEntity.idKategori)
        }

        return SekolahBumdesResponse(
            id = sekolahBumdesEntity.id,
            id_kategori = sekolahBumdesEntity.idKategori,
            nama_kategori = sekolahBumdesEntity.kategori!!.namaKategori,
            judul = sekolahBumdesEntity.judul,
            isi = sekolahBumdesEntity.isi,
            tanggal = sekolahBumdesEntity.tanggal.formateDate(),
            waktu = sekolahBumdesEntity.waktu,
            penulis = sekolahBumdesEntity.penulis,
            foto = ipTes + "/api/v1/sekolah-bumdes/attachment/" + sekolahBumdesEntity.foto,
            created_at = sekolahBumdesEntity.createdAt.formateDateTime(),
            created_by = sekolahBumdesEntity.createdBy,
            updated_at = if (sekolahBumdesEntity.updatedAt == null) null else sekolahBumdesEntity.updatedAt!!.formateDateTime(),
            updated_by = sekolahBumdesEntity.updatedBy,
            deleted_at = if (sekolahBumdesEntity.deletedAt == null) null else sekolahBumdesEntity.deletedAt!!.formateDateTime(),
            deleted_by = sekolahBumdesEntity.deletedBy,
        )
    }

    private fun generateFilter(filter: Map<String, String>): Specification<SekolahBumdesEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }

    private fun findByIdOrThrowNotFound(id: String): SekolahBumdesEntity{
        val result = sekolahBumdesRepository.findByIdOrNull(id)
        if (result == null){
            throw NotFoundException()
        }else{
            return result
        }
    }
}