package com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryOperatorEnum;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class DataIsLessThanQueryGenerator extends DataTemplateQueryGenerator {

    public DataIsLessThanQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(QueryOperatorEnum.GREATER_THAN_OR_EQUALS, eventLog, templateValues);
    }
}
