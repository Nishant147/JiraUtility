<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Jira Compiance Report</title>
        <style>
            body{
                font-family: "Segoe UI", Tahoma, Verdana, Sans-Serif;
            }
            table{
                width: 600px;
            }
            table tbody tr{
                border: black 1px solid;
            }
            table thead tr th{
                font-weight: bold;
                font-style: normal;
                vertical-align: baseline;
                font-kerning: none;
                background-color: rgb(21, 96, 130);
                color: rgb(255, 255, 255);
                text-align: left;
                font-size: 14px;
            }
            table tr:nth-child(even) {
                background-color: #9ad4ef;
            }
            table tr:nth-child(odd){
                background-color: #ffffff;
            }
        </style>
    </head>
    <body>
        <p>Hi all,</p>
        <p>Below is the list of Hours Reported in JIRA's</p>
        <table>
            <thead>
                <tr>
                    <th>Resource Name</th>
                    <#list data?keys as jiraId>
                        <th>${jiraId}</th>
                    </#list>
                    <th>Total Hours</th>
                </tr>
            </thead>
            <tbody>
                <#assign members=[]/>
                <#list data?values as jiraIssue>
                    <#if jiraIssue.workLogs?has_content>
                        <#list jiraIssue.workLogs?keys as member>
                            <#if !(members?seq_contains(member))>
                                <#assign members = members + [member]/>
                            </#if>
                        </#list>
                    </#if>
                </#list>
                <#list members as member>
                    <tr>
                        <td>${member}</td>
                        <#assign totalHours = 0/>
                        <#list data?values as jiraIssue>
                            <#if jiraIssue.workLogs[member]?has_content>
                                <td>${jiraIssue.workLogs[member]/60}</td>
                                <#assign totalHours += jiraIssue.workLogs[member]/>
                            <#else>
                                <td>0</td>
                            </#if>
                        </#list>
                        <td>${totalHours/60}</td>
                    </tr>
                </#list>
            <tbody>
        </table>
    </body>
</html>