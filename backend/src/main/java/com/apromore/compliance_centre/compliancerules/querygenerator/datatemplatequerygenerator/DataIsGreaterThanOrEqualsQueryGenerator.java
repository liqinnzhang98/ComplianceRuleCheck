package com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryOperatorEnum;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class DataIsGreaterThanOrEqualsQueryGenerator extends DataTemplateQueryGenerator {

    public DataIsGreaterThanOrEqualsQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(QueryOperatorEnum.LESS_THAN, eventLog, templateValues);
    }
}
