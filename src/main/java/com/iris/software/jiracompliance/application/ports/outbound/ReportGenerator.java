package com.iris.software.jiracompliance.application.ports.outbound;

import com.iris.software.jiracompliance.application.model.ComplianceReport;
import org.springframework.core.io.InputStreamSource;

import java.util.List;

public interface ReportGenerator {
    InputStreamSource generateComplianceReport(List<ComplianceReport> reportItems);
}
