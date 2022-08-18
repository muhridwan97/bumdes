package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.CreateTanggapanRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateTanggapanRequest
import id.go.purbalinggakab.bumdes.model.response.TanggapanResponse
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.TanggapanService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/tanggapan")
class TanggapanController(val tanggapanService: TanggapanService) {
    @PostMapping(
        produces = ["application/json"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun createTanggapan(
        @ModelAttribute body: CreateTanggapanRequest) : WebResponse<TanggapanResponse> {
        val response = tanggapanService.create(body)

        return WebResponse(
            code = 200,
            status = "OK",
            data = response
        )
    }
    @GetMapping(
        produces = ["application/json"]
    )
    fun listTanggapan(
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>
    ): WebResponse<ListResponse<TanggapanResponse>?> {
        val responses = tanggapanService.list( request, filter)
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
    fun getTanggapan(
        @PathVariable("id") id : String) : WebResponse<TanggapanResponse>{
        val response = tanggapanService.get( id)
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
    fun updateTanggapan(
        @PathVariable("id") id: String,
        updateTanggapanRequest: UpdateTanggapanRequest
    ): WebResponse<TanggapanResponse>{

        val newsResponse = tanggapanService.update( id, updateTanggapanRequest)
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
    fun deleteTanggapan(
        @PathVariable("id") id: String,
                       deleteTanggapanRequest: DeleteRequest
    ): WebResponse<String>{

        val newsResponse = tanggapanService.delete( id, deleteTanggapanRequest)
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
        @RequestBody ids: List<String>): WebResponse<List<String>> {
        tanggapanService.deleteList( ids)
        return WebResponse(
            code = 200,
            status = "OK",
            data = ids
        )
    }
}