package com.apromore.compliance_centre.compliancerules.querygenerator.controlflowtemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryGenerator;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class ControlFlowExistsQueryGenerator extends QueryGenerator {

    String taskP;

    public ControlFlowExistsQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(eventLog, templateValues);
    }

    @Override
    public String getQuery() {
        return String.format(
                "SELECT %s FROM (SELECT %s, GROUP_CONCAT(%s) as a FROM %s GROUP BY %s) AS _ WHERE a NOT LIKE '%%%s%%'",
                caseIdColumnName,
                caseIdColumnName,
                activityColumnName,
                tableName,
                caseIdColumnName,
                taskP
        );
    }

    @Override
    public void processQueryParameters(Map<String, String> templateValues) {
        taskP = templateValues.get("task_p");
    }
}
