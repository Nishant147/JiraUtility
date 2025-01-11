package com.iris.software.jiracompliance.application.service;

import com.iris.software.jiracompliance.application.model.CustomIssue;
import com.iris.software.jiracompliance.application.ports.inbound.JiraReportService;
import com.iris.software.jiracompliance.application.ports.outbound.EmailNotification;
import com.iris.software.jiracompliance.application.ports.outbound.TemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JiraReportServiceImpl implements JiraReportService {
    @Value("${app.report.email.from}")
    private String from;
    @Value("${app.report.email.to}")
    private Set<String> to;
    @Value("${app.report.email.cc}")
    private Set<String> cc;
    @Value("${app.report.email.subject}")
    private String subject;

    @Autowired
    private EmailNotification notificationService;
    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void generateIndividualReportedHoursReport(List<CustomIssue> jiraCustomIssues) {
        Map<String, CustomIssue> customIssueMap = jiraCustomIssues.stream().collect(Collectors.toMap(customIssue -> {
            return (String) customIssue.getIssueFieldMap().get("key");
        }, element -> element));
        notificationService.sendMail(from, to, cc, subject, templateEngine.getResourceHourlyReport(customIssueMap));
    }
}
