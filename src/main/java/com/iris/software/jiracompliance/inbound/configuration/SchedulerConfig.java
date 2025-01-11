package com.iris.software.jiracompliance.inbound.configuration;

import com.iris.software.jiracompliance.inbound.scheduler.ComplianceGeneratorJob;
import com.iris.software.jiracompliance.inbound.scheduler.ReportGeneratorJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerConfig {
    @Value("${app.report.scheduler.cron}")
    private String jiraReportGeneratorCron;
    @Value("${app.compliance.scheduler.cron}")
    private String jiraComplianceGeneratorCron;

    @Bean
    public JobDetail reportGeneratorJobDetail(){
        return JobBuilder.newJob(ReportGeneratorJob.class)
                .withIdentity("ReportGenerator")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger reportGenerationTrigger(){
        return TriggerBuilder.newTrigger()
                .withIdentity("reportGeneratorTrigger")
                .forJob(reportGeneratorJobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule(jiraReportGeneratorCron))
                .build();
    }

    @Bean
    public JobDetail complianceGeneratorJobDetail(){
        return JobBuilder.newJob(ComplianceGeneratorJob.class)
                .withIdentity("ComplianceGenerator")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger dailyComplianceGenerationTrigger(){
        return TriggerBuilder.newTrigger()
                .withIdentity("complianceGeneratorTrigger")
                .forJob(complianceGeneratorJobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule(jiraComplianceGeneratorCron))
                .build();
    }
}
