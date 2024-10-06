package com.apromore.compliance_centre.compliancerules.querygenerator;

import com.apromore.compliance_centre.eventlogs.EventLogModel;
import com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator.TimeAtLeastAfterQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator.TimeExactlyAfterQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator.TimeExactlyAtQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator.TimeWithinLeadsToQueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.timetemplatequerygenerator.TimeWithinTaskTimeQueryGenerator;
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

public class TimeQueryGeneratorTest {

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

    private TimeAtLeastAfterQueryGenerator timeAtLeastAfterQueryGenerator;

    private TimeExactlyAfterQueryGenerator timeExactlyAfterQueryGenerator;

    private TimeExactlyAtQueryGenerator timeExactlyAtQueryGenerator;

    private TimeWithinLeadsToQueryGenerator timeWithinLeadsToQueryGenerator;

    private TimeWithinTaskTimeQueryGenerator timeWithinTaskTimeQueryGenerator;

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
        templateValues.put("task_p", "TaskP");
        templateValues.put("task_q", "TaskQ");
        templateValues.put("duration_k", "1 DAY");
        templateValues.put("time_k", "1:00:00");

        timeAtLeastAfterQueryGenerator = new TimeAtLeastAfterQueryGenerator(eventLogModel, templateValues);
        timeExactlyAfterQueryGenerator = new TimeExactlyAfterQueryGenerator(eventLogModel, templateValues);
        timeExactlyAtQueryGenerator = new TimeExactlyAtQueryGenerator(eventLogModel, templateValues);
        timeWithinLeadsToQueryGenerator = new TimeWithinLeadsToQueryGenerator(eventLogModel, templateValues);
        timeWithinTaskTimeQueryGenerator = new TimeWithinTaskTimeQueryGenerator(eventLogModel, templateValues);
    }

    @Test
    public void testTimeAtLeastAfterQueryGeneratorGetQuery() {

        String actualQuery = timeAtLeastAfterQueryGenerator.getQuery();
        String expectedQuery = """
            SELECT DISTINCT a1.case_id
            FROM eventLogTable AS a1
            JOIN eventLogTable AS a2
            ON a1.case_id = a2.case_id
            AND a1.activity = 'TaskP'
            AND a2.activity = 'TaskQ'
            AND TIMESTAMPDIFF(DAY, a1.start_time, a2.start_time) < 1;
            """;

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testTimeExactlyAfterQueryGeneratorGetQuery() {

        String actualQuery = timeExactlyAfterQueryGenerator.getQuery();
        String expectedQuery = """
            SELECT DISTINCT a1.case_id
            FROM eventLogTable AS a1
            JOIN eventLogTable AS a2
            ON a1.case_id = a2.case_id
            AND a1.activity = 'TaskP'
            AND a2.activity = 'TaskQ'
            AND TIMESTAMPDIFF(DAY, a1.start_time, a2.start_time) != 1;
            """;

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testTimeExactlyAtQueryGeneratorGetQuery() {

        String actualQuery = timeExactlyAtQueryGenerator.getQuery();
        String expectedQuery = """
            SELECT DISTINCT a1.case_id
            FROM eventLogTable AS a1
            JOIN eventLogTable AS a2
            ON a1.case_id = a2.case_id
            AND a1.activity = 'TaskP'
            AND a2.activity = 'TaskQ'
            AND (TIMESTAMPDIFF(MICROSECOND, a1.complete_time, a2.start_time) <= 0 /* task P follows task Q */
            OR TIMESTAMPDIFF(DAY, a1.complete_time, a2.start_time) > 0 /* within a day */
            OR TIME(a2.start_time) != '1:00:00');
            """;

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testTimeWithinLeadsToQueryGeneratorGetQuery() {

        String actualQuery = timeWithinLeadsToQueryGenerator.getQuery();
        String expectedQuery = """
            SELECT DISTINCT a1.case_id
            FROM eventLogTable AS a1
            JOIN eventLogTable AS a2
            ON a1.case_id = a2.case_id
            AND a1.activity = 'TaskP'
            AND a2.activity = 'TaskQ'
            AND TIMESTAMPDIFF(DAY, a1.start_time, a2.start_time) >= 1;
            """;

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testTimeWithinTaskTimeQueryGeneratorGetQuery() {

        String actualQuery = timeWithinTaskTimeQueryGenerator.getQuery();
        String expectedQuery = """
            SELECT DISTINCT case_id
            FROM eventLogTable
            WHERE activity = 'TaskP'
            AND TIMESTAMPDIFF(DAY, start_time, complete_time) >= 1;
            """;

        assertEquals(expectedQuery, actualQuery);
    }
}