package id.go.purbalinggakab.bumdes.bumdes.entity

import org.hibernate.annotations.*
import java.sql.Time
import java.util.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "berita_bumdes")
@Where(clause = "is_deleted=false")
data class BeritaBumdesEntity(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    var id: String,

    @Column(name = "id_kategori")
    var idKategori: String,

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "id_kategori", insertable = false, updatable = false)
    var kategori: KategoriEntity?,

    @Column(name = "judul")
    var judul: String,

    @Column(name = "isi")
    var isi: String,

    var tanggal: Date,

    var waktu: Time,

    var penulis: String,

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
