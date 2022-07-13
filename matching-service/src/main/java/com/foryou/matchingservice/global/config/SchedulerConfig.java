package com.foryou.matchingservice.global.config;

import com.foryou.matchingservice.global.error.ScheduledExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler tpts = new ThreadPoolTaskScheduler();
        tpts.setPoolSize(10);
        tpts.setThreadNamePrefix("matching-scheduled-task-pool-");
        tpts.setErrorHandler(new ScheduledExceptionHandler());
        tpts.initialize();

        taskRegistrar.setTaskScheduler(tpts);
    }
}
