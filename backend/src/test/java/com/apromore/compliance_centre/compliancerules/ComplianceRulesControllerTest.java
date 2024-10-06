package com.apromore.compliance_centre.compliancerules;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.apromore.compliance_centre.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

public class ComplianceRulesControllerTest {

    @Mock
    private ComplianceRulesService complianceRulesService;

    @InjectMocks
    private ComplianceRulesController complianceRulesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveComplianceRules() {
        ComplianceRulesDto complianceRulesDto = new ComplianceRulesDto();
        ComplianceRulesDto expectedComplianceRulesDto = new ComplianceRulesDto();

        when(complianceRulesService.saveComplianceRules(complianceRulesDto)).thenReturn(expectedComplianceRulesDto);

        Response<ComplianceRulesDto> response = complianceRulesController.saveComplianceRules(complianceRulesDto);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedComplianceRulesDto, response.getData());
        verify(complianceRulesService, times(1)).saveComplianceRules(complianceRulesDto);
    }

    @Test
    void testGetComplianceRulesByProcess() {
        Long id = 1L;
        List<ComplianceRulesDto> expectedComplianceRules = new ArrayList<>();
        expectedComplianceRules.add(
            new ComplianceRulesDto().setControlId(1L).setId(1L).setProcessId(1L).setRules(null)
        );
        expectedComplianceRules.add(
            new ComplianceRulesDto().setControlId(2L).setId(2L).setProcessId(1L).setRules(null)
        );
        expectedComplianceRules.add(
            new ComplianceRulesDto().setControlId(3L).setId(3L).setProcessId(1L).setRules(null)
        );

        when(complianceRulesService.getComplianceRulesByProcess(id)).thenReturn(expectedComplianceRules);

        Response<List<ComplianceRulesDto>> response = complianceRulesController.getComplianceRulesByProcess(id);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        for (int i = 0; i < expectedComplianceRules.size(); i++) {
            assertEquals(expectedComplianceRules.get(i).getProcessId(), id);
        }
        assertEquals(expectedComplianceRules, response.getData());
        verify(complianceRulesService, times(1)).getComplianceRulesByProcess(id);
    }

    @Test
    void testGetComplianceRulesByControl() {
        Long id = 1L;
        List<ComplianceRulesDto> expectedComplianceRules = new ArrayList<>();
        expectedComplianceRules.add(
            new ComplianceRulesDto().setControlId(1L).setId(1L).setProcessId(1L).setRules(null)
        );
        expectedComplianceRules.add(
            new ComplianceRulesDto().setControlId(1L).setId(2L).setProcessId(2L).setRules(null)
        );
        expectedComplianceRules.add(
            new ComplianceRulesDto().setControlId(1L).setId(3L).setProcessId(3L).setRules(null)
        );

        when(complianceRulesService.getComplianceRulesByControl(id)).thenReturn(expectedComplianceRules);

        Response<List<ComplianceRulesDto>> response = complianceRulesController.getComplianceRulesByControl(id);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        for (int i = 0; i < expectedComplianceRules.size(); i++) {
            assertEquals(expectedComplianceRules.get(i).getControlId(), id);
        }
        assertEquals(expectedComplianceRules, response.getData());
        verify(complianceRulesService, times(1)).getComplianceRulesByControl(id);
    }
}
