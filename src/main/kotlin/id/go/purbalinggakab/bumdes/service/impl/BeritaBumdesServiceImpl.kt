package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.BeritaBumdesEntity
import id.go.purbalinggakab.bumdes.bumdes.repository.KategoriRepository
import id.go.purbalinggakab.bumdes.bumdes.repository.BeritaBumdesRepository
import id.go.purbalinggakab.bumdes.error.DeleteDataException
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.error.UploadException
import id.go.purbalinggakab.bumdes.extensions.formateDate
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.BeritaBumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateFileRequest
import id.go.purbalinggakab.bumdes.model.response.BeritaBumdesResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.service.BeritaBumdesService
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
class BeritaBumdesServiceImpl(
    val beritaBumdesRepository: BeritaBumdesRepository,
    val kategoriRepository: KategoriRepository,
    val filterRequestUtil: FilterRequestUtil,
    val validationUtil: ValidationUtil,
    private val keycloakAuthService: KeycloakAuthService,
    private val specification: FilterSpecification<BeritaBumdesEntity>
) : BeritaBumdesService {

    @Autowired
    lateinit var minioClient: MinioClient

    @Value("\${minio.bucket.name}")
    lateinit var bucketName: String

    @Value("\${tes.ip}")
    lateinit var ipTes: String

    override fun create(principal: Principal, beritaBumdesRequest: BeritaBumdesRequest): BeritaBumdesResponse {
        validationUtil.validate(beritaBumdesRequest)

        var fileName = ""
        if (beritaBumdesRequest.file !== null) {
            var extension = beritaBumdesRequest.file!!.contentType!!.substringAfterLast("/")

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
                        .`object`("/foto-berita-bumdes/" + fileName)
                        .stream(beritaBumdesRequest.file!!.inputStream, beritaBumdesRequest.file!!.size, -1)
                        .contentType(beritaBumdesRequest.file!!.contentType)
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
        val timeBeritaBumdes = Time(formatter.parse(beritaBumdesRequest.waktu!!).time)

        val formatter2: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val tanggalBeritaBumdes = Date(formatter2.parse(beritaBumdesRequest.tanggal!!).time)

        val beritaBumdesEntity = BeritaBumdesEntity(
            id = "",
            idKategori = beritaBumdesRequest.id_kategori,
            kategori = null,
            judul = beritaBumdesRequest.judul,
            isi = beritaBumdesRequest.isi,
            tanggal = tanggalBeritaBumdes,
            waktu = timeBeritaBumdes,
            penulis = beritaBumdesRequest.penulis,
            foto = fileName,
            createdAt = Date(),
            createdBy = user.username,
            updatedAt = null,
            updatedBy = null,
            deletedAt = null,
            deletedBy = null
        )

        val result = beritaBumdesRepository.save(beritaBumdesEntity)

        return convertToResponse(result)
    }

    override fun list(
        principal: Principal,
        requestParams: RequestParams,
        filter: Map<String, String>
    ): ListResponse<BeritaBumdesResponse> {
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

        val list = beritaBumdesRepository.findAll(generateFilter(filter), pageable)

        val items: List<BeritaBumdesEntity> = list.get().collect(Collectors.toList())

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

    override fun get(principal: Principal, id: String): BeritaBumdesResponse {
        val response = findByIdOrThrowNotFound(id)
        return convertToResponse(response)
    }

    override fun update(principal: Principal, id: String, beritaBumdesRequest: BeritaBumdesRequest): BeritaBumdesResponse {
        val result = findByIdOrThrowNotFound(id)
        validationUtil.validate(result)
        var fileName = result.foto
        if (beritaBumdesRequest.file !== null) {
            var extension = beritaBumdesRequest.file!!.contentType!!.substringAfterLast("/")

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
                        .`object`("/foto-berita-bumdes/" + result.foto)
                        .build()
                )
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .`object`("/foto-berita-bumdes/" + fileName)
                        .stream(beritaBumdesRequest.file!!.inputStream, beritaBumdesRequest.file!!.size, -1)
                        .contentType(beritaBumdesRequest.file!!.contentType)
                        .build()
                )
            } catch (e: Exception) {
                println("error -> $e")
                throw UploadException()
            }
        }
        //bypass name with access token
        val user = keycloakAuthService.getUserInfo(principal)
        var timeBeritaBumdes = result.waktu
        if(beritaBumdesRequest.waktu != null){
            val formatter: DateFormat = SimpleDateFormat("HH:mm:ss")
            timeBeritaBumdes = Time(formatter.parse(beritaBumdesRequest.waktu!!).time)
        }
        var tanggalBeritaBumdes = result.tanggal
        if(beritaBumdesRequest.tanggal != null) {
            val formatter2: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            tanggalBeritaBumdes = Date(formatter2.parse(beritaBumdesRequest.tanggal!!).time)
        }
        result.apply {
            idKategori = beritaBumdesRequest.id_kategori
            judul = beritaBumdesRequest.judul
            isi = beritaBumdesRequest.isi
            tanggal = tanggalBeritaBumdes
            waktu = timeBeritaBumdes
            penulis = beritaBumdesRequest.penulis
            foto = fileName
            updatedBy = user.username
            updatedAt = Date()
        }
        beritaBumdesRepository.save(result)
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
                beritaBumdesRepository.save(price)
            }else{
                beritaBumdesRepository.delete(price)
            }
        } catch (e: Exception) {
            println("error -> $e")
            throw DeleteDataException()
        }
        return "Delete Successfully"
    }

    override fun deleteList(principal: Principal, ids: List<String>): String {
        beritaBumdesRepository.deleteAllById(ids)
        return "Delete Successfully"
    }

    override fun getObject(filename: String): InputStream? {
        val stream: InputStream = try {
            minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`("/foto-berita-bumdes/"+filename)
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
    ): BeritaBumdesResponse {
        val beritaBumdes = findByIdOrThrowNotFound(id)
        validationUtil.validate(beritaBumdes)
        var fileName = beritaBumdes.foto
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
                        .`object`("/foto-berita-bumdes/" + beritaBumdes.foto)
                        .build()
                )
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .`object`("/foto-berita-bumdes/" + fileName)
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
        beritaBumdes.apply {
            foto = fileName
            updatedBy = user.username
            updatedAt = Date()
        }
        beritaBumdesRepository.save(beritaBumdes)
        return convertToResponse(beritaBumdes)
    }

    private fun convertToResponse(beritaBumdesEntity: BeritaBumdesEntity): BeritaBumdesResponse {

        if(beritaBumdesEntity.kategori == null){
            beritaBumdesEntity.kategori = kategoriRepository.findByIdOrNull(beritaBumdesEntity.idKategori)
        }

        return BeritaBumdesResponse(
            id = beritaBumdesEntity.id,
            id_kategori = beritaBumdesEntity.idKategori,
            nama_kategori = beritaBumdesEntity.kategori!!.namaKategori,
            judul = beritaBumdesEntity.judul,
            isi = beritaBumdesEntity.isi,
            tanggal = beritaBumdesEntity.tanggal.formateDate(),
            waktu = beritaBumdesEntity.waktu,
            penulis = beritaBumdesEntity.penulis,
            foto = ipTes + "/api/v1/berita-bumdes/attachment/" + beritaBumdesEntity.foto,
            created_at = beritaBumdesEntity.createdAt.formateDateTime(),
            created_by = beritaBumdesEntity.createdBy,
            updated_at = if (beritaBumdesEntity.updatedAt == null) null else beritaBumdesEntity.updatedAt!!.formateDateTime(),
            updated_by = beritaBumdesEntity.updatedBy,
            deleted_at = if (beritaBumdesEntity.deletedAt == null) null else beritaBumdesEntity.deletedAt!!.formateDateTime(),
            deleted_by = beritaBumdesEntity.deletedBy,
        )
    }

    private fun generateFilter(filter: Map<String, String>): Specification<BeritaBumdesEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }

    private fun findByIdOrThrowNotFound(id: String): BeritaBumdesEntity{
        val result = beritaBumdesRepository.findByIdOrNull(id)
        if (result == null){
            throw NotFoundException()
        }else{
            return result
        }
    }
}