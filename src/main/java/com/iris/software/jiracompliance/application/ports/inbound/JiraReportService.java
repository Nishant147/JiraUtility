package com.iris.software.jiracompliance.application.ports.inbound;

import com.iris.software.jiracompliance.application.model.CustomIssue;

import java.util.List;

public interface JiraReportService {
    void generateIndividualReportedHoursReport(List<CustomIssue> jiraCustomIssues);
}
