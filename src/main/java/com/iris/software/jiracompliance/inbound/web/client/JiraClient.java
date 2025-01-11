package com.iris.software.jiracompliance.inbound.web.client;

import java.util.List;
import java.util.Map;

import com.iris.software.jiracompliance.application.model.CustomIssue;

public interface JiraClient {
	Map<String, List<CustomIssue>> getJiraCustomIssuesMap();

	List<CustomIssue> getJiraCustomIssues();
}