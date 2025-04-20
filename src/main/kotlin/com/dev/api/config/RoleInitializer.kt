package com.dev.api.config

import com.dev.funcinema.model.Role
import com.dev.funcinema.repository.RoleRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RoleInitializer(private val roleRepository: RoleRepository) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun initRoles(): CommandLineRunner = CommandLineRunner {
        // Initialize roles if they don't exist
        if (roleRepository.count() == 0L) {
            log.info("Initializing roles...")

            roleRepository.save(Role(name = Role.ERole.ROLE_USER))
            roleRepository.save(Role(name = Role.ERole.ROLE_MODERATOR))
            roleRepository.save(Role(name = Role.ERole.ROLE_ADMIN))

            log.info("Roles initialized successfully")
        }
    }
}