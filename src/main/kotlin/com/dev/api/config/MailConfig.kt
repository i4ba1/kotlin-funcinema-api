package com.dev.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.Properties

@Configuration
class MailConfig {

    @Value("\${spring.mail.host:smtp.gmail.com}")
    private lateinit var host: String

    @Value("\${spring.mail.port:587}")
    private var port: Int = 0

    @Value("\${spring.mail.username:}")
    private lateinit var username: String

    @Value("\${spring.mail.password:}")
    private lateinit var password: String

    @Value("\${spring.mail.properties.mail.smtp.auth:true}")
    private lateinit var auth: String

    @Value("\${spring.mail.properties.mail.smtp.starttls.enable:true}")
    private lateinit var starttls: String

    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port

        // Only set username/password if they're actually configured
        if (username.isNotEmpty() && password.isNotEmpty()) {
            mailSender.username = username
            mailSender.password = password
        }

        val props = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = auth
        props["mail.smtp.starttls.enable"] = starttls
        props["mail.debug"] = "true" // Good for development, remove in production

        return mailSender
    }
}