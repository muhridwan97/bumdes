package id.go.purbalinggakab.bumdes.util

import com.google.common.base.CaseFormat
import id.go.purbalinggakab.bumdes.model.request.ParamRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class PaginationUtil {
    fun sort(paramRequest: ParamRequest): Sort {
        val format = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, paramRequest.sort)

        return if (paramRequest.order === "desc") {
            Sort.by(format).descending()
        } else {
            Sort.by(format).ascending()
        }
    }
}