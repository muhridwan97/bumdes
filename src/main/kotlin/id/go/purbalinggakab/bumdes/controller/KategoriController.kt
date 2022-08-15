package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.KategoriRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.KategoriResponse
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.KategoriService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/kategori")
class KategoriController(val kategoriService: KategoriService) {
    @PostMapping(
        produces = ["application/json"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun createKategori(
        principal: Principal,
        @ModelAttribute body: KategoriRequest) : WebResponse<KategoriResponse> {
        val response = kategoriService.create(principal, body)

        return WebResponse(
            code = 200,
            status = "OK",
            data = response
        )
    }
    @GetMapping(
        produces = ["application/json"]
    )
    fun listKategori(
        principal: Principal,
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>
    ): WebResponse<ListResponse<KategoriResponse>?> {
        val responses = kategoriService.list(principal, request, filter)
        var status = "OK"
        if(responses.items.isEmpty()){
            status = "No Data Available"
        }
        return WebResponse(
            code = 200,
            status = status,
            data = responses
        )
    }

    @GetMapping(
        value = ["/{id}"],
        produces = ["application/json"]
    )
    fun getKategori(
        principal: Principal,
        @PathVariable("id") id : String) : WebResponse<KategoriResponse>{
        val response = kategoriService.get(principal, id)
        return WebResponse(
            200,
            "oke",
            response
        )
    }

    @PatchMapping(
        value = ["/{id}"],
        produces = ["application/json"],
        consumes = ["application/json", MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun updateKategori(
        principal: Principal,
        @PathVariable("id") id: String,
                   kategoriRequest: KategoriRequest
    ): WebResponse<KategoriResponse>{

        val newsResponse = kategoriService.update(principal, id, kategoriRequest)
        return WebResponse(
            200,
            "OK",
            newsResponse
        )
    }

    @DeleteMapping(
        value = ["/{id}"],
        produces = ["application/json"]
    )
    fun deleteKategori(
        principal: Principal,
        @PathVariable("id") id: String,
                       deleteKategoriRequest: DeleteRequest
    ): WebResponse<String>{

        val newsResponse = kategoriService.delete(principal, id, deleteKategoriRequest)
        return WebResponse(
            200,
            "OK",
            newsResponse
        )
    }


    @DeleteMapping(
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun deleteListHard(
        principal: Principal,
        @RequestBody ids: List<String>): WebResponse<List<String>> {
        kategoriService.deleteList(principal, ids)
        return WebResponse(
            code = 200,
            status = "OK",
            data = ids
        )
    }
}