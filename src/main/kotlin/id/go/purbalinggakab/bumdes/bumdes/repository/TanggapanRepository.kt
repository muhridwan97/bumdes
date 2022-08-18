package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.TanggapanEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface TanggapanRepository : JpaRepository<TanggapanEntity, String>, JpaSpecificationExecutor<TanggapanEntity> {
}