package com.dev.api.security

import com.dev.api.model.User
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserDetailsImpl(
    val id: Long? = null,
    private val username: String,
    val email: String,

    @JsonIgnore
    private val password: String,

    private val authorities: Collection<GrantedAuthority>,

    val emailVerified: Boolean,
    val mobileVerified: Boolean,
    val active: Boolean
) : UserDetails {

    companion object {
        fun build(user: User): UserDetailsImpl {
            val authorities = user.roles.map { role ->
                SimpleGrantedAuthority(role.name.name)
            }

            return UserDetailsImpl(
                id = user.id,
                username = user.username,
                email = user.email,
                password = user.password,
                authorities = authorities,
                emailVerified = user.emailVerified,
                mobileVerified = user.mobileVerified,
                active = user.active
            )
        }
    }

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = active
}