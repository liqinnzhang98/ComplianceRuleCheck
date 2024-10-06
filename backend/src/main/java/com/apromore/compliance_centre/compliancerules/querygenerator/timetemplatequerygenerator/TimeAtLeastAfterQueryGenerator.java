package com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryOperatorEnum;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class TimeAtLeastAfterQueryGenerator extends TimeTemplateQueryGenerator {

    public TimeAtLeastAfterQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(QueryOperatorEnum.LESS_THAN, eventLog, templateValues);
    }

}
