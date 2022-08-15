package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.UnitUsahaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface UnitUsahaRepository : JpaRepository<UnitUsahaEntity, String>, JpaSpecificationExecutor<UnitUsahaEntity> {

    fun getUnitUsahaByNamaUnitUsaha(nama : String) : UnitUsahaEntity?
}