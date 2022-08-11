package id.go.purbalinggakab.bumdes.specification


import com.google.common.base.CaseFormat
import id.go.purbalinggakab.bumdes.error.FilterOperatorExecption
import id.go.purbalinggakab.bumdes.model.response.pageable.SortingResponse
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.util.stream.Collectors

@Component
class FilterRequestUtil {

    fun toSortResponse(sort: String): SortingResponse {
        val split = sort.split(":")
        return SortingResponse(
            key = split[0],
            direction = split[1]
        )
    }

    fun toSplitDirection(sort: String): Sort.Direction {
        val split = sort.split(":")
        val direction = when (split[1].lowercase()) {
            "desc" -> Sort.Direction.DESC
            else -> Sort.Direction.ASC
        }
        return direction
    }

    fun toSplitSortKey(sort: String): String {
        val split = sort.split(":")
        return split[0]
    }

    fun toSortBy(sort: String, convert: Boolean = true): Sort {
        val split = sort.split(":")
        val direction = when (split[1].lowercase()) {
            "desc" -> Sort.Direction.DESC
            else -> Sort.Direction.ASC
        }
        return convertToSort(Sort.by(direction, split[0]), convert)
    }

    private fun convertToSort(sort: Sort, convert: Boolean = true): Sort {
        if (convert) {
            return Sort.by(sort.get()
                .map { sortOrder: Sort.Order ->
                    sortOrder.withProperty(
                        CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, sortOrder.property)
                    )
                }
                .collect(Collectors.toList())
            )
        } else {
            return Sort.by(sort.get()
                .map { sortOrder: Sort.Order ->
                    sortOrder.withProperty(
                        CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, sortOrder.property)
                    )
                }.collect(Collectors.toList())
            )
        }
    }

    fun convertToKeySpecification(key: String?): String {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key!!)
    }

    fun toFilterCriteria(
        filter: Map<String, String>,
        options: MutableList<FilterMapper>
    ): MutableList<FilterCriteria> {
        val filters = mutableListOf<FilterCriteria>()
        filter.forEach {
            if (it.key != "page" && it.key != "size" && it.key != "sortBy") {
                val mapper = options.find { option -> option.key.equals(it.key) }
                filters.add(
                    convertToFilterCriteria(it, mapper)
                )
            }
        }
        return filters
    }

    private fun convertToFilterCriteria(
        filter: Map.Entry<String, String>,
        mapper: FilterMapper?
    ): FilterCriteria {
        var key = filter.key
        if (mapper !== null) {
            key = mapper.substitute.toString()
        }
        val operatorValue = filter.value.split(":")
        if (operatorValue.size == 1) {
            throw FilterOperatorExecption()
        } else {
            return FilterCriteria(
                key = convertToKeySpecification(key),
                operation = operatorValue[0],
                value = operatorValue[1]
            )
        }
    }

}