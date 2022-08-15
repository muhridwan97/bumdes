package id.go.purbalinggakab.bumdes.model.response


data class BumdesResponse (
    var id: String,

    var id_desa : Long,

    var nama_desa : String,

    var id_kecamatan : Long,

    var nama_kecamatan: String,

    var id_badan_hukum : String,

    var no_badan_hukum : String,

    var id_unit_usaha: String,

    var nama_unit_usaha: String,

    var nama: String,

    var jumlah_kontribusi_pad: Double,

    var klasifikasi: String,

    var tipe: String,

    var foto: String,

    var created_at : String,

    val created_by: String,

    val updated_at: String?,

    val updated_by: String?,

    val deleted_at: String?,

    val deleted_by: String?,
)