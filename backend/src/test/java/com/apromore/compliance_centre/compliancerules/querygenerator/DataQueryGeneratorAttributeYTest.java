package com.apromore.compliance_centre.compliancerules.querygenerator;

import com.apromore.compliance_centre.eventlogs.EventLogModel;
import com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator.DataEqualsQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator.DataIsGreaterThanOrEqualsQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator.DataIsGreaterThanQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator.DataIsLessThanOrEqualsQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator.DataIsLessThanQueryGenerator;
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

public class DataQueryGeneratorAttributeYTest {

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

    private DataEqualsQueryGenerator dataEqualsQueryGenerator;

    private DataIsGreaterThanOrEqualsQueryGenerator dataIsGreaterThanOrEqualsQueryGenerator;

    private DataIsGreaterThanQueryGenerator dataIsGreaterThanQueryGenerator;

    private DataIsLessThanOrEqualsQueryGenerator dataIsLessThanOrEqualsQueryGenerator;

    private DataIsLessThanQueryGenerator dataIsLessThanQueryGenerator;

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


        Map<String, String> templateValues = new HashMap<>();
        templateValues.put("attribute_x", "attributeX");
        templateValues.put("attribute_y", "attributeY");

        dataEqualsQueryGenerator = new DataEqualsQueryGenerator(eventLogModel, templateValues);
        dataIsGreaterThanOrEqualsQueryGenerator = new DataIsGreaterThanOrEqualsQueryGenerator(eventLogModel, templateValues);
        dataIsGreaterThanQueryGenerator = new DataIsGreaterThanQueryGenerator(eventLogModel, templateValues);
        dataIsLessThanOrEqualsQueryGenerator = new DataIsLessThanOrEqualsQueryGenerator(eventLogModel, templateValues);
        dataIsLessThanQueryGenerator = new DataIsLessThanQueryGenerator(eventLogModel, templateValues);
    }

    @Test
    public void testDataEqualsQueryGeneratorGetQuery() {

        String actualQuery = dataEqualsQueryGenerator.getQuery();
        String expectedQuery = "SELECT case_id FROM eventLogTable WHERE `attributeX` != `attributeY` GROUP BY case_id";

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testDataIsGreaterThanOrEqualsQueryGeneratorGetQuery() {

        String actualQuery = dataIsGreaterThanOrEqualsQueryGenerator.getQuery();
        String expectedQuery = "SELECT case_id FROM eventLogTable WHERE `attributeX` < `attributeY` GROUP BY case_id";

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testDataIsGreaterThanQueryGeneratorGetQuery() {

        String actualQuery = dataIsGreaterThanQueryGenerator.getQuery();
        String expectedQuery = "SELECT case_id FROM eventLogTable WHERE `attributeX` <= `attributeY` GROUP BY case_id";

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testDataIsLessThanOrEqualsQueryGeneratorGetQuery() {

        String actualQuery = dataIsLessThanOrEqualsQueryGenerator.getQuery();
        String expectedQuery = "SELECT case_id FROM eventLogTable WHERE `attributeX` > `attributeY` GROUP BY case_id";

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testDataIsLessThanQueryGeneratorGetQuery() {

        String actualQuery = dataIsLessThanQueryGenerator.getQuery();
        String expectedQuery = "SELECT case_id FROM eventLogTable WHERE `attributeX` >= `attributeY` GROUP BY case_id";

        assertEquals(expectedQuery, actualQuery);
    }
}