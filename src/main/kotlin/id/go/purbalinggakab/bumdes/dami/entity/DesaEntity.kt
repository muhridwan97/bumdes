package id.go.purbalinggakab.bumdes.dami.entity

import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "datadesa")
data class DesaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "kode_kab")
    var kodeKab: String,

    @Column(name = "kode_kec")
    var kodeKec: Long,

    @Column(name = "kode_desa")
    var kodeDesa: String,

    @Column(name = "nama_desa")
    var namaDesa: String,

    @Column(name = "created_at")
    @CreationTimestamp
    var createdAt : Date,

)
