package com.apromore.compliance_centre.compliancerules.querygenerator.resourcetemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryGenerator;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class ResourceBoundedWithQueryGenerator extends QueryGenerator {
    String taskOne;
    String taskTwo;
    public ResourceBoundedWithQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(eventLog, templateValues);
        
    }

    @Override
    public String getQuery() {
        return String.format(
            "SELECT DISTINCT a.%s FROM %s a INNER JOIN %s b ON a.%s = b.%s WHERE a.%s = '%s' AND b.%s = '%s' AND a.%s <> b.%s",
                caseIdColumnName,
                tableName,
                tableName,
                caseIdColumnName,
                caseIdColumnName,
                activityColumnName,
                taskOne,
                activityColumnName,
                taskTwo,
                resourceColumnName,
                resourceColumnName
        );
    }

    @Override
    public void processQueryParameters(Map<String, String> templateValues) {
        taskOne = templateValues.get("task_1");
        taskTwo = templateValues.get("task_2");
    }
}
