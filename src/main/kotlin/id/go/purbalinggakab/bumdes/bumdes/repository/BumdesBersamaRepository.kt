package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.BumdesBersamaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface BumdesBersamaRepository : JpaRepository<BumdesBersamaEntity, String>, JpaSpecificationExecutor<BumdesBersamaEntity> {

    fun getBumdesBersamaByIdBumdes(idBumdes : String) : BumdesBersamaEntity?

    fun existsBumdesBersamaByIdBumdes(idBumdes: String) : Boolean

    fun existsBumdesBersamaByIdBumdesAndIdDesaAndIdKecamatan(idBumdes: String,
                                                             idDesa : Long,
                                                             idKecamatan : Long) : Boolean


    fun countBumdesBersamaByIdBumdes(idBumdes: String) : Int
}