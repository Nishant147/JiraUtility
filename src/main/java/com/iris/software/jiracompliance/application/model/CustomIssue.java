package com.iris.software.jiracompliance.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomIssue {
    private Long id;
    private String issueType;
    private Map<String, Object> issueFieldMap;
    private Map<String, Integer> workLogs;
   }
