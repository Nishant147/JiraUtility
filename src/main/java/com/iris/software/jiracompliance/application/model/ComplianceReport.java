package com.iris.software.jiracompliance.application.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplianceReport {
    private String key;
    private String issueType;
    private String assignee;
    private String summary;
    private String status;
    private String message;
}
