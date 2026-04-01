package com.example.meeting.reservation.config;

import com.example.meeting.reservation.job.RoomTimeSlotGenerationJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class QuartzConfig {

    @Bean
    public JobDetail roomTimeSlotGenerationJobDetail() {
        return JobBuilder.newJob(RoomTimeSlotGenerationJob.class)
                .withIdentity("roomTimeSlotGenerationJob")
                .withDescription("예약 시간 슬롯 삽입 작업")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger roomTimeSlotGenerationJobTrigger(@Qualifier("roomTimeSlotGenerationJobDetail") JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("roomTimeSlotGenerationJobTrigger")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 10 0 * * ?").withMisfireHandlingInstructionFireAndProceed()
                )
                .build();
    }
}
