package com.iris.software.jiracompliance.outbound.email;

import com.iris.software.jiracompliance.application.model.ComplianceReport;
import com.iris.software.jiracompliance.application.model.CustomIssue;
import com.iris.software.jiracompliance.application.ports.outbound.TemplateEngine;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TemplateEngineImpl implements TemplateEngine {
    public static final String COMPLIANCE_REPORT_TEMPLATE_FTL = "message-email-template.ftl";
    public static final String HOURLY_REPORT_TEMPLATE_FTL = "hourly-report-email-template.ftl";
    public static final String EMPTY_COMPLIANCE_REPORT_TEMPLATE_FTL = "empty-message-email-template.ftl";

    private Configuration freeMarkerConfig;

    @Override
    public String getEmptyComplianceReport() {
        String htmlBody = StringUtils.EMPTY;
        try {
            Template template = freeMarkerConfig.getTemplate(EMPTY_COMPLIANCE_REPORT_TEMPLATE_FTL);
            htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, new Object());
        } catch (IOException e) {
            log.error("Error getting Free-Marker template", e);
        } catch (TemplateException e) {
            log.error("Error creating email Body using Free-Marker template", e);
        }
        return htmlBody;
    }

    @Override
    public String getComplianceReportWithDefaultersList(List<ComplianceReport> reports, Set<String> missingDefaultersEmail) {
        String htmlBody = StringUtils.EMPTY;
        try {
            Map<String, Integer> defaulters = reports.stream().collect(Collectors.groupingBy(ComplianceReport::getAssignee, Collectors.summingInt(e -> 1)));
            Map<String, Object> model = new HashMap<>();
            model.put("totalCount", reports.size());
            model.put("data", defaulters);
            model.put("missingEmail", missingDefaultersEmail);
            Template template = freeMarkerConfig.getTemplate(COMPLIANCE_REPORT_TEMPLATE_FTL);
            htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException e) {
            log.error("Error getting Free-Marker template", e);
        } catch (TemplateException e) {
            log.error("Error creating email Body using Free-Marker template", e);
        }
        return htmlBody;
    }

    @Override
    public String getResourceHourlyReport(Map<String, CustomIssue> reports) {
        String htmlBody = StringUtils.EMPTY;
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("data", reports);
            Template template = freeMarkerConfig.getTemplate(HOURLY_REPORT_TEMPLATE_FTL);
            htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException e) {
            log.error("Error getting Hourly Report Free-Marker template", e);
        } catch (TemplateException e) {
            log.error("Error creating html using Hourly Report Free-Marker template", e);
        }
        return htmlBody;
    }
}

