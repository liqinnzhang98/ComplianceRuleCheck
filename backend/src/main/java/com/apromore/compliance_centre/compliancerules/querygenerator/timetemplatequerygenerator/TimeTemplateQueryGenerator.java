package com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator;

import java.util.Map;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.QueryOperatorEnum;
import com.apromore.compliance_centre.eventlogs.EventLogModel;

public class TimeTemplateQueryGenerator extends QueryGenerator {
    
    QueryOperatorEnum operator;
    String taskP;
    String taskQ;
    String kUnit;
    String kTime;
    String kValue;    

    TimeTemplateQueryGenerator(QueryOperatorEnum operator, EventLogModel eventLog, Map<String, String> templateValues) {
        super(eventLog, templateValues);
        this.operator = operator;
    }

    @Override
    public void processQueryParameters(Map<String, String> templateValues) {

        taskP = templateValues.get("task_p");
        taskQ = templateValues.get("task_q");
        String kDuration = templateValues.get("duration_k");
        if (kDuration != null) {
            String[] duration = kDuration.split(" ");
            if (duration.length == 2) {
                kValue = duration[0];
                kUnit = duration[1];
            } else {
                kValue = null;
                kUnit = null;
            }
        }

        if (taskP == null || taskQ == null) {
            throw new IllegalArgumentException("Missing task");
        }
        if (kUnit == null) {
            throw new IllegalArgumentException("Missing unit");
        }
        if (kValue == null) {
            throw new IllegalArgumentException("Missing value");
        }

    }

    @Override
    public String getQuery() {
        return String.format("""
                SELECT DISTINCT a1.%s
                FROM %s AS a1
                JOIN %s AS a2
                ON a1.%s = a2.%s
                AND a1.%s = '%s'
                AND a2.%s = '%s'
                AND TIMESTAMPDIFF(%s, a1.%s, a2.%s) %s %s;
                """,
                caseIdColumnName,
                tableName,
                tableName,
                caseIdColumnName,
                caseIdColumnName,
                activityColumnName,
                taskP,
                activityColumnName,
                taskQ,
                kUnit,
                startTimeColumnName,
                startTimeColumnName,
                operator.getOperator(),
                kValue
        );
    }
    
}
