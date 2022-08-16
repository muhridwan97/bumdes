package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.KonsultasiBumdesEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface KonsultasiBumdesRepository : JpaRepository<KonsultasiBumdesEntity, String>, JpaSpecificationExecutor<KonsultasiBumdesEntity> {

}