package com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryOperatorEnum;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class DataIsGreaterThanQueryGenerator extends DataTemplateQueryGenerator {

    public DataIsGreaterThanQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(QueryOperatorEnum.LESS_THAN_OR_EQUALS, eventLog, templateValues);
    }
}
