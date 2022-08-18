package id.go.purbalinggakab.bumdes.model.response

data class UserResponse(
    val id : String,

    var googleId: String?,

    var token: String?,

    var photo: String?,

    var email : String?,

    var displayName : String?,

    var givenName : String?,

    var familyName : String?,

    var isDeleted : Boolean?,

    var createdAt : String,

    var createdBy : String,

    var updatedAt : String?,

    var updatedBy : String?,

    var deletedAt : String?,

    var deletedBy : String?
)
