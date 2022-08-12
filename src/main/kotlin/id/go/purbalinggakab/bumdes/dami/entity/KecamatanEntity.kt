package id.go.purbalinggakab.bumdes.dami.entity

import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "datakecamatan")
data class KecamatanEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "kode_kab")
    var kodeKab: String,

    @Column(name = "kode_kec")
    var kodeKec: String,

    @Column(name = "kode_skpd")
    var kodeSkpd: String,

    @Column(name = "nama_kecamatan")
    var namaKecamatan: String,

    @Column(name = "created_at")
    @CreationTimestamp
    var createdAt : Date,

)
