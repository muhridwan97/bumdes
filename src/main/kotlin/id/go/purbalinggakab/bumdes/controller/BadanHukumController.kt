package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.BadanHukumRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.BadanHukumResponse
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.BadanHukumService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/badan-hukum")
class BadanHukumController(val badanHukumService: BadanHukumService) {
    @PostMapping(
        produces = ["application/json"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun createBadanHukum(
        principal: Principal,
        @ModelAttribute body: BadanHukumRequest) : WebResponse<BadanHukumResponse> {
        val response = badanHukumService.create(principal, body)

        return WebResponse(
            code = 200,
            status = "OK",
            data = response
        )
    }
    @GetMapping(
        produces = ["application/json"]
    )
    fun listBadanHukum(
        principal: Principal,
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>
    ): WebResponse<ListResponse<BadanHukumResponse>?> {
        val responses = badanHukumService.list(principal, request, filter)
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
    fun getBadanHukum(
        principal: Principal,
        @PathVariable("id") id : String) : WebResponse<BadanHukumResponse>{
        val response = badanHukumService.get(principal, id)
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
    fun updateBadanHukum(
        principal: Principal,
        @PathVariable("id") id: String,
                   badanHukumRequest: BadanHukumRequest
    ): WebResponse<BadanHukumResponse>{

        val newsResponse = badanHukumService.update(principal, id, badanHukumRequest)
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
    fun deleteBadanHukum(
        principal: Principal,
        @PathVariable("id") id: String,
                       deleteBadanHukumRequest: DeleteRequest
    ): WebResponse<String>{

        val newsResponse = badanHukumService.delete(principal, id, deleteBadanHukumRequest)
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
        badanHukumService.deleteList(principal, ids)
        return WebResponse(
            code = 200,
            status = "OK",
            data = ids
        )
    }
}