package com.apromore.compliance_centre.riskObligationRegister;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.apromore.compliance_centre.response.Response;
import com.apromore.compliance_centre.riskobligationregister.CSVHelper;
import com.apromore.compliance_centre.riskobligationregister.CSVService;
import com.apromore.compliance_centre.riskobligationregister.ControlDto;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationController;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationDto;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationService;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

public class RiskObligationControllerTest {

    @Mock
    private RiskObligationService riskObligationService;

    @Mock
    private CSVService csvService;


    @InjectMocks
    private RiskObligationController riskObligationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRiskObligations() {

        List<RiskObligationDto> expectedRiskObligations = new ArrayList<>();
        expectedRiskObligations.add(new RiskObligationDto());
        expectedRiskObligations.add(new RiskObligationDto());
        expectedRiskObligations.add(new RiskObligationDto());

        when(riskObligationService.getAllRiskObligations()).thenReturn(expectedRiskObligations);

        Response<List<RiskObligationDto>> response = riskObligationController.getAllRiskObligations();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedRiskObligations, response.getData());
        verify(riskObligationService, times(1)).getAllRiskObligations();
    }

    @Test
    void testCreateNewRiskObligations() {

        RiskObligationDto riskObligationDto = new RiskObligationDto();
        RiskObligationDto expectedRiskObligationDto = new RiskObligationDto();

        when(riskObligationService.createNewRiskObligation(riskObligationDto)).thenReturn(expectedRiskObligationDto);

        Response<RiskObligationDto> response = riskObligationController.createNewRiskObligation(riskObligationDto);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedRiskObligationDto, response.getData());
        verify(riskObligationService, times(1)).createNewRiskObligation(riskObligationDto);
    }

    @Test
    void testGetRiskObligationById() {

        Long id = 1L;
        RiskObligationDto expectedRiskObligations = new RiskObligationDto();

        when(riskObligationService.getRiskObligationById(id)).thenReturn(expectedRiskObligations);

        Response<RiskObligationDto> response = riskObligationController.getRiskObligationById(id);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedRiskObligations, response.getData());
        verify(riskObligationService, times(1)).getRiskObligationById(id);
    }

    @Test
    void testGetRiskObligationByIdWithNull() {

        Long id = 1L;
        when(riskObligationService.getRiskObligationById(id)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            riskObligationController.getRiskObligationById(id);
        });

       assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
       String expectedMessage = "entity not found";
       assertEquals(expectedMessage, exception.getReason());
    }

    @Test
    void testGetAllControls() {

        List<ControlDto> expectedControls = new ArrayList<>();
        expectedControls.add(new ControlDto());
        expectedControls.add(new ControlDto());
        expectedControls.add(new ControlDto());

        when(riskObligationService.getAllControls()).thenReturn(expectedControls);

        Response<List<ControlDto>> response = riskObligationController.getAllControls();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedControls, response.getData());
        verify(riskObligationService, times(1)).getAllControls();
    }

    @Test
    void testCreateNewControl() {

        ControlDto controlDto = new ControlDto();
        ControlDto expectedControlDto = new ControlDto();

        when(riskObligationService.createNewControl(controlDto)).thenReturn(expectedControlDto);

        Response<ControlDto> response = riskObligationController.createNewControl(controlDto);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedControlDto, response.getData());
        verify(riskObligationService, times(1)).createNewControl(controlDto);
    }

    @Test
    void testGetControlById() {

        Long id = 1L;
        ControlDto expectedControlDto = new ControlDto();

        when(riskObligationService.getControlById(id)).thenReturn(expectedControlDto);

        Response<ControlDto> response = riskObligationController.getControlById(id);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedControlDto, response.getData());
        verify(riskObligationService, times(1)).getControlById(id);
    }

    @Test
    void testGetControlByIdWithNull() {

        Long id = 1L;
        when(riskObligationService.getControlById(id)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            riskObligationController.getControlById(id);
        });

       assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
       String expectedMessage = "entity not found";
       assertEquals(expectedMessage, exception.getReason());
    }

    @Test
    void testUploadFileWithValidCSV() {

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getContentType()).thenReturn("text/csv");

        Response<String> response = riskObligationController.uploadFile(mockFile);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(csvService, times(1)).save(mockFile);
    }

    @Test
    void testUploadFileWithInvalidCSV() {

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getContentType()).thenReturn("not a csv");

        Response<String> response = riskObligationController.uploadFile(mockFile);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        String expectedMessage = "Please upload a csv file!";
        assertEquals(expectedMessage, response.getData());
    }

    @Test
    void testUploadFileWithException() {

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getContentType()).thenReturn("text/csv");
        doThrow(new RuntimeException("")).when(csvService).save(mockFile);

        Response<String> response = riskObligationController.uploadFile(mockFile);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        String expectedMessage = "Could not upload the file: " + mockFile.getOriginalFilename() + "!";
        assertEquals(expectedMessage, response.getData());
    }
    
}
