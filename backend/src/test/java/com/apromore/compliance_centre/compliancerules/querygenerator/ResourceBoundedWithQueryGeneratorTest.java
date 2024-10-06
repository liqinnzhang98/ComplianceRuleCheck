package com.apromore.compliance_centre.compliancerules.querygenerator;

import com.apromore.compliance_centre.eventlogs.EventLogModel;
import com.apromore.compliance_centre.compliancerules.querygenerator.resourcetemplatequerygenerator.ResourceBoundedWithQueryGenerator;
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

public class ResourceBoundedWithQueryGeneratorTest {
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

    private ResourceBoundedWithQueryGenerator resourceBoundedWithQueryGenerator;

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

        resourceBoundedWithQueryGenerator = new ResourceBoundedWithQueryGenerator(eventLogModel, new HashMap<>());
    }

    @Test
    public void testGetQuery() {
        Map<String, String> templateValues = new HashMap<>();
        templateValues.put("task_1", "Task1");
        templateValues.put("task_2", "Task2");

        resourceBoundedWithQueryGenerator.processQueryParameters(templateValues);

        String actualQuery = resourceBoundedWithQueryGenerator.getQuery();
        String expectedQuery =
                    "SELECT DISTINCT a.case_id FROM eventLogTable a INNER JOIN eventLogTable b ON a.case_id = b.case_id WHERE a.activity = 'Task1' AND b.activity = 'Task2' AND a.resource <> b.resource";
        assertEquals(expectedQuery, actualQuery);
    
    }
}
