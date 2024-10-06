package com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryOperatorEnum;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class TimeExactlyAtQueryGenerator extends TimeTemplateQueryGenerator {

    public TimeExactlyAtQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(QueryOperatorEnum.NOT_EQUALS, eventLog, templateValues);
    }

    @Override
    public void processQueryParameters(Map<String, String> templateValues) {

        taskP = templateValues.get("task_p");
        taskQ = templateValues.get("task_q");
        kTime = templateValues.get("time_k");

        if (taskP == null || taskQ == null) {
            throw new IllegalArgumentException("Missing task");
        }

    }

    @Override
    public String getQuery() {
        // TODO: fix this query
        return String.format("""
                SELECT DISTINCT a1.%s
                FROM %s AS a1
                JOIN %s AS a2
                ON a1.%s = a2.%s
                AND a1.%s = '%s'
                AND a2.%s = '%s'
                AND (TIMESTAMPDIFF(MICROSECOND, a1.%s, a2.%s) <= 0 /* task P follows task Q */
                OR TIMESTAMPDIFF(DAY, a1.%s, a2.%s) > 0 /* within a day */
                OR TIME(a2.%s) %s '%s');
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
                completeTimeColumnName,
                startTimeColumnName,
                completeTimeColumnName,
                startTimeColumnName,
                startTimeColumnName,
                operator.getOperator(),
                kTime
        );
    }

}
