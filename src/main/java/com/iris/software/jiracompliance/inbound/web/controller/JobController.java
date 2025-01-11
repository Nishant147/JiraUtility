package com.iris.software.jiracompliance.inbound.web.controller;

import com.iris.software.jiracompliance.inbound.scheduler.ComplianceGeneratorJob;
import com.iris.software.jiracompliance.inbound.scheduler.ReportGeneratorJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping(path = "jobs")
@AllArgsConstructor
public class JobController {
    private Scheduler scheduler;
    private Environment environment;

    @PostMapping("compliance/trigger")
    public ResponseEntity<String> triggerComplianceJob() throws SchedulerException{
        JobDetail jobDetail = JobBuilder.newJob(ComplianceGeneratorJob.class)
                .withIdentity("ComplianceGenerator")
                .build();
        scheduler.triggerJob(jobDetail.getKey());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("report/trigger")
    public ResponseEntity<String> triggerReportJob() throws SchedulerException{
        String[] activeProfiles = environment.getActiveProfiles();
        if(Arrays.stream(activeProfiles).anyMatch("JiraXMLClient"::contains)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Profile JiraXMLClient is not acceptable for current report");
        }
        JobDetail jobDetail = JobBuilder.newJob(ReportGeneratorJob.class)
                .withIdentity("ReportGenerator")
                .build();
        scheduler.triggerJob(jobDetail.getKey());
        return ResponseEntity.accepted().build();
    }
}
