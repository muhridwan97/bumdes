package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.DesaRequest
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.DesaResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.DesaService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/desa")
class DesaController(val desaService: DesaService) {
    @GetMapping
    fun list(
        principal: Principal,
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>): WebResponse<ListResponse<DesaResponse>> {
        val result = desaService.list(principal, request, filter)

        return WebResponse(
            code = 200,
            status = "OK",
            data = result
        )
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): WebResponse<DesaResponse> {
        val result = desaService.get(id)

        return WebResponse(
            code = 200,
            status = "OK",
            data = result
        )
    }

    @PostMapping(
        produces = ["application/json"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun create(
        principal: Principal,
        @ModelAttribute desaRequest: DesaRequest): WebResponse<DesaResponse> {
        val result = desaService.create(principal, desaRequest)

        return WebResponse(
            code = 200,
            status = "OK",
            data = result
        )
    }

    @PatchMapping("/{id}",
        produces = ["application/json"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun update(
        @PathVariable id: String,
        @ModelAttribute desaRequest: DesaRequest
    ): WebResponse<DesaResponse> {
        val result = desaService.update(id, desaRequest)

        return WebResponse(
            code = 200,
            status = "OK",
            data = result
        )
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): WebResponse<String> {
        desaService.delete(id)

        return WebResponse(
            code = 200,
            status = "OK",
            data = "${id} deleted"
        )
    }
    @DeleteMapping(
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun deleteListHard(@RequestBody ids: List<String>): WebResponse<List<String>> {
        desaService.deleteList(ids)
        return WebResponse(
            code = 200,
            status = "OK",
            data = ids
        )
    }
}