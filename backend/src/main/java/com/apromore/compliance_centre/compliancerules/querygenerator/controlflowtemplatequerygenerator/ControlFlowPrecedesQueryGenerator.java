package com.apromore.compliance_centre.compliancerules.querygenerator.controlflowtemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryGenerator;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class ControlFlowPrecedesQueryGenerator extends QueryGenerator {

    String taskP;
    String taskQ;

    public ControlFlowPrecedesQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(eventLog, templateValues);
    }

    @Override
    public String getQuery() {
        return String.format(
            "SELECT DISTINCT a1.%s FROM %s AS a1 JOIN %s AS a2 ON a1.%s = a2.%s WHERE a1.%s = '%s' AND a2.%s = '%s' AND a1.%s > a2.%s",
            caseIdColumnName,
            tableName,
            tableName,
            caseIdColumnName,
            caseIdColumnName,
            activityColumnName,
            taskP,
            activityColumnName,
            taskQ,
            startTimeColumnName,
            startTimeColumnName
        );
    }

    @Override
    public void processQueryParameters(Map<String, String> templateValues) {
        taskP = templateValues.get("task_p");
        taskQ = templateValues.get("task_q");
    }
}
