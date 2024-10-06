package com.apromore.compliance_centre.compliancerules.querygenerator;

import com.apromore.compliance_centre.eventlogs.EventLogModel;
import com.apromore.compliance_centre.process.attributes.AttributeType;
import java.util.Map;

public abstract class QueryGenerator implements QueryGenerationStrategy {

    protected String tableName;

    protected String caseIdColumnName;

    protected String activityColumnName;

    protected String startTimeColumnName;

    protected String completeTimeColumnName;

    protected String roleColumnName;

    protected String resourceColumnName;

    public QueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        this.tableName = eventLog.getEventLogTableName();
        this.caseIdColumnName = eventLog.getAttributeByType(AttributeType.CASE_ID).getDatabaseColumnName();
        this.activityColumnName = eventLog.getAttributeByType(AttributeType.ACTIVITY).getDatabaseColumnName();
        this.startTimeColumnName = eventLog.getAttributeByType(AttributeType.START_TIMESTAMP).getDatabaseColumnName();
        this.completeTimeColumnName =
            eventLog.getAttributeByType(AttributeType.COMPLETE_TIMESTAMP).getDatabaseColumnName();
        this.roleColumnName = eventLog.getAttributeByType(AttributeType.ROLE).getDatabaseColumnName();
        this.resourceColumnName = eventLog.getAttributeByType(AttributeType.RESOURCE).getDatabaseColumnName();

        this.processQueryParameters(templateValues);
    }
}
