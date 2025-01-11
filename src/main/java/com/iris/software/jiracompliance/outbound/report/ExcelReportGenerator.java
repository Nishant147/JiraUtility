package com.iris.software.jiracompliance.outbound.report;

import com.iris.software.jiracompliance.application.model.ComplianceReport;
import com.iris.software.jiracompliance.application.ports.outbound.ReportGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ExcelReportGenerator implements ReportGenerator {
    @Override
    public InputStreamSource generateComplianceReport(List<ComplianceReport> reportItems) {
        try(XSSFWorkbook workbook = generateComplianceExcelWorkbook(reportItems)){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private XSSFWorkbook generateComplianceExcelWorkbook(List<ComplianceReport> reportItems) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Compliance Report");
        String[] headers = { "Key", "Issue Type", "Assignee", "Summary", "Status", "Message" };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            // Apply style if needed
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        int rowNum = 1;
        for (ComplianceReport reportItem : reportItems) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(reportItem.getKey());
            row.createCell(1).setCellValue(reportItem.getIssueType());
            row.createCell(2).setCellValue(reportItem.getAssignee());
            row.createCell(3).setCellValue(reportItem.getSummary());
            row.createCell(4).setCellValue(reportItem.getStatus());
            Cell messageCell = row.createCell(5);
            messageCell.setCellValue(reportItem.getMessage());
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);
            messageCell.setCellStyle(cellStyle);
        }
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i); // Auto-size columns
        }
        return workbook;
    }
}
