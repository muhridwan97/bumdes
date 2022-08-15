package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.BumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateFileRequest
import id.go.purbalinggakab.bumdes.model.response.BumdesResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import java.io.InputStream
import java.security.Principal

interface BumdesService {
    fun create(principal: Principal, bumdesRequest: BumdesRequest) : BumdesResponse

    fun list(principal: Principal,requestParams: RequestParams, filter: Map<String,String>): ListResponse<BumdesResponse>

    fun getObject(filename: String): InputStream?

    fun get(principal: Principal, id: String): BumdesResponse

    fun update(principal: Principal, id: String, bumdesRequest: BumdesRequest) : BumdesResponse

    fun delete(principal: Principal, id: String, deleteRequest: DeleteRequest) : String

    fun deleteList(principal: Principal, ids : List<String>) : String

    fun updateFile(principal: Principal, id: String, updateFileRequest: UpdateFileRequest) : BumdesResponse
}