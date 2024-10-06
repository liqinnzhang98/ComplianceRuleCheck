package com.apromore.compliance_centre.eventlogs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.apromore.compliance_centre.process.ProcessModel;
import com.apromore.compliance_centre.process.ProcessRepository;
import com.apromore.compliance_centre.process.attributes.AttributeModel;
import com.apromore.compliance_centre.process.attributes.AttributeType;

public class EventlogModelTest {

    private EventLogModel eventLog;
    private EventLogServiceImpl eventLogService;
    private EventLogRepository eventLogRepository;
    private ProcessRepository processRepository;

    @BeforeEach
    public void setUp() {
        eventLog = new EventLogModel();
        eventLogRepository = mock(EventLogRepository.class);
        processRepository = mock(ProcessRepository.class);
        eventLogService = new EventLogServiceImpl(eventLogRepository, processRepository, new ModelMapper());
    }

    @Test
    public void testGetAttributes() {
        EventLogModel eventLog = new EventLogModel();

        ProcessModel process = mock(ProcessModel.class);
        when(process.getAttributes()).thenReturn(Arrays.asList(new AttributeModel(), new AttributeModel()));
        eventLog.setProcess(process);

        List<AttributeModel> attributes = eventLog.getAttributes();

        assertFalse(attributes.isEmpty());
        assertEquals(2, attributes.size());
    }

    @Test
    public void testGetAttributeByType() {
        EventLogModel eventLog = new EventLogModel();

        AttributeModel attr1 = new AttributeModel();
        attr1.setType(AttributeType.ATTRIBUTE);

        AttributeModel attr2 = new AttributeModel();
        attr2.setType(AttributeType.ACTIVITY);

        AttributeModel attr3 = new AttributeModel();
        attr3.setType(AttributeType.RESOURCE);

        ProcessModel process = mock(ProcessModel.class);
        when(process.getAttributes()).thenReturn(Arrays.asList(attr1, attr2, attr3));
        eventLog.setProcess(process);

        AttributeModel foundAttribute = eventLog.getAttributeByType(AttributeType.ACTIVITY);

        assertNotNull(foundAttribute);
        assertEquals(AttributeType.ACTIVITY, foundAttribute.getType());
    }

    @Test
    public void testGetEventLogTableName() {
        EventLogModel eventLog = new EventLogModel();
        eventLog.setName("TestEventLogName");
        eventLog.setId((long) 1);

        String tableName = eventLog.getEventLogTableName();

        assertNotEquals("`log_TestEventLogName_1`", tableName);
    }

    @Test
        public void testFromDto() {
            EventLogDto eventLogDto = new EventLogDto();
            eventLogDto.setName("TestDtoName");
            eventLogDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            EventLogModel eventLog = EventLogModel.fromDto(eventLogDto);

            assertNotEquals("TestDtoName", eventLog.getName());
            assertEquals(eventLogDto.getCreatedAt(), eventLog.getCreatedAt());
        }
}
