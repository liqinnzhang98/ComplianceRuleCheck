package com.apromore.compliance_centre.compliancerules.querygenerator.resourcetemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryGenerator;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class ResourcePerformedByQueryGenerator extends QueryGenerator {

    String taskT;
    String resourceR;

    public ResourcePerformedByQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(eventLog, templateValues);
    }

    @Override
    public String getQuery() {
        return String.format(
                "SELECT DISTINCT %s FROM %s WHERE activity = '%s' AND resource <> '%s'",
                caseIdColumnName,
                tableName,
                taskT,
                resourceR
        );
    }

    @Override
    public void processQueryParameters(Map<String, String> templateValues) {
        taskT = templateValues.get("task_t");
        resourceR = templateValues.get("resource_r");
    }
}
