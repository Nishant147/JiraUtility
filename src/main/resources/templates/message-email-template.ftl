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
                width: 400px;
            }
            table tbody tr{
                border: black 1px solid;
            }
            table thead tr td{
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
        <p>List of non-compliant JIRA's has been attached herewith. Kindly take appropriate actions against your Jira's. <b>Total Jira Count : ${totalCount}</b></p>
        <table>
            <thead>
                <tr>
                    <td>#</td>
                    <td>Name</td>
                    <td>Count</td>
                </tr>
            </thead>
            <tbody>
                <#assign counter=1>
                <#list data?keys as key>
                    <tr>
                        <td>${counter}</td>
                        <td>${key}</td>
                        <td>${data[key]}</td>
                    </tr>
                    <#assign counter = counter+1>
                </#list>
            <tbody>
        </table>
        <#if missingEmail?has_content>
            <p style="color:red;">P.S.: Following members email Id is missing in Jira and in configurations files:
                <ul>
                    <#list missingEmail as member>
                        <li>${member}</li>
                    </#list>
                </ul>
            </p>
        </#if>
    </body>
</html>