package id.go.purbalinggakab.bumdes.model.response


data class BumdesBersamaResponse (
    var id: String,

    var id_bumdes : String,

    var nama_bumdes : String,

    var id_desa : Long,

    var nama_desa : String,

    var id_kecamatan : Long,

    var nama_kecamatan : String,

    var created_at : String,

    val created_by: String,

    val updated_at: String?,

    val updated_by: String?,

    val deleted_at: String?,

    val deleted_by: String?,
)