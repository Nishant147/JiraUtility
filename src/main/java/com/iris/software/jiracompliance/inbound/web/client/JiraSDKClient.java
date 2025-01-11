package com.iris.software.jiracompliance.inbound.web.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.iris.software.jiracompliance.application.model.CustomIssue;
import com.iris.software.jiracompliance.inbound.configuration.AppConfigLoader;
import com.iris.software.jiracompliance.inbound.web.mapper.JiraModelMapper;

import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@NoArgsConstructor
@Profile("JiraSDKClient")
public class JiraSDKClient implements JiraClient {

	@Value("${app.jira.url}")
	private String jiraUrl;
	@Value("${app.jira.username}")
	private String username;
	@Value("${app.jira.apiToken}")
	private String apiToken;
	@Value("${app.jira.maxResults:50}")
	private int maxResults;

	@Autowired
	private AppConfigLoader appConfigLoader;
	@Autowired
	private JiraModelMapper jiraModelMapper;

	private JiraRestClient jiraRestClient;

	@PostConstruct
	private void init() throws URISyntaxException {
		URI jiraServerUri = new URI(jiraUrl);
		this.jiraRestClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(jiraServerUri,
				username, apiToken);
	}

	@Override
	public Map<String, List<CustomIssue>> getJiraCustomIssuesMap() {
		List<Issue> jiraIssues = getJiraIssues();
		return jiraModelMapper.toCustomIssues(jiraIssues).stream()
				.collect(Collectors.groupingBy(CustomIssue::getIssueType));
	}

	@Override
	public List<CustomIssue> getJiraCustomIssues() {
		List<Issue> jiraIssues = getJiraIssues();
		return jiraModelMapper.toCustomIssues(jiraIssues);
	}

	private List<Issue> getJiraIssues() {
		String jqlQuery = appConfigLoader.getJiraJQL();

		SearchResult searchResult = this.jiraRestClient.getSearchClient()
				.searchJql(jqlQuery, maxResults, 0, Set.of("*all")).claim();

		return StreamSupport.stream(searchResult.getIssues().spliterator(), false).toList();
	}
}
