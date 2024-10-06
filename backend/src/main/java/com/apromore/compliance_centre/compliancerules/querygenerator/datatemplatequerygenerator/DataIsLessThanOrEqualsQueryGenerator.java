package com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryOperatorEnum;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class DataIsLessThanOrEqualsQueryGenerator extends DataTemplateQueryGenerator {

    public DataIsLessThanOrEqualsQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(QueryOperatorEnum.GREATER_THAN, eventLog, templateValues);
    }
}
