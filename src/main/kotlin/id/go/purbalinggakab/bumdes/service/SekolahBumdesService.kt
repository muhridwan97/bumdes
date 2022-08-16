package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.SekolahBumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateFileRequest
import id.go.purbalinggakab.bumdes.model.response.BumdesResponse
import id.go.purbalinggakab.bumdes.model.response.SekolahBumdesResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import java.io.InputStream
import java.security.Principal

interface SekolahBumdesService {
    fun create(principal: Principal, sekolahBumdesRequest: SekolahBumdesRequest) : SekolahBumdesResponse

    fun list(principal: Principal,requestParams: RequestParams, filter: Map<String,String>): ListResponse<SekolahBumdesResponse>

    fun get(principal: Principal, id: String): SekolahBumdesResponse

    fun update(principal: Principal, id: String, sekolahBumdesRequest: SekolahBumdesRequest) : SekolahBumdesResponse

    fun delete(principal: Principal, id: String, deleteRequest: DeleteRequest) : String

    fun deleteList(principal: Principal, ids : List<String>) : String

    fun getObject(filename: String): InputStream?

    fun updateFile(principal: Principal, id: String, updateFileRequest: UpdateFileRequest) : SekolahBumdesResponse
}