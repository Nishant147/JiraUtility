package com.iris.software.jiracompliance.application.ports.inbound;

import com.iris.software.jiracompliance.application.model.CustomIssue;
import com.iris.software.jiracompliance.application.model.ValidationRule;

import java.util.List;
import java.util.Map;

public interface JiraComplianceService {
    void generateComplianceReport(Map<String, List<ValidationRule>> stringListMap,
                                  Map<String, List<CustomIssue>> jiraCustomIssues);
}
