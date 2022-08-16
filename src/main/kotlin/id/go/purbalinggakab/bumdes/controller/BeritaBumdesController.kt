package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.BeritaBumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateFileRequest
import id.go.purbalinggakab.bumdes.model.response.BeritaBumdesResponse
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.BeritaBumdesService
import org.apache.commons.io.IOUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.AntPathMatcher
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.HandlerMapping
import java.io.IOException
import java.security.Principal
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/berita-bumdes")
class BeritaBumdesController(val beritaBumdesService: BeritaBumdesService) {
    @PostMapping(
        produces = ["application/json"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun createBeritaBumdes(
        principal: Principal,
        @ModelAttribute body: BeritaBumdesRequest) : WebResponse<BeritaBumdesResponse> {
        val response = beritaBumdesService.create(principal, body)

        return WebResponse(
            code = 200,
            status = "OK",
            data = response
        )
    }
    @GetMapping(
        produces = ["application/json"]
    )
    fun listBeritaBumdes(
        principal: Principal,
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>
    ): WebResponse<ListResponse<BeritaBumdesResponse>?> {
        val responses = beritaBumdesService.list(principal, request, filter)
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
    fun getBeritaBumdes(
        principal: Principal,
        @PathVariable("id") id : String) : WebResponse<BeritaBumdesResponse>{
        val response = beritaBumdesService.get(principal, id)
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
    fun updateBeritaBumdes(
        principal: Principal,
        @PathVariable("id") id: String,
                   beritaBumdesRequest: BeritaBumdesRequest
    ): WebResponse<BeritaBumdesResponse>{

        val newsResponse = beritaBumdesService.update(principal, id, beritaBumdesRequest)
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
    fun deleteBeritaBumdes(
        principal: Principal,
        @PathVariable("id") id: String,
                       deleteBeritaBumdesRequest: DeleteRequest
    ): WebResponse<String>{

        val newsResponse = beritaBumdesService.delete(principal, id, deleteBeritaBumdesRequest)
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
        beritaBumdesService.deleteList(principal, ids)
        return WebResponse(
            code = 200,
            status = "OK",
            data = ids
        )
    }
    @GetMapping(value = ["/attachment/**"])
    @Throws(IOException::class)
    fun getFile(request: HttpServletRequest): ResponseEntity<Any?>? {
        val pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) as String
        val filename = AntPathMatcher().extractPathWithinPattern(pattern, request.servletPath)
        val header = HttpHeaders()
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$filename")
        header.add("Cache-Control", "no-cache, no-store, must-revalidate")
        header.add("Pragma", "no-cache")
        header.add("Expires", "0")
        return ResponseEntity.ok()
            .headers(header)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(IOUtils.toByteArray(beritaBumdesService.getObject(filename)))
    }

    @PatchMapping(
        value = ["/upload-file/{id}"],
        produces = ["application/json"],
        consumes = ["application/json", MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun updateFileCarousel(
        principal: Principal,
        @PathVariable("id") id: String,
        @ModelAttribute updateFileRequest: UpdateFileRequest
    ): WebResponse<BeritaBumdesResponse>{

        val carouselResponse = beritaBumdesService.updateFile(principal, id, updateFileRequest)
        return WebResponse(
            200,
            "OK",
            carouselResponse
        )
    }
}