package com.apromore.compliance_centre.compliancerules.querygenerator;

import com.apromore.compliance_centre.eventlogs.EventLogModel;
import com.apromore.compliance_centre.compliancerules.querygenerator.resourcetemplatequerygenerator.ResourceSegregatedFromQueryGenerator;
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

public class ResourceSegregatedFromQueryGeneratorTest {
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

    private ResourceSegregatedFromQueryGenerator ResourceSegregatedFromQueryGenerator;

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

        ResourceSegregatedFromQueryGenerator = new ResourceSegregatedFromQueryGenerator(eventLogModel, new HashMap<>());
    }

    @Test
    public void testGetQuery() {
        Map<String, String> templateValues = new HashMap<>();
        templateValues.put("task_1", "Task1");
        templateValues.put("task_2", "Task2");

        ResourceSegregatedFromQueryGenerator.processQueryParameters(templateValues);

        String actualQuery = ResourceSegregatedFromQueryGenerator.getQuery();
        String expectedQuery =
            "SELECT DISTINCT t1.case_id FROM eventLogTable t1 JOIN eventLogTable t2 ON t1.case_id = t2.case_id WHERE (t1.resource = t2.resource OR t1.role = t2.role) AND t1.activity = 'Task1' AND t2.activity = 'Task2'";
        assertEquals(expectedQuery, actualQuery);
    
    }
}
