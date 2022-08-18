package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.CreateUserRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.UpdateUserRequest
import id.go.purbalinggakab.bumdes.model.response.UserResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse

interface UserService {
    fun create(createUserRequest: CreateUserRequest) : UserResponse

    fun list(requestParams: RequestParams, filter: Map<String,String>): ListResponse<UserResponse>

    fun get(id: String): UserResponse

    fun getByEmail(email: String): UserResponse

    fun update(id: String, updateUserRequest: UpdateUserRequest) : UserResponse

    fun delete(id: String, deleteUserRequest: DeleteRequest) : String

    fun deleteList(ids : List<String>) : String
}