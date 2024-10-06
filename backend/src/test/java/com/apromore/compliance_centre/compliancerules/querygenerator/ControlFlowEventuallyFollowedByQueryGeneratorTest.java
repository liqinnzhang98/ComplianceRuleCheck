package com.apromore.compliance_centre.compliancerules.querygenerator;

import com.apromore.compliance_centre.eventlogs.EventLogModel;
import com.apromore.compliance_centre.compliancerules.querygenerator.controlflowtemplatequerygenerator.ControlFlowEventuallyFollowedByQueryGenerator;
import com.apromore.compliance_centre.process.attributes.AttributeModel;
import com.apromore.compliance_centre.process.attributes.AttributeType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ControlFlowEventuallyFollowedByQueryGeneratorTest {

    @Mock
    private EventLogModel eventLogModel;

    @Mock
    private AttributeModel caseIdAttributeModel;

    @Mock
    private AttributeModel activityAttributeModel;

    @Mock
    private AttributeModel startTimeAttributeModel;

    @Mock
    private AttributeModel completeTimeAttributeModel;

    @Mock
    private AttributeModel roleAttributeModel;

    @Mock
    private AttributeModel resourceAttributeModel;

    private ControlFlowEventuallyFollowedByQueryGenerator controlFlowEventuallyFollowedByQueryGenerator; // Notice this change

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(eventLogModel.getEventLogTableName()).thenReturn("eventLogTable");
        when(eventLogModel.getAttributeByType(AttributeType.CASE_ID)).thenReturn(caseIdAttributeModel);
        when(eventLogModel.getAttributeByType(AttributeType.ACTIVITY)).thenReturn(activityAttributeModel);
        when(eventLogModel.getAttributeByType(AttributeType.START_TIMESTAMP)).thenReturn(startTimeAttributeModel);
        when(eventLogModel.getAttributeByType(AttributeType.COMPLETE_TIMESTAMP)).thenReturn(completeTimeAttributeModel);
        when(eventLogModel.getAttributeByType(AttributeType.ROLE)).thenReturn(roleAttributeModel);
        when(eventLogModel.getAttributeByType(AttributeType.RESOURCE)).thenReturn(resourceAttributeModel);

        when(caseIdAttributeModel.getDatabaseColumnName()).thenReturn("case_id");
        when(activityAttributeModel.getDatabaseColumnName()).thenReturn("activity");
        when(startTimeAttributeModel.getDatabaseColumnName()).thenReturn("start_time");
        when(completeTimeAttributeModel.getDatabaseColumnName()).thenReturn("complete_time");
        when(roleAttributeModel.getDatabaseColumnName()).thenReturn("role");
        when(resourceAttributeModel.getDatabaseColumnName()).thenReturn("resource");

        controlFlowEventuallyFollowedByQueryGenerator = new ControlFlowEventuallyFollowedByQueryGenerator(eventLogModel, new HashMap<>()); // And this line
    }

    @Test
    public void testGetQuery() {
        Map<String, String> templateValues = new HashMap<>();
        templateValues.put("task_p", "TaskP");
        templateValues.put("task_q", "TaskQ");

        controlFlowEventuallyFollowedByQueryGenerator.processQueryParameters(templateValues);

        String actualQuery = controlFlowEventuallyFollowedByQueryGenerator.getQuery();
        String expectedQuery =
                "SELECT DISTINCT case_id FROM eventLogTable WHERE case_id NOT IN (SELECT DISTINCT a1.case_id FROM eventLogTable AS a1 JOIN eventLogTable AS a2 ON a1.case_id = a2.case_id WHERE a1.activity = 'TaskP' AND a2.activity = 'TaskQ' AND a1.start_time <= a2.start_time)";

        assertEquals(expectedQuery, actualQuery);
    }
}