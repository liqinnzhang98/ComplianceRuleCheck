package com.apromore.compliance_centre.compliancerules.querygenerator.resourcetemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryGenerator;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class ResourceRoleBoundedWithQueryGenerator extends QueryGenerator {
    String taskOne;
    String taskTwo;
    String roleR;
    public ResourceRoleBoundedWithQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(eventLog, templateValues);
    }

    @Override
    public String getQuery() {
        return String.format(
            "SELECT DISTINCT a.%s FROM %s a JOIN %s b ON a.%s = b.%s WHERE a.%s = '%s' AND a.%s = b.%s AND a.%s = '%s' AND b.%s = '%s'",
                caseIdColumnName,
                tableName,
                tableName,
                caseIdColumnName,
                caseIdColumnName,
                roleColumnName,
                roleR,
                resourceColumnName,
                resourceColumnName,
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
        roleR = templateValues.get("role_r");
    }
}
