package com.apromore.compliance_centre.process;

import com.apromore.compliance_centre.eventlogs.EventLogDto;
import com.apromore.compliance_centre.eventlogs.EventLogService;
import com.apromore.compliance_centre.process.attributes.AttributeDto;
import com.apromore.compliance_centre.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProcessControllerTest {

    @Mock
    private ProcessService processService;

    @Mock
    private EventLogService eventLogService;

    @InjectMocks
    private ProcessController processController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProcesses() {
        List<ProcessDto> expectedProcesses = new ArrayList<>();
        expectedProcesses.add(new ProcessDto().setId(1).setName("process1"));
        expectedProcesses.add(new ProcessDto().setId(2).setName("process2"));

        when(processService.getAllProcesses()).thenReturn(expectedProcesses);

        Response<List<ProcessDto>> response = processController.getAllProcesses();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedProcesses, response.getData());
        verify(processService, times(1)).getAllProcesses();
    }

    @Test
    void testGetEventLogsForProcess() {
        Long processId = 1L;
        List<EventLogDto> expectedEventlgos = new ArrayList<>();
        expectedEventlgos.add(new EventLogDto().setId(1).setName("eventlog1").setCreatedAt(new Timestamp(System.currentTimeMillis())));
        expectedEventlgos.add(new EventLogDto().setId(2).setName("eventlog2").setCreatedAt(new Timestamp(System.currentTimeMillis())));

        when(eventLogService.findEventLogsByProcessId(processId)).thenReturn(expectedEventlgos);

        Response<List<EventLogDto>> response = processController.getEventLogsForProcess(processId);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedEventlgos, response.getData());
        verify(eventLogService, times(1)).findEventLogsByProcessId(processId);
    }

    @Test
    void testCreateNewProcess() {
        ProcessDto processDto = new ProcessDto();
        ProcessDto expectedProcess = new ProcessDto();

        when(processService.createNewProcess(processDto)).thenReturn(expectedProcess);

        Response<ProcessDto> response = processController.createNewProcess(processDto);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedProcess, response.getData());
        verify(processService, times(1)).createNewProcess(processDto);
    }

    @Test
    void testGetProcessById() {
        long id = 1L;
        ProcessDto expectedProcess = new ProcessDto().setId(1).setName("process");

        when(processService.getProcessById(id)).thenReturn(expectedProcess);
        Response<ProcessDto> response = processController.getProcessById(id);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedProcess, response.getData());
        verify(processService, times(1)).getProcessById(id);
    }

    @Test
    void testGetProcessByIdWithNull() {
        long id = 1L;
        when(processService.getProcessById(id)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> processController.getProcessById(id));

        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "entity not found";
        assert(expectedStatus.equals(exception.getStatusCode()));
        assert(expectedMessage.equals(exception.getReason()));
    }

    @Test
    void testGetProcessSchema() {
        long processId = 1L;
        List<AttributeDto> expectedSchema = new ArrayList<>();
        expectedSchema.add(new AttributeDto().setId(1L).setName("att1").setDisplayName("dis1"));
        expectedSchema.add(new AttributeDto().setId(2L).setName("att2").setDisplayName("dis2"));

        when(processService.getAttributes(processId)).thenReturn(expectedSchema);
        Response<List<AttributeDto>> response = processController.getProcessSchema(processId);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedSchema, response.getData());
        verify(processService, times(1)).getAttributes(processId);
    }

    @Test
    void testCreateProcessSchema() {
        long processId = 1L;
        List<AttributeDto> schema = new ArrayList<>();
        schema.add(new AttributeDto().setId(1L).setName("att1").setDisplayName("dis1"));
        schema.add(new AttributeDto().setId(2L).setName("att2").setDisplayName("dis2"));


        when(processService.saveAttributes(processId, schema)).thenReturn(schema);

        Response<List<AttributeDto>> response = processController.createProcessSchema(processId, schema);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(schema, response.getData());
        verify(processService, times(1)).saveAttributes(processId, schema);
    }

    @Test
    void testGetEventLog() {
        long processId = 1L;

        Map<String, Set<String>> metadata = new HashMap<>();
        Set<String> attributes = new HashSet<>();
        attributes.add("Attribute1");
        attributes.add("Attribute2");
        metadata.put("Process", attributes);

        when(processService.getMetadata(processId)).thenReturn(metadata);

        Response<Map<String, Set<String>>> response = processController.getEventlog(processId);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(metadata, response.getData());
        verify(processService, times(1)).getMetadata(processId);
    }
}
