package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.KategoriRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.KategoriResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import java.security.Principal

interface KategoriService {
    fun create(principal: Principal, kategoriRequest: KategoriRequest) : KategoriResponse

    fun list(principal: Principal,requestParams: RequestParams, filter: Map<String,String>): ListResponse<KategoriResponse>

    fun get(principal: Principal, id: String): KategoriResponse

    fun update(principal: Principal, id: String, kategoriRequest: KategoriRequest) : KategoriResponse

    fun delete(principal: Principal, id: String, deleteRequest: DeleteRequest) : String

    fun deleteList(principal: Principal, ids : List<String>) : String
}