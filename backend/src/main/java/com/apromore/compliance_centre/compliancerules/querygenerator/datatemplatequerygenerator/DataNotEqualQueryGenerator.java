package com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryOperatorEnum;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class DataNotEqualQueryGenerator extends DataTemplateQueryGenerator {

    public DataNotEqualQueryGenerator(EventLogModel eventLog, Map<String, String> templateValues) {
        super(QueryOperatorEnum.EQUALS, eventLog, templateValues);

    }
}
