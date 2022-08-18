package id.go.purbalinggakab.bumdes.model.response

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class UpdateUserRequest(

    @field:NotNull
    var googleId: String,

    var token: String?,

    var photo: String?,

    @field:NotBlank
    var email : String,

    var displayName : String?,

    var givenName : String?,

    var familyName : String?,

    var updatedBy: String?= "")
