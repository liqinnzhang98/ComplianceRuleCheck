package com.apromore.compliance_centre.compliancecheck;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.apromore.compliance_centre.compliancecheck.ComplianceCheckReport.ReportBreaches;
import com.apromore.compliance_centre.compliancerules.ComplianceRulesDto;
import com.apromore.compliance_centre.compliancerules.ComplianceRulesItem;
import com.apromore.compliance_centre.process.ProcessDto;
import com.apromore.compliance_centre.process.ProcessRepository;
import com.apromore.compliance_centre.response.Response;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

public class ComplianceCheckControllerTest {
    
    @Mock
    private ComplianceCheckService complianceCheckService;

    @Mock
    private ProcessRepository processRepository;

    @InjectMocks
    private ComplianceCheckController complianceCheckController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
 
    @Test
    void testGetLatestReportForProcess(){
        Long processId = 1L;
        Long reportId = 2L;
        ComplianceCheckDto report1 = new ComplianceCheckDto().setId(1).setRunAt(new Timestamp(System.currentTimeMillis()));
        ComplianceCheckDto report2 = new ComplianceCheckDto().setId(2).setRunAt(new Timestamp(System.currentTimeMillis()+1000));
        ComplianceCheckDto latestReport = new ComplianceCheckDto().setId(3).setRunAt(new Timestamp(System.currentTimeMillis()+2000));
        List<ComplianceCheckDto> complianceChecks = new ArrayList<>();
        complianceChecks.add(report1);
        complianceChecks.add(report2);
        complianceChecks.add(latestReport);

        ComplianceCheckReport expectedCheckReport = new ComplianceCheckReport(0, 0, null, 0, null, 0, reportId, null);

        when(complianceCheckService.getAllComplianceChecksForProcess(processId)).thenReturn(complianceChecks);
        when(complianceCheckService.getReportResults(latestReport.getId())).thenReturn(expectedCheckReport);

        Response<ComplianceCheckDto> response = complianceCheckController.getLastestReportForProcess(processId);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(latestReport, response.getData());

        verify(complianceCheckService, times(1)).getAllComplianceChecksForProcess(processId);
        verify(complianceCheckService, times(1)).getReportResults(latestReport.getId());
    }

    @Test
    public void testGetLastestReportForProcess_NotFound() {
        long processId = 1L;
    
        when(complianceCheckService.getAllComplianceChecksForProcess(processId))
                .thenReturn(Collections.emptyList());
    
        Response<ComplianceCheckDto> response = complianceCheckController.getLastestReportForProcess(processId);
    
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("Report not found", response.getErrorDetails().get(0).getDescription()); 
    }

    @Test
    void testGetAllReportsForProcess() {

        Long id = 1L;
        ProcessDto processDto1 = new ProcessDto().setId(id).setName("Test1");
        ProcessDto processDto2 = new ProcessDto().setId(id).setName("Test2");
        ProcessDto processDto3 = new ProcessDto().setId(id).setName("Test3");
        List<ComplianceCheckDto> expectedComplianceChecks = new ArrayList<>();
        expectedComplianceChecks.add(new ComplianceCheckDto().setProcess(processDto1));
        expectedComplianceChecks.add(new ComplianceCheckDto().setProcess(processDto2));
        expectedComplianceChecks.add(new ComplianceCheckDto().setProcess(processDto3));

        when(complianceCheckService.getAllComplianceChecksForProcess(id)).thenReturn(expectedComplianceChecks);

        Response<List<ComplianceCheckDto>> response = complianceCheckController.getAllReportsForProcess(id);
        for (int i = 0; i < expectedComplianceChecks.size(); i++) {
            assertEquals(expectedComplianceChecks.get(i).getProcess().getId(), id);
        }
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedComplianceChecks, response.getData());
        verify(complianceCheckService, times(1)).getAllComplianceChecksForProcess(id);
    }

    @Test
    void testGetAllReportsForProcess_NotFound() {
        long processId = 1L;

        when(complianceCheckService.getAllComplianceChecksForProcess(processId)).thenReturn(Collections.emptyList());

        Response<List<ComplianceCheckDto>> response = complianceCheckController.getAllReportsForProcess(processId);

        assertEquals(HttpStatus.OK.value(), response.getStatus());  
        assertTrue(response.getData().isEmpty());  
        verify(complianceCheckService, times(1)).getAllComplianceChecksForProcess(processId);
    }

    @Test
    public void testGetReport() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        ComplianceCheckDto dto1 = new ComplianceCheckDto()
            .setId(1L)
            .setRunAt(currentTime)
            .setStatus(ComplianceCheckStatus.COMPLETED)
            .setProcess(new ProcessDto());
        
        List<ReportBreaches> breachesMock = new ArrayList<>();
        // Map<Long, List<String>> breachesMock = new HashMap<>(); 
        ComplianceCheckReport mockReport = new ComplianceCheckReport(
            10, 5, "CASE123", 3, "CASE456", 1, 2.5, breachesMock
        );
    
        when(complianceCheckService.getComplianceCheckById(1L)).thenReturn(dto1);
        when(complianceCheckService.getReportResults(1L)).thenReturn(mockReport); 
    
        Response<ComplianceCheckDto> response = complianceCheckController.getReport(1L, 1L);
    
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        if (response.getData() instanceof ComplianceCheckDto) {
            ComplianceCheckDto returnedDto = (ComplianceCheckDto) response.getData();
            assertEquals(1L, returnedDto.getId());
        } else {
            fail("Unexpected data type in response.");
        }

        assertNotNull(response.getData().getReport());
        assertEquals(10, response.getData().getReport().getTotalCases());
        ComplianceCheckReport returnedReport = response.getData().getReport();
        assertNotNull(returnedReport);
        assertEquals(10, returnedReport.getTotalCases());
        assertEquals(5, returnedReport.getTotalBreachedCases());

        verify(complianceCheckService, times(1)).getComplianceCheckById(1L);
        verify(complianceCheckService, times(1)).getReportResults(1L);
    }

    @Test
    void testGetReport_NotFound_catchException() {
        long mockProcessId = 1L;
        long mockReportId = 1L;
        when(complianceCheckService.getComplianceCheckById(mockReportId)).thenReturn(null);
        

        Exception exception = assertThrows(NullPointerException.class, () -> {
            complianceCheckController.getReport(mockProcessId, mockReportId);
        });

        assertTrue(exception.getMessage().contains("Cannot invoke"));
    }

    @Test
    void testGetAllReports() {

        List<ComplianceCheckDto> expectedComplianceChecks = new ArrayList<>();
        expectedComplianceChecks.add(new ComplianceCheckDto());
        expectedComplianceChecks.add(new ComplianceCheckDto());
        expectedComplianceChecks.add(new ComplianceCheckDto());

        when(complianceCheckService.getAllComplianceChecks()).thenReturn(expectedComplianceChecks);

        Response<List<ComplianceCheckDto>> response = complianceCheckController.getAllReports();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedComplianceChecks, response.getData());
        verify(complianceCheckService, times(1)).getAllComplianceChecks();
    }

    @Test
    void testGetAllReports_NotFound() {

        when(complianceCheckService.getAllComplianceChecks()).thenReturn(Collections.emptyList());

        Response<List<ComplianceCheckDto>> response = complianceCheckController.getAllReports();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getData().isEmpty());
        verify(complianceCheckService, times(1)).getAllComplianceChecks();
    }

    @Test
    void testCheckProcessCompliance(){
        Long id = 1L;

        HashMap<String, String> myValue = new HashMap<>();
        myValue.put("attribute_x","requested_amount");       
        myValue.put("value_y","500000");

        ComplianceRulesItem complianceRulesItem = new ComplianceRulesItem().setTemplateId(id).setValues(myValue);
        List<ComplianceRulesItem> rule = new ArrayList<>();
        rule.add(complianceRulesItem);
        List<List<ComplianceRulesItem>> rules = new ArrayList<>();
        rules.add(rule);
        
        List<ComplianceRulesDto> complianceRulesDtoList = new ArrayList<>();
        ComplianceRulesDto complianceRulesDto = new ComplianceRulesDto().setId(id).setControlId(id).setProcessId(id).setRules(null);
        complianceRulesDtoList.add(complianceRulesDto);

        ProcessDto processDto1 = new ProcessDto().setId(id).setName("Test1");
        ComplianceCheckDto expectedComplianceChecks = new ComplianceCheckDto().setProcess(processDto1);
        
        when(complianceCheckService.checkProcessCompliance(id,complianceRulesDtoList)).thenReturn(expectedComplianceChecks);

        Response<ComplianceCheckDto> response = complianceCheckController.checkProcessCompliance(id,complianceRulesDtoList);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedComplianceChecks, response.getData());

        verify(complianceCheckService, times(1)).checkProcessCompliance(id,complianceRulesDtoList);
    }

    @Test
    void testCheckProcessCompliance_NotFound() {
        long mockProcessId = 1L;
        List<ComplianceRulesDto> complianceRulesDtoList = new ArrayList<>();
        when(complianceCheckService.checkProcessCompliance(mockProcessId, complianceRulesDtoList)).thenReturn(null);

        Response<ComplianceCheckDto> response = complianceCheckController.checkProcessCompliance(mockProcessId, complianceRulesDtoList);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertNull(response.getData());
        verify(complianceCheckService, times(0)).checkProcessCompliance(mockProcessId, complianceRulesDtoList);
    }
}
