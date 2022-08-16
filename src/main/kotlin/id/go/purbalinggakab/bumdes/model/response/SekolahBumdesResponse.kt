package id.go.purbalinggakab.bumdes.model.response

import java.sql.Time


data class SekolahBumdesResponse (
    var id: String,

    var id_kategori : String,

    var nama_kategori : String,

    var judul : String,

    var isi : String,

    var tanggal : String,

    var waktu : Time,

    var penulis : String,

    var foto : String,

    var created_at : String,

    val created_by: String,

    val updated_at: String?,

    val updated_by: String?,

    val deleted_at: String?,

    val deleted_by: String?,
)