package com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryOperatorEnum;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class TimeWithinTaskTimeQueryGenerator extends TimeTemplateQueryGenerator {

    public TimeWithinTaskTimeQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(QueryOperatorEnum.GREATER_THAN_OR_EQUALS, eventLog, templateValues);
    }

    @Override
    public void processQueryParameters(Map<String, String> templateValues) {

        taskP = templateValues.get("task_p");
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

        if (taskP == null) {
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
                SELECT DISTINCT %s
                FROM %s
                WHERE %s = '%s'
                AND TIMESTAMPDIFF(%s, %s, %s) %s %s;
                """,
                caseIdColumnName,
                tableName,
                activityColumnName,
                taskP,
                kUnit,
                startTimeColumnName,
                completeTimeColumnName,
                operator.getOperator(),
                kValue
        );
    }

}
