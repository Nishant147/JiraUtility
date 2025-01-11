package com.iris.software.jiracompliance.inbound.scheduler;

import com.iris.software.jiracompliance.application.ports.inbound.JiraComplianceService;
import com.iris.software.jiracompliance.inbound.configuration.AppConfigLoader;
import com.iris.software.jiracompliance.inbound.web.client.JiraClient;
import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ComplianceGeneratorJob implements Job {
    private JiraClient jiraClient;
    private JiraComplianceService service;
    private AppConfigLoader appConfig;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        service.generateComplianceReport(appConfig.validationRules(), jiraClient.getJiraCustomIssuesMap());
    }
}
