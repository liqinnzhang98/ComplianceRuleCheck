package com.apromore.compliance_centre.eventlogs;

import com.apromore.compliance_centre.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EventlogControllerTest {

    @Mock
    private EventLogService eventLogService;

    @InjectMocks
    private EventLogController eventLogController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEventLogsById() {
        Long id = 1L;
        EventLogDto expectedEventLogDto = new EventLogDto();
        expectedEventLogDto.setId(id);
        expectedEventLogDto.setName("process");
        expectedEventLogDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        when(eventLogService.getEventLogById(id)).thenReturn(expectedEventLogDto);

        Response<EventLogDto> response = eventLogController.getEventLogsById(id);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedEventLogDto, response.getData());
        verify(eventLogService, times(1)).getEventLogById(id);
    }

    @Test
    void testUploadEventLogData() {
        Long id = 1L;
        EventLogDto expectedEventLogDto = new EventLogDto();
        expectedEventLogDto.setId(id);
        expectedEventLogDto.setName("process");
        expectedEventLogDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        String fileName = "test-event-log-file.txt";

        MockMultipartFile file = new MockMultipartFile("file", fileName,
                "text/plain", "content".getBytes());

        when(eventLogService.uploadEventLogData(id, file)).thenReturn(expectedEventLogDto);

        Response<EventLogDto> response = eventLogController.uploadEventLogData(id, file);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedEventLogDto, response.getData());
        verify(eventLogService, times(1)).uploadEventLogData(id, file);
    }

}
