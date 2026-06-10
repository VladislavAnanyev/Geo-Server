package com.example.meetings.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

import static com.example.meetings.util.Constants.GEOLOCATION_MEETING_EXECUTOR;

@Configuration
public class GeolocationMeetingAsyncConfig {

    @Bean(name = GEOLOCATION_MEETING_EXECUTOR)
    public ThreadPoolTaskExecutor geolocationMeetingExecutor(
            @Value("${geolocation.meeting-async.core-pool-size:8}") int corePoolSize,
            @Value("${geolocation.meeting-async.max-pool-size:16}") int maxPoolSize,
            @Value("${geolocation.meeting-async.queue-capacity:500}") int queueCapacity
    ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("geolocation-meeting-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
