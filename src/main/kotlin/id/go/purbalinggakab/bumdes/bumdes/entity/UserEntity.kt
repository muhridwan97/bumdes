package id.go.purbalinggakab.bumdes.bumdes.entity

import org.hibernate.annotations.*
import java.util.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "prv_users")
@Where(clause = "is_deleted=false")
data class UserEntity(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    var id: String,

    @Column(name="id_google")
    var googleId: String,

    var token : String?="",

    var email : String,

    @Column(name="display_name")
    var displayName : String?="",


    @Column(name="given_name")
    var givenName : String?="",

    @Column(name="family_name")
    var familyName : String?="",

    var photo : String?="",

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
