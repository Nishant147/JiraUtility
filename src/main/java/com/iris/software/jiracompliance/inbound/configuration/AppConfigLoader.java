package com.iris.software.jiracompliance.inbound.configuration;

import com.iris.software.jiracompliance.application.model.ValidationRule;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class AppConfigLoader {
    private static final String JQL_CONFIG_FILE_PATH = "/query.jql";
    private static final String VALIDATION_RULES_CONFIG_FILE_PATH = "/Validation.xlsx";

    public Map<String, List<ValidationRule>> validationRules() {
        Map<String, List<ValidationRule>> rules = new HashMap<>();
        try (InputStream inputStream = getClass().getResourceAsStream(VALIDATION_RULES_CONFIG_FILE_PATH);
            Workbook workbook = new XSSFWorkbook(Objects.requireNonNull(inputStream))) {
            Sheet sheet = workbook.getSheet("Rules");
            for (Row row : sheet) {
                if (row.getRowNum() != 0) { // Reading validations, Skipping Header at row 0
                    Cell issueTypeCell = row.getCell(0);
                    Cell statusCell = row.getCell(1);
                    Cell fieldCell = row.getCell(2);
                    Cell rule1Cell = row.getCell(3);
                    Cell rule2Cell = row.getCell(4);
                    Cell additionalRuleCell = row.getCell(5);

                    if (null != statusCell && null != fieldCell && null != issueTypeCell && null != rule1Cell) {
                        String issueType = issueTypeCell.getStringCellValue().trim();
                        List<String> status = Arrays.asList(statusCell.getStringCellValue().trim().split("\\|"));
                        String field = fieldCell.getStringCellValue().trim();
                        String rule1 = rule1Cell.getStringCellValue();
                        Double rule2 = rule2Cell != null ? rule2Cell.getNumericCellValue() : null;
                        String additionalRule = getCellValueAsString(additionalRuleCell);
                        ValidationRule rule = new ValidationRule(field, status, rule1, rule2, additionalRule);
                        rules.computeIfAbsent(issueType, f -> new ArrayList<>()).add(rule);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to load Validation Rules Excel File");
        }
        return rules;
    }

    public String getCellValueAsString(Cell cell){
        return Objects.isNull(cell) ? null : switch (cell.getCellType()){
            case _NONE, BLANK, ERROR, FORMULA -> null;
            case NUMERIC -> String.format("%.0f", cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case STRING -> cell.getStringCellValue();
        };
    }

    public String getJiraJQL(){
        try (InputStream inputStream = getClass().getResourceAsStream(JQL_CONFIG_FILE_PATH)){
            assert inputStream != null;
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unable to load Jira JQL File");
        }
    }
}
