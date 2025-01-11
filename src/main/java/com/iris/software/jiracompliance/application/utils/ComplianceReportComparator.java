package com.iris.software.jiracompliance.application.utils;

import com.iris.software.jiracompliance.application.model.ComplianceReport;

import java.util.Comparator;

public class ComplianceReportComparator implements Comparator<ComplianceReport> {
    @Override
    public int compare(ComplianceReport o1, ComplianceReport o2) {
        return o1.getAssignee().compareTo(o2.getAssignee());
    }
}
