package com.apromore.compliance_centre.eventlogs;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.apromore.compliance_centre.process.ProcessModel;
import com.apromore.compliance_centre.process.ProcessRepository;
import com.apromore.compliance_centre.process.ProcessService;
import com.apromore.compliance_centre.process.ProcessServiceImpl;
import com.apromore.compliance_centre.process.attributes.AttributeModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventlogServiceImplTest {

   private final EventLogRepository eventLogRepository = mock(EventLogRepository.class);
   private final ProcessRepository processRepository = mock(ProcessRepository.class);
   private final ModelMapper modelMapper = new ModelMapper();
   private final EventLogService eventLogService = new EventLogServiceImpl(eventLogRepository, processRepository, modelMapper);

   @Test
   public void testGetAllEventLogs() {
       List<EventLogModel> eventLogModels = new ArrayList<>();
       eventLogModels.add(new EventLogModel().setName("EventLog 1"));
       eventLogModels.add(new EventLogModel().setName("EventLog 2"));
       when(eventLogRepository.findAll()).thenReturn(eventLogModels);

       List<EventLogDto> eventLogDtos = eventLogService.getAllEventLogs();

       assertEquals(eventLogModels.size(), eventLogDtos.size());
       for (int i = 0; i < eventLogDtos.size(); i++) {
           EventLogModel model = eventLogModels.get(i);
           EventLogDto dto = eventLogDtos.get(i);
           assertEquals(model.getName(), dto.getName());
       }
   }

   @Test
   public void testGetEventLogById() {
       EventLogModel eventLogModel = new EventLogModel();
       eventLogModel.setId(1L);
       eventLogModel.setName("Test EventLog");
       when(eventLogRepository.findById(1L)).thenReturn(Optional.of(eventLogModel));

       EventLogDto eventLogDto = eventLogService.getEventLogById(1L);

       assertNotNull(eventLogDto);
       assertEquals(eventLogDto.getId(), eventLogModel.getId());
       assertEquals(eventLogDto.getName(), eventLogModel.getName());
   }

   @Test
   public void testUploadEventLogData() {
        Long processId = 1L;
        String fileName = "test-file.csv";
        MockMultipartFile file = new MockMultipartFile("file", fileName, "text/csv", "CSV content".getBytes());

        ProcessModel process = Mockito.mock(ProcessModel.class);
        when(processRepository.findById(processId)).thenReturn(java.util.Optional.of(process));

        List<AttributeModel> mockAttributes = new ArrayList<>();
        mockAttributes.add(new AttributeModel());
        mockAttributes.add(new AttributeModel());
        when(process.getAttributes()).thenReturn(mockAttributes);

        EventLogModel eventLog = new EventLogModel();
        eventLog.setId(1L);
        when(eventLogRepository.save(any(EventLogModel.class))).thenReturn(eventLog);
        doNothing().when(eventLogRepository).createEventLogTable(any(EventLogModel.class), anyList());
        doNothing().when(eventLogRepository).saveEventLogData(any(EventLogModel.class), anyList(), anyList());

        EventLogDto result = eventLogService.uploadEventLogData(processId, file);

        assertNotNull(result);
    }

    @Test
    public void testUploadEventLogDataWithEmptyAttributes() {
         Long processId = 1L;
         String fileName = "test-file.csv";
         MockMultipartFile file = new MockMultipartFile("file", fileName, "text/csv", "CSV content".getBytes());
 
         ProcessModel process = Mockito.mock(ProcessModel.class);
         when(processRepository.findById(processId)).thenReturn(java.util.Optional.of(process));
 
         List<AttributeModel> mockAttributes = new ArrayList<>();
         when(process.getAttributes()).thenReturn(mockAttributes);

         ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> eventLogService.uploadEventLogData(processId, file));

        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "No attributes found for process with id " + processId;
        assert(expectedStatus.equals(exception.getStatusCode()));
        assert(expectedMessage.equals(exception.getReason()));
     }

     @Test
     public void testFindEventLogsByProcessId() {
         Long processId = 1L;
         EventLogModel eventLogModel1 = new EventLogModel();
         eventLogModel1.setId(1L);
         eventLogModel1.setName("EventLog1");
         EventLogModel eventLogModel2 = new EventLogModel();
         eventLogModel2.setId(2L);
         eventLogModel2.setName("EventLog2");

         List<EventLogModel> eventLogModels = new ArrayList<>();
         eventLogModels.add(eventLogModel1);
         eventLogModels.add(eventLogModel2);

         when(eventLogRepository.findByProcessId(processId)).thenReturn(eventLogModels);
  
         List<EventLogDto> eventLogDtos = eventLogService.findEventLogsByProcessId(processId);
  
         assertNotNull(eventLogDtos);
         assertEquals(eventLogDtos.get(0).getId(), eventLogModel1.getId());
         assertEquals(eventLogDtos.get(0).getName(), eventLogModel1.getName());
         assertEquals(eventLogDtos.get(1).getId(), eventLogModel2.getId());
         assertEquals(eventLogDtos.get(1).getName(), eventLogModel2.getName());
     }

     @Test
     public void testGetAttributeValues() {
         EventLogModel eventLogModel = new EventLogModel();
         eventLogModel.setId(1L);
         eventLogModel.setName("EventLog");
         AttributeModel attributeModel = new AttributeModel();
         attributeModel.setId(1L);
         attributeModel.setName("loangoal");
         attributeModel.setDisplayName("Loan Goal");

         List<String> attributes = new ArrayList<>();
         attributes.add(attributeModel.getName());
         attributes.add(attributeModel.getDisplayName());
         when(eventLogRepository.getEventLogDataAttributeValues(eventLogModel, attributeModel)).thenReturn(attributes);
  
         List<String> result = eventLogService.getAttributeValues(eventLogModel, attributeModel);
  
         assertEquals(result.get(0), attributeModel.getName());
         assertEquals(result.get(1), attributeModel.getDisplayName());
     }
    
}