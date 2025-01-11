package com.iris.software.jiracompliance.application.model;

import lombok.Data;

import java.util.List;

@Data
public class ValidationRule {
	private String ruleName;
	private String field;
	private String rule1;
	private String additionalRule;

	private List<String>  status;
	private Double limit;

	public ValidationRule(String field, List<String> status, String rule1, Double limit, String additionalRule) {
		this.field = field;
		this.status = status;
		this.rule1 = rule1;
		this.limit = limit;
		this.additionalRule = additionalRule;
	}
}
