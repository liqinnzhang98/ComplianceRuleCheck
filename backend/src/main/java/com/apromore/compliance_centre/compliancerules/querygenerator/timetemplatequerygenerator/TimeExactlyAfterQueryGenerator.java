package com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryOperatorEnum;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class TimeExactlyAfterQueryGenerator extends TimeTemplateQueryGenerator {

    public TimeExactlyAfterQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(QueryOperatorEnum.NOT_EQUALS, eventLog, templateValues);
    }

}
