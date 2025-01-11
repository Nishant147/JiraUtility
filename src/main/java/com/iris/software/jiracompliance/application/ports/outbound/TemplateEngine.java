package com.iris.software.jiracompliance.application.ports.outbound;

import com.iris.software.jiracompliance.application.model.ComplianceReport;
import com.iris.software.jiracompliance.application.model.CustomIssue;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TemplateEngine {
    String getEmptyComplianceReport();
    String getComplianceReportWithDefaultersList(List<ComplianceReport> reports, Set<String> missingDefaultersEmail);
    String getResourceHourlyReport(Map<String, CustomIssue> customIssueMap);
}
