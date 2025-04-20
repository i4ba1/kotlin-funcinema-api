package com.dev.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Configuration
@EnableAsync
class AsyncConfig : AsyncConfigurer {

    @Bean(name = ["virtualThreadTaskExecutor"])
    fun virtualThreadTaskExecutor(): Executor {
        // Creating an executor that uses virtual threads
        // Each task submitted to this executor will run in its own virtual thread
        return Executors.newVirtualThreadPerTaskExecutor()
    }

    @Bean(name = ["taskExecutor"])
    override fun getAsyncExecutor(): Executor {
        // This is a fallback executor using platform threads
        // We define this for compatibility and demonstration purposes
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 10
        executor.maxPoolSize = 50
        executor.queueCapacity = 100
        executor.setThreadNamePrefix("MovieApi-Thread-")
        executor.initialize()
        return executor
    }
}