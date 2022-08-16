package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.BeritaBumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateFileRequest
import id.go.purbalinggakab.bumdes.model.response.BeritaBumdesResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import java.io.InputStream
import java.security.Principal

interface BeritaBumdesService {
    fun create(principal: Principal, beritaBumdesRequest: BeritaBumdesRequest) : BeritaBumdesResponse

    fun list(principal: Principal,requestParams: RequestParams, filter: Map<String,String>): ListResponse<BeritaBumdesResponse>

    fun get(principal: Principal, id: String): BeritaBumdesResponse

    fun update(principal: Principal, id: String, beritaBumdesRequest: BeritaBumdesRequest) : BeritaBumdesResponse

    fun delete(principal: Principal, id: String, deleteRequest: DeleteRequest) : String

    fun deleteList(principal: Principal, ids : List<String>) : String

    fun getObject(filename: String): InputStream?

    fun updateFile(principal: Principal, id: String, updateFileRequest: UpdateFileRequest) : BeritaBumdesResponse
}