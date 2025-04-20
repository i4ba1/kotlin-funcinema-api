package com.dev.api.repository

import com.dev.api.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleRepository : JpaRepository<Role, Int> {
    fun findByName(name: Role.ERole): Optional<Role>
}