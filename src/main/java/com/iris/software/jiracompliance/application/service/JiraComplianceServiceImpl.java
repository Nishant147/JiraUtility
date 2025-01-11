package com.iris.software.jiracompliance.application.service;

import com.iris.software.jiracompliance.application.model.ComplianceReport;
import com.iris.software.jiracompliance.application.model.CustomIssue;
import com.iris.software.jiracompliance.application.model.ValidationRule;
import com.iris.software.jiracompliance.application.ports.inbound.JiraComplianceService;
import com.iris.software.jiracompliance.application.ports.outbound.EmailNotification;
import com.iris.software.jiracompliance.application.ports.outbound.ReportGenerator;
import com.iris.software.jiracompliance.application.ports.outbound.TemplateEngine;
import com.iris.software.jiracompliance.application.utils.ComplianceReportComparator;
import com.iris.software.jiracompliance.inbound.configuration.TeamConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JiraComplianceServiceImpl implements JiraComplianceService {
	@Value("${app.compliance.email.from}")
	private String from;
	@Value("${app.compliance.email.cc}")
	private Set<String> cc;
	@Value("${app.compliance.email.subject}")
	private String subject;

	public static final String ATTACHMENT_FILENAME = "Report.xlsx";

	@Autowired
	private EmailNotification notificationService;
	@Autowired
	private ReportGenerator reportGenerator;
	@Autowired
	private TemplateEngine templateEngine;
	@Autowired
	private TeamConfig teamEmailMapping;

	@Override
	public void generateComplianceReport(Map<String, List<ValidationRule>> validationRulesMap,
			Map<String, List<CustomIssue>> jiraCustomIssuesMap) {
		List<ComplianceReport> reports = validateObjects(validationRulesMap, jiraCustomIssuesMap);
		if (reports.isEmpty()) {
			notificationService.sendMail(from, cc, Collections.emptySet(), subject,
					templateEngine.getEmptyComplianceReport());
		} else {
			Set<String> to = new HashSet<>();
			Set<String> missingEmails = new HashSet<>();

			jiraCustomIssuesMap.values().stream()
					.flatMap(Collection::stream)
					.forEach(issue -> {
						String assigneeName = (String) issue.getIssueFieldMap().get("assignee");
						String email = (String) issue.getIssueFieldMap().get("assigneeEmailId");
						if(StringUtils.isNotBlank(email)){
							to.add(email);
						}else if(!StringUtils.equals("Unassigned",assigneeName)){
							if(StringUtils.isNotBlank(assigneeName)){
								email = teamEmailMapping.getTeams().get(assigneeName);
								if(StringUtils.isBlank(email)){
									missingEmails.add(assigneeName);
								}else{
									to.add(email);
								}
							}
						}
					});
			InputStreamSource inputStreamSource = reportGenerator.generateComplianceReport(reports);
			notificationService.sendMail(from, to, cc, subject,
					templateEngine.getComplianceReportWithDefaultersList(reports, missingEmails), ATTACHMENT_FILENAME,
					inputStreamSource);
		}
	}

	private List<ComplianceReport> validateObjects(Map<String, List<ValidationRule>> validationRules,
			Map<String, List<CustomIssue>> issueTypeMap) {
		List<ComplianceReport> complianceReportDTOS = new ArrayList<>();
		for (Map.Entry<String, List<CustomIssue>> issueMap : issueTypeMap.entrySet()) {
			String issueType = issueMap.getKey();
			List<CustomIssue> issue = issueMap.getValue();
			List<ValidationRule> rules = validationRules.get(issueType);
			for (CustomIssue customIssue : issue) {
				try {
					List<String> validationErrors = new ArrayList<>();
					for (ValidationRule rule : rules) {
						Object fieldValue = customIssue.getIssueFieldMap().get(rule.getField());
						validationErrors.addAll(applyRule(rule.getField(), fieldValue, rule));
					}
					ComplianceReport complianceReportDTO = populateComplianceReportData(customIssue, validationErrors);
					complianceReportDTOS.add(complianceReportDTO);
				} catch (Exception e) {
					log.error("Error validating Rules against Jira Issues!!", e);
					// validationErrors.add("Field not found in object " + issue);
				}

			}
		}
		return complianceReportDTOS.stream().sorted(new ComplianceReportComparator()).toList();
	}

	private ComplianceReport populateComplianceReportData(CustomIssue customIssue, List<String> validationErrors) {
		Map<String, Object> issueFieldMap = customIssue.getIssueFieldMap();
		return ComplianceReport.builder().key(issueFieldMap.get("key").toString()).issueType(customIssue.getIssueType())
				.assignee(issueFieldMap.get("assignee").toString()).summary(issueFieldMap.get("summary").toString())
				.status(issueFieldMap.get("status").toString())
				.message(String.join(System.lineSeparator(), validationErrors)).build();
	}

	private List<String> applyRule(String fieldName, Object fieldValue, ValidationRule rule) {
		if (fieldValue == null || fieldValue.toString().isEmpty()) {
			return List.of(fieldName + " cannot be empty");
		} else if (rule.getLimit() != null && fieldValue.toString().length() < rule.getLimit()) {
			return List.of(fieldName + " must be at least " + rule.getLimit().intValue() + " characters long");
		} else if (rule.getAdditionalRule() != null) {
			List<String> fieldValue1 = applyAdditionalRules(fieldName, fieldValue, rule.getAdditionalRule());
			if (fieldValue1 != null)
				return fieldValue1;
		}
		return Collections.emptyList();
	}

	private List<String> applyAdditionalRules(String fieldName, Object fieldValue, String rule) {
		if ("daysWithoutAnUpdate".equals(fieldName) && StringUtils.isNumeric(rule)
				&& ((Long) fieldValue).compareTo(Long.parseLong(rule)) > 0) {
			return List.of(
					"There Hasn't been any update since : %s days".formatted((Long) fieldValue - Long.parseLong(rule)));
		} else if ("attachmentFileName".equals(fieldName) && StringUtils.isNotBlank(rule)
				&& fieldValue instanceof ArrayList<?>) {
			List<String> fileAttachmentRegex = List.of(rule.split(","));
			@SuppressWarnings("unchecked")
			ArrayList<String> attachedFileNames = (ArrayList<String>) fieldValue;
			return fileAttachmentRegex.stream().filter(s -> isFileMissing(s, attachedFileNames))
					.map("File with Pattern %s is missing"::formatted).toList();
		}
		return null;
	}

	private boolean isFileMissing(String fileNameRegex, List<String> attachedFileNames) {
		boolean fileIsMissing = true;
		for (String attachedFileName : attachedFileNames) {
			if (attachedFileName.matches(fileNameRegex)) {
				fileIsMissing = false;
				break;
			}
		}
		return fileIsMissing;
	}
}
