package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.PengurusRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.PengurusResponse
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.PengurusService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/pengurus")
class PengurusController(val pengurusService: PengurusService) {
    @PostMapping(
        produces = ["application/json"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun createPengurus(
        principal: Principal,
        @ModelAttribute body: PengurusRequest) : WebResponse<PengurusResponse> {
        val response = pengurusService.create(principal, body)

        return WebResponse(
            code = 200,
            status = "OK",
            data = response
        )
    }
    @GetMapping(
        produces = ["application/json"]
    )
    fun listPengurus(
        principal: Principal,
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>
    ): WebResponse<ListResponse<PengurusResponse>?> {
        val responses = pengurusService.list(principal, request, filter)
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
    fun getPengurus(
        principal: Principal,
        @PathVariable("id") id : String) : WebResponse<PengurusResponse>{
        val response = pengurusService.get(principal, id)
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
    fun updatePengurus(
        principal: Principal,
        @PathVariable("id") id: String,
                   pengurusRequest: PengurusRequest
    ): WebResponse<PengurusResponse>{

        val newsResponse = pengurusService.update(principal, id, pengurusRequest)
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
    fun deletePengurus(
        principal: Principal,
        @PathVariable("id") id: String,
                       deletePengurusRequest: DeleteRequest
    ): WebResponse<String>{

        val newsResponse = pengurusService.delete(principal, id, deletePengurusRequest)
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
        pengurusService.deleteList(principal, ids)
        return WebResponse(
            code = 200,
            status = "OK",
            data = ids
        )
    }
}