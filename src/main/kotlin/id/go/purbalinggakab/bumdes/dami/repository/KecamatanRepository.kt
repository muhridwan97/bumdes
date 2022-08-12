package id.go.purbalinggakab.bumdes.dami.repository

import id.go.purbalinggakab.bumdes.dami.entity.KecamatanEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface KecamatanRepository : JpaRepository<KecamatanEntity, Long>, JpaSpecificationExecutor<KecamatanEntity> {

}