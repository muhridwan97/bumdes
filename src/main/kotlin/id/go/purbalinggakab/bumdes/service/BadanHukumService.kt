package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.BadanHukumRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.BadanHukumResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import java.security.Principal

interface BadanHukumService {
    fun create(principal: Principal, badanHukumRequest: BadanHukumRequest) : BadanHukumResponse

    fun list(principal: Principal,requestParams: RequestParams, filter: Map<String,String>): ListResponse<BadanHukumResponse>

    fun get(principal: Principal, id: String): BadanHukumResponse

    fun update(principal: Principal, id: String, badanHukumRequest: BadanHukumRequest) : BadanHukumResponse

    fun delete(principal: Principal, id: String, deleteRequest: DeleteRequest) : String

    fun deleteList(principal: Principal, ids : List<String>) : String
}