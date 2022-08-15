package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.error.*
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import javax.validation.ConstraintViolationException


@RestControllerAdvice
class ErrorController {
    @ExceptionHandler(value = [ConstraintViolationException::class])
    fun validationHandler(constraintViolationException: ConstraintViolationException): WebResponse<String> {
        return WebResponse(
            code = 400,
            status = "BAD REQUEST",
            data = constraintViolationException.message!!
        )
    }

    @ExceptionHandler(value = [NotFoundException::class])
    fun notFound(notFoundException: NotFoundException):WebResponse<String>{
        return WebResponse(
            code = 404,
            status = "NOT FOUND",
            data = "Not Found"
        )
    }

    @ExceptionHandler(value = [UnauthorizedException::class])
    fun unauthorized(unauthorizedException: UnauthorizedException):WebResponse<String>{
        return WebResponse(
            code = 401,
            status = "UNAUTHORIZED",
            data = "Please put your X-Api-Key"
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handledBadRequest(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(value = [DeleteDataException::class])
    fun deleteData(deleteDataException: DeleteDataException):WebResponse<String>{
        return WebResponse(
            code = 400,
            status = "BAD REQUEST",
            data = "Delete failed"
        )
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = [FilterKeyException::class])
    fun keyFilterError(filterKeyException: FilterKeyException): WebResponse<String> {
        return WebResponse(
            code = 400,
            status = "BAD REQUEST",
            data = "Must use valid key for filter."
        )
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = [FilterOperatorExecption::class])
    fun operatorFilterError(filterOperatorExecption: FilterOperatorExecption): WebResponse<String> {
        return WebResponse(
            code = 400,
            status = "BAD REQUEST",
            data = "Must use valid operator for filter."
        )
    }

    @ExceptionHandler(value = [NullException::class])
    fun nullException(nullException: NullException):WebResponse<String>{
        return WebResponse(
            code = 400,
            status = "BAD REQUEST",
            data = nullException.message+" Null Data"
        )
    }

//    @ExceptionHandler(value = [DuplicateException::class])
//    fun duplicateException(duplicateException: DuplicateException):WebResponse<String>{
//        println("ex: ${duplicateException.message}")
//        return WebResponse(
//            code = 400,
//            status = "BAD REQUEST",
//            data = "Slug value have been exist "+ duplicateException.message
//        )
//    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleFileSizeLimitExceeded(exc: MaxUploadSizeExceededException?): WebResponse<String> {
        return WebResponse(
            code = 400,
            status = "BAD REQUEST",
            data = "file must less than 1 MB"
        )
    }

    @ExceptionHandler(NoBadanHukumException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFound(noBadanHukumException: NoBadanHukumException): WebResponse<String> {
        return WebResponse(
            code = 400,
            status = "BAD REQUEST",
            data = "${noBadanHukumException.message} has been exist"
        )
    }

    @ExceptionHandler(NamaUnitUsahaException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFound(namaUnitUsahaException: NamaUnitUsahaException): WebResponse<String> {
        return WebResponse(
            code = 400,
            status = "BAD REQUEST",
            data = "${namaUnitUsahaException.message} has been exist"
        )
    }

}