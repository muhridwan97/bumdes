package id.go.purbalinggakab.bumdes.dami.repository

import id.go.purbalinggakab.bumdes.dami.entity.DesaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface DesaRepository : JpaRepository<DesaEntity, Long>, JpaSpecificationExecutor<DesaEntity> {

    fun findAllByKodeKec(idKec : Long) : List<DesaEntity>
}