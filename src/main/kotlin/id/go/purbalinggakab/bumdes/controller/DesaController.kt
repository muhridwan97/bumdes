package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.DesaResponse
import id.go.purbalinggakab.bumdes.model.response.ItemResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.DesaService
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
    fun get(@PathVariable id: Long): WebResponse<DesaResponse> {
        val result = desaService.get(id)

        return WebResponse(
            code = 200,
            status = "OK",
            data = result
        )
    }

    @GetMapping(
        value = ["/listAll"],
        produces = ["application/json"]
    )
    fun getItemListAll(): WebResponse<List<ItemResponse<String>>> {
        val responses = desaService.listAll()
        return WebResponse(
            code = 200,
            status = "OK",
            data = responses
        )
    }

    @GetMapping(
        value = ["/listByKec/{id}"],
        produces = ["application/json"]
    )
    fun getItemListByKec(@PathVariable id: Long): WebResponse<List<ItemResponse<String>>> {
        val responses = desaService.listByKec(id)
        return WebResponse(
            code = 200,
            status = "OK",
            data = responses
        )
    }
}