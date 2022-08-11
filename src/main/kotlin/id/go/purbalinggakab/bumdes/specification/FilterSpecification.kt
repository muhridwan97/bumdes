package id.go.purbalinggakab.bumdes.specification

import id.go.purbalinggakab.bumdes.specification.FilterCriteria
import id.go.purbalinggakab.bumdes.error.FilterKeyException
import id.go.purbalinggakab.bumdes.error.FilterOperatorExecption
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import javax.persistence.criteria.Join
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

@Component
class FilterSpecification<T> {

    fun buildPredicate(requestParams: MutableList<FilterCriteria>, operator: String? = "and"): Specification<T>? {

        return Specification { root, query, criteriaBuilder ->
            val predicates: MutableList<Predicate> = mutableListOf()

            if (requestParams.isEmpty()) {
                null
            } else {
                requestParams.map {

                    // generate key
                    val key: Path<String>?
                    val keys = it.key.split(".")
                    if (keys.size == 1) {
                        try {
                            key = root.get(keys[0])
                        } catch (e: IllegalArgumentException) {
                            print("error -> ${e.message}")
                            throw FilterKeyException()
                        }
                    } else {
                        var join: Join<Any, Any> = root.join(keys[0], JoinType.INNER)
                        val lastIndex = keys.size - 1
                        for (i in keys.indices) {
                            if (i in 1 until lastIndex) {
                                join = join.join(keys[i], JoinType.INNER)
                            }
                        }
                        try {
                            key = join.get(keys[lastIndex])
                        } catch (e: IllegalArgumentException) {
                            print("error -> ${e.message}")
                            throw FilterKeyException()
                        }
                    }

                    // select operator
                    when (it.operation) {
                        "countLike" -> predicates.add(
                            criteriaBuilder.like(
                                criteriaBuilder.lower(key.`as`(String::class.java)),
                                "%" + it.value.toString().lowercase() + "%"
                            )
                        )
                        "likeStart" -> predicates.add(
                            criteriaBuilder.like(
                                criteriaBuilder.lower(key.`as`(String::class.java)),
                                it.value.toString().lowercase() + "%"
                            )
                        )
                        "likeEnd" -> predicates.add(
                            criteriaBuilder.like(
                                criteriaBuilder.lower(key.`as`(String::class.java)),
                                "%" + it.value.toString().lowercase()
                            )
                        )
                        "equal" -> predicates.add(
                            criteriaBuilder.equal(
                                criteriaBuilder.lower(key.`as`(String::class.java)), it.value.toString().lowercase()
                            )
                        )
                        "greatherThanOrEqual" -> predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.lower(key.`as`(String::class.java)), it.value.toString())
                        )
                        "lessThanOrEqual" -> predicates.add(
                            criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.lower(key.`as`(String::class.java)), it.value.toString())
                        )
                        "between" -> {
                            val between = it.value.toString().split(",")
                            predicates.add(
                                criteriaBuilder.between(criteriaBuilder.lower(key.`as`(String::class.java)), between[0], between[1])
                            )
                        }
                        "in" -> {
                            val between = it.value.toString().split(",")
                            val predicatesIn: MutableList<Predicate> = mutableListOf()
                            between.forEach {
                                predicatesIn.add(
                                    criteriaBuilder.equal(
                                        criteriaBuilder.lower(key.`as`(String::class.java)), it.lowercase()
                                    )
                                )
                            }
                            predicates.add(
                                criteriaBuilder.or(*predicatesIn.toTypedArray())
                            )
                        }
                        else -> throw FilterOperatorExecption()
                    }
                }

                if (operator === "or") {
                    criteriaBuilder.or(*predicates.toTypedArray())
                } else {
                    criteriaBuilder.and(*predicates.toTypedArray())
                }
            }
        }

    }

}
