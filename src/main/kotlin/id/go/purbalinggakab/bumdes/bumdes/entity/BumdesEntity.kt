package id.go.purbalinggakab.bumdes.bumdes.entity

import id.go.purbalinggakab.bumdes.dami.entity.DesaEntity
import id.go.purbalinggakab.bumdes.dami.entity.KecamatanEntity
import org.hibernate.annotations.*
import java.util.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "bumdes")
@Where(clause = "is_deleted=false")
data class BumdesEntity(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    var id: String,

    @Column(name = "id_desa")
    var idDesa: Long,

    @Column(name = "id_kecamatan")
    var idKecamatan: Long,

    @Column(name = "id_badan_hukum")
    var idBadanHukum: String,

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "id_badan_hukum", insertable = false, updatable = false)
    var badanHukum: BadanHukumEntity?,

    @Column(name = "id_unit_usaha")
    var idUnitUsaha: String,

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "id_unit_usaha", insertable = false, updatable = false)
    var unitUsaha: UnitUsahaEntity?,

    @Column(name = "nama")
    var nama: String,

    @Column(name = "jumlah_kontribusi_pad")
    var jumlahKontribusiPad: Double,

    @Column(name = "klasifikasi")
    var klasifikasi: String,

    @Column(name = "tipe")
    var tipe: String,

    @Column(name = "foto")
    var foto: String,

    @Column(name = "is_deleted")
    var isDeleted : Boolean? = false,

    @Column(name = "created_at")
    @CreationTimestamp
    var createdAt : Date,

    @Column(name = "created_by")
    var createdBy : String,

    @Column(name = "updated_at")
    @UpdateTimestamp
    var updatedAt : Date?,

    @Column(name = "updated_by")
    var updatedBy : String?,

    @Column(name = "deleted_at")
    var deletedAt : Date?,

    @Column(name = "deleted_by")
    var deletedBy : String?
)
