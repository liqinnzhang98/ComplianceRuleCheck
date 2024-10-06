package com.apromore.compliance_centre.process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.apromore.compliance_centre.eventlogs.EventLogModel;
import com.apromore.compliance_centre.eventlogs.EventLogRepository;
import com.apromore.compliance_centre.eventlogs.EventLogService;
import com.apromore.compliance_centre.eventlogs.EventLogServiceImpl;
import com.apromore.compliance_centre.process.attributes.AttributeDto;
import com.apromore.compliance_centre.process.attributes.AttributeModel;
import com.apromore.compliance_centre.process.attributes.AttributeType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
public class ProcessServiceImplTest {

    private final ProcessRepository processRepository = mock(ProcessRepository.class);
    private final EventLogRepository eventLogRepository = mock(EventLogRepository.class);
    private final ModelMapper modelMapper = new ModelMapper();
    private final ProcessService processService = new ProcessServiceImpl(processRepository, modelMapper);
    private final EventLogService eventLogService = new EventLogServiceImpl(eventLogRepository, processRepository, modelMapper);

    private List<AttributeDto> attributeDtos = new ArrayList<>();
    private List<AttributeModel> attributeModels = new ArrayList<>();
    private List<EventLogModel> eventLogModels = new ArrayList<>();
    Long processId;

    @BeforeEach
    public void setup() {
        processId = 1L;
        
        AttributeDto attributeDto1 = new AttributeDto().setId(1L).setName("AttDto1").setDisplayName("DisDto1");
        AttributeDto attributeDto2 = new AttributeDto().setId(2L).setName("AttDto2").setDisplayName("DisDto2");
        attributeDtos.add(attributeDto1);
        attributeDtos.add(attributeDto2);

        AttributeModel attributeModel1 = new AttributeModel().setId(1L).setName("AttMod1").setDisplayName("DisMod1");
        AttributeModel attributeModel2 = new AttributeModel().setId(2L).setName("AttMod2").setDisplayName("DisMod2");
        attributeModels.add(attributeModel1);
        attributeModels.add(attributeModel2);

        EventLogModel eventLogModel1 = new EventLogModel().setId(1L).setName("Eventlog1").setCreatedAt(new Timestamp(System.currentTimeMillis()));
        EventLogModel eventLogModel2 = new EventLogModel().setId(2L).setName("Eventlog2").setCreatedAt(new Timestamp(System.currentTimeMillis()));
        eventLogModels.add(eventLogModel1);
        eventLogModels.add(eventLogModel2);

    }

    @Test
    public void testGetAllProcesses() {
        List<ProcessModel> processModels = new ArrayList<>();
        processModels.add(new ProcessModel().setName("Process 1"));
        processModels.add(new ProcessModel().setName("Process 2"));
        when(processRepository.findAll()).thenReturn(processModels);

        List<ProcessDto> processDtos = processService.getAllProcesses();

        assertEquals(processModels.size(), processDtos.size());
        for (int i = 0; i < processDtos.size(); i++) {
            ProcessModel model = processModels.get(i);
            ProcessDto dto = processDtos.get(i);
            assertEquals(model.getName(), dto.getName());
        }
    }

    @Test
    public void testGetProcessByIdWithExistingId() {
        ProcessModel processModel = new ProcessModel();
        processModel.setId(1L);
        processModel.setName("Test Process");
        when(processRepository.findById(1L)).thenReturn(Optional.of(processModel));

        ProcessDto processDto = processService.getProcessById(1L);

        assertNotNull(processDto);
        assertEquals(processDto.getId(), processModel.getId());
        assertEquals(processDto.getName(), processModel.getName());
    }

    @Test
    public void testGetProcessByIdWithNonExistingId() {
        when(processRepository.findById(2L)).thenReturn(Optional.empty());
        ProcessDto processDto = processService.getProcessById(2L);
        assertNull(processDto);
    }

    @Test
    public void testCreateNewProcess() {
        ProcessDto inputDto = new ProcessDto();
        inputDto.setName("Test Process");

        ProcessModel savedModel = new ProcessModel();
        savedModel.setName(inputDto.getName());
        savedModel.setId(1L);

        Mockito.when(processRepository.save(Mockito.any(ProcessModel.class))).thenReturn(savedModel);
        ProcessDto resultDto = processService.createNewProcess(inputDto);

        assertNotNull(resultDto);
        assertEquals(savedModel.getId(), resultDto.getId());
        assertEquals(inputDto.getName(), resultDto.getName());
    }

    @Test
    public void testGetAttributes() {
        ProcessModel processModel = new ProcessModel();
        processModel.setAttributes(attributeModels);

        when(processRepository.findById(processId)).thenReturn(Optional.of(processModel));

        List<AttributeDto> result = processService.getAttributes(processId);

        assertEquals(2, result.size());
        assertEquals(result.get(0).getName(), result.get(0).getName());
        assertEquals(result.get(1).getName(), result.get(1).getName());
    }

    @Test
    public void testGetAttributesWithNull() {

        when(processRepository.findById(processId)).thenReturn(Optional.empty());
        List<AttributeDto> result = processService.getAttributes(processId);

        assertEquals(0, result.size());
    }

    @Test
    public void testSaveAttributesValid() {
        ProcessModel process = Mockito.mock(ProcessModel.class);
        when(processRepository.findById(processId)).thenReturn(Optional.of(process));

        when(process.getAttributes()).thenReturn(new ArrayList<AttributeModel>());
        when(process.getEventLogs()).thenReturn(new ArrayList<EventLogModel>());

        List<AttributeDto> result = processService.saveAttributes(processId, attributeDtos);

        assertEquals(2, result.size());
    }

    @Test
    public void testSaveAttributesWithExistingAttributes() {
        ProcessModel process = Mockito.mock(ProcessModel.class);
        when(processRepository.findById(processId)).thenReturn(Optional.of(process));
        when(process.getAttributes()).thenReturn(attributeModels);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> processService.saveAttributes(processId, attributeDtos));

        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "Process schema is already defined";
        assert(expectedStatus.equals(exception.getStatusCode()));
        assert(expectedMessage.equals(exception.getReason()));
    }

    @Test
    public void testSaveAttributesWithExistingEventlogs() {
        ProcessModel process = Mockito.mock(ProcessModel.class);
        when(processRepository.findById(processId)).thenReturn(Optional.of(process));

        when(process.getAttributes()).thenReturn(new ArrayList<AttributeModel>());
        when(process.getEventLogs()).thenReturn(eventLogModels);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> processService.saveAttributes(processId, attributeDtos));

        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "Process already has event logs";
        assert(expectedStatus.equals(exception.getStatusCode()));
        assert(expectedMessage.equals(exception.getReason()));
    }

    @Test
    public void testSaveAttributesWithDuplicateAttributes() {
        ProcessModel process = Mockito.mock(ProcessModel.class);
        when(processRepository.findById(processId)).thenReturn(Optional.of(process));

        String attributeName = "Attribute Name";
        AttributeDto attributeDto1 = new AttributeDto().setId(1L).setName(attributeName).setDisplayName("DisDto1");
        AttributeDto attributeDto2 = new AttributeDto().setId(2L).setName(attributeName).setDisplayName("DisDto2");
        attributeDtos.add(attributeDto1);
        attributeDtos.add(attributeDto2);

        when(process.getAttributes()).thenReturn(new ArrayList<AttributeModel>());
        when(process.getEventLogs()).thenReturn(new ArrayList<EventLogModel>());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> processService.saveAttributes(processId, attributeDtos));

        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "Duplicate attribute name: " + attributeName;
        assert(expectedStatus.equals(exception.getStatusCode()));
        assert(expectedMessage.equals(exception.getReason()));
    }

    @Test
    public void testGetMetadataProcessNotPresent() {
        when(processRepository.findById(processId)).thenReturn(Optional.empty());
        Map<String, Set<String>> result = processService.getMetadata(processId);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMetadataProcessPresent() {
        ProcessModel process = new ProcessModel();
        process.setName("process");
        List<AttributeModel> attributes = new ArrayList<>();
        attributes.add(new AttributeModel().setName("Attribute1").setType(AttributeType.ACTIVITY));
        attributes.add(new AttributeModel().setName("Attribute2").setType(AttributeType.ATTRIBUTE));
        attributes.add(new AttributeModel().setName("Attribute3").setType(AttributeType.RESOURCE));
        attributes.add(new AttributeModel().setName("Attribute4").setType(AttributeType.ROLE));
        process.setAttributes(attributes);
        process.setEventLogs(new ArrayList<>());

        when(processRepository.findById(processId)).thenReturn(Optional.of(process));

        Map<String, Set<String>> result = processService.getMetadata(processId);

        assertFalse(result.isEmpty());
        assertEquals(4, result.size());
        assertTrue(result.containsKey(AttributeType.ATTRIBUTE.toString()));
        assertTrue(result.containsKey(AttributeType.ACTIVITY.toString()));
        assertTrue(result.containsKey(AttributeType.RESOURCE.toString()));
        assertTrue(result.containsKey(AttributeType.ROLE.toString()));
    }

    @Test
    public void testGetCasesProcessNotPresent() {
        when(processRepository.findById(processId)).thenReturn(Optional.empty());
        List<String> result = processService.getAllCases(processId);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCases() {
        // TODO
    }
}