package com.apromore.compliance_centre.compliancerules.querygenerator.resourcetemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryGenerator;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class ResourceSegregatedFromQueryGenerator extends QueryGenerator {

    String taskOne;
    String taskTwo;

    public ResourceSegregatedFromQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(eventLog, templateValues);
    }

    @Override
    public String getQuery() {
        return String.format(
            "SELECT DISTINCT t1.%s FROM %s t1 JOIN %s t2 ON t1.%s = t2.%s WHERE (t1.%s = t2.%s OR t1.%s = t2.%s) AND t1.%s = '%s' AND t2.%s = '%s'",
            caseIdColumnName,
            tableName,
            tableName,
            caseIdColumnName,
            caseIdColumnName,
            resourceColumnName,
            resourceColumnName,
            roleColumnName,
            roleColumnName,
            activityColumnName,
            taskOne,
            activityColumnName,
            taskTwo
        );
    }

    @Override
    public void processQueryParameters(Map<String, String> templateValues) {
        taskOne = templateValues.get("task_1");
        taskTwo = templateValues.get("task_2");
    }
}
