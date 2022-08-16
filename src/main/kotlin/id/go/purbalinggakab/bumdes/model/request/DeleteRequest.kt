package id.go.purbalinggakab.bumdes.model.request

data class DeleteRequest(
    var softDelete : Boolean? = true,

    var deletedBy : String? = ""
)
