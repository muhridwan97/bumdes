package id.go.purbalinggakab.bumdes.model.response


data class KategoriResponse (
    var id: String,

    var nama_kategori : String,

    var created_at : String,

    val created_by: String,

    val updated_at: String?,

    val updated_by: String?,

    val deleted_at: String?,

    val deleted_by: String?,
)