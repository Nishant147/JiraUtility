package com.iris.software.jiracompliance.inbound.web.mapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import com.iris.software.jiracompliance.application.model.CustomIssue;

@Component
public class JiraModelMapper {
    public List<CustomIssue> toCustomIssues(List<Issue> issues){
        return issues.stream()
                .map(this::toCustomIssue)
                .collect(Collectors.toList());
    }

    public CustomIssue toCustomIssue(Issue issue){
        Map<String, Object> issueFields = new HashMap<>();
        issueFields.put("key",issue.getKey());
        issueFields.put("summary",issue.getSummary());
        issueFields.put("description",issue.getDescription());
        issueFields.put("status", issue.getStatus().getName());
        issueFields.put("lastUpdatedTimestamp",toLocalDate(issue.getUpdateDate()));
        issueFields.put("daysWithoutAnUpdate",ChronoUnit.DAYS.between(toLocalDate(issue.getUpdateDate()), LocalDateTime.now()));
        issueFields.put("assignee", issue.getAssignee() != null ? issue.getAssignee().getDisplayName()
                : "Unassigned");
        issueFields.put("assigneeEmailId", issue.getAssignee() != null ? issue.getAssignee().getEmailAddress() : null);
        issueFields.put("attachmentFileNames", getAttachmentFileNames(issue.getAttachments()));
        issueFields.put("comments", getIssueComments(issue.getComments()));
        issue.getFields().forEach(issueField -> {
            if(issueField != null){
                issueFields.put(issueField.getName(), issueField.getValue());
            }
        });
        return CustomIssue.builder()
                .id(issue.getId())
                .issueType(issue.getIssueType().getName())
                .issueFieldMap(issueFields)
                .workLogs(getWorkLogs(issue.getWorklogs()))
                .build();
    }

    private List<String> getAttachmentFileNames(Iterable<Attachment> attachments){
        Stream<Attachment> attachmentStream = StreamSupport.stream(attachments.spliterator(), false);
        return attachmentStream
                .map(Attachment::getFilename)
                .collect(Collectors.toList());
    }

    private Object getIssueComments(Iterable<Comment> comments) {
        Stream<Comment> commentStream = StreamSupport.stream(comments.spliterator(), false);
        return commentStream
                .map(Comment::getBody)
                .collect(Collectors.toList());
    }
    private Map<String, Integer> getWorkLogs(Iterable<Worklog> workLogs){
        Stream<Worklog> workLogsStream = StreamSupport.stream(workLogs.spliterator(), false);
        return workLogsStream
                .collect(Collectors.groupingBy(worklog -> worklog.getAuthor().getDisplayName(), Collectors.summingInt(Worklog::getMinutesSpent)));
    }

    public LocalDateTime toLocalDate(DateTime dateTime) {
        DateTime dateTimeUtc = dateTime.withZone(DateTimeZone.UTC);
        return LocalDateTime.of(
                dateTimeUtc.getYear(),
                dateTimeUtc.getMonthOfYear(),
                dateTimeUtc.getDayOfMonth(),
                dateTimeUtc.getHourOfDay(),
                dateTimeUtc.getMinuteOfHour(),
                dateTimeUtc.getSecondOfMinute());
    }
}
