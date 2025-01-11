package com.iris.software.jiracompliance.inbound.scheduler;

import com.iris.software.jiracompliance.application.ports.inbound.JiraReportService;
import com.iris.software.jiracompliance.inbound.web.client.JiraClient;
import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReportGeneratorJob implements Job {
    private JiraClient client;
    private JiraReportService service;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        service.generateIndividualReportedHoursReport(client.getJiraCustomIssues());
    }
}
