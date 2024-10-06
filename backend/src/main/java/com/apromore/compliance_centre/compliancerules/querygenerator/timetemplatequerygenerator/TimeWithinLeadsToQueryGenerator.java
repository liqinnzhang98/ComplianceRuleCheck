package com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryOperatorEnum;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class TimeWithinLeadsToQueryGenerator extends TimeTemplateQueryGenerator {

    public TimeWithinLeadsToQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(QueryOperatorEnum.GREATER_THAN_OR_EQUALS, eventLog, templateValues);
    }

}
