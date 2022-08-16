package id.go.purbalinggakab.bumdes.bumdes.entity

import id.go.purbalinggakab.bumdes.dami.entity.DesaEntity
import id.go.purbalinggakab.bumdes.dami.entity.KecamatanEntity
import org.hibernate.annotations.*
import java.util.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "tanggapan")
@Where(clause = "is_deleted=false")
data class TanggapanEntity(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    var id: String,

    @Column(name = "id_bumdes")
    var idBumdes: String,

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "id_bumdes", insertable = false, updatable = false)
    var bumdes: BumdesEntity?,

    @Column(name = "id_pengurus")
    var idPengurus: String,

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "id_pengurus", insertable = false, updatable = false)
    var pengurus: PengurusEntity?,

    @Column(name = "jabatan")
    var jabatan: String,

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
