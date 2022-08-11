package id.go.purbalinggakab.bumdes.repository

import id.go.purbalinggakab.bumdes.entity.KecamatanEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface KecamatanRepository : JpaRepository<KecamatanEntity, String>, JpaSpecificationExecutor<KecamatanEntity> {

}