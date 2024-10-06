package com.apromore.compliance_centre.compliancerules.querygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.controlflowtemplatequerygenerator.ControlFlowDirectlyFollowedByQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.controlflowtemplatequerygenerator.ControlFlowEventuallyFollowedByQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.controlflowtemplatequerygenerator.ControlFlowExistsQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.controlflowtemplatequerygenerator.ControlFlowPrecedesQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator.DataEqualsQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator.DataIsGreaterThanOrEqualsQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator.DataIsGreaterThanQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator.DataIsLessThanOrEqualsQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator.DataIsLessThanQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.resourcetemplatequerygenerator.ResourceBoundedWithQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.resourcetemplatequerygenerator.ResourcePerformedByQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.resourcetemplatequerygenerator.ResourceRoleBoundedWithQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.resourcetemplatequerygenerator.ResourceSegregatedFromQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.resourcetemplatequerygenerator.ResourceUserSegregatedFromQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator.TimeAtLeastAfterQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator.TimeExactlyAfterQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator.TimeExactlyAtQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator.TimeWithinLeadsToQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator.TimeWithinTaskTimeQueryGenerator;
import com.apromore.compliance_centre.compliancerules.templates.TemplateModel;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class QueryGeneratorProvider {

    public QueryGenerationStrategy getQueryGenerator(
        TemplateModel template,
        EventLogModel eventLog,
        Map<String, String> templateValues
    ) {
        switch (template.getType()) {
            case CONTROL_FLOW_PRECEDES:
                return new ControlFlowPrecedesQueryGenerator(eventLog, templateValues);
            case CONTROL_FLOW_DIRECTLY_FOLLOWED_BY:
                return new ControlFlowDirectlyFollowedByQueryGenerator(eventLog, templateValues);
            case CONTROL_FLOW_EVENTUALLY_FOLLOWED_BY:
                return new ControlFlowEventuallyFollowedByQueryGenerator(eventLog, templateValues);
            case CONTROL_FLOW_EXISTS:
                return new ControlFlowExistsQueryGenerator(eventLog, templateValues);
            case RESOURCE_PERFORMED_BY:
                return new ResourcePerformedByQueryGenerator(eventLog, templateValues);
            case RESOURCE_SEGREGATED_FROM:
                return new ResourceSegregatedFromQueryGenerator(eventLog, templateValues);
            case RESOURCE_USER_SEGREGATED_FROM:
                return new ResourceUserSegregatedFromQueryGenerator(eventLog, templateValues);
            case RESOURCE_BOUNDED_WITH:
                return new ResourceBoundedWithQueryGenerator(eventLog, templateValues);
            case RESOURCE_ROLE_BOUNDED_WITH:
                return new ResourceRoleBoundedWithQueryGenerator(eventLog, templateValues);
            case TIME_WITHIN_LEADS_TO:
                return new TimeWithinLeadsToQueryGenerator(eventLog, templateValues);
            case TIME_WITHIN_TASK_TIME:
                return new TimeWithinTaskTimeQueryGenerator(eventLog, templateValues);
            case TIME_AT_LEAST_AFTER:
                return new TimeAtLeastAfterQueryGenerator(eventLog, templateValues);
            case TIME_EXACTLY_AT:
                return new TimeExactlyAtQueryGenerator(eventLog, templateValues);
            case TIME_EXACTLY_AFTER:
                return new TimeExactlyAfterQueryGenerator(eventLog, templateValues);
            case DATA_NOT_EQUAL:
                return new DataEqualsQueryGenerator(eventLog, templateValues);
            case DATA_EQUALS:
                return new DataEqualsQueryGenerator(eventLog, templateValues);
            case DATA_IS_GREATER_THAN:
                return new DataIsGreaterThanQueryGenerator(eventLog, templateValues);
            case DATA_IS_GREATER_THAN_OR_EQUALS:
                return new DataIsGreaterThanOrEqualsQueryGenerator(eventLog, templateValues);
            case DATA_IS_LESS_THAN:
                return new DataIsLessThanQueryGenerator(eventLog, templateValues);
            case DATA_IS_LESS_THAN_OR_EQUALS:
                return new DataIsLessThanOrEqualsQueryGenerator(eventLog, templateValues);
            default:
                throw new IllegalArgumentException("Unknown template category");
        }
    }
}
