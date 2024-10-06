package com.apromore.compliance_centre.compliancerules;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.apromore.compliance_centre.compliancerules.templates.TemplateCategory;
import com.apromore.compliance_centre.compliancerules.templates.TemplateDto;
import com.apromore.compliance_centre.compliancerules.templates.TemplateModel;
import com.apromore.compliance_centre.compliancerules.templates.TemplateServiceImpl;
import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldDto;
import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldModel;
import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldType;
import com.apromore.compliance_centre.process.ProcessModel;
import com.apromore.compliance_centre.process.ProcessRepository;
import com.apromore.compliance_centre.process.attributes.AttributeType;
import com.apromore.compliance_centre.response.Response;
import com.apromore.compliance_centre.riskobligationregister.CSVHelper;
import com.apromore.compliance_centre.riskobligationregister.CSVService;
import com.apromore.compliance_centre.riskobligationregister.ControlDto;
import com.apromore.compliance_centre.riskobligationregister.ControlModel;
import com.apromore.compliance_centre.riskobligationregister.ControlRepository;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationController;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationDto;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationModel;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationRepository;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationService;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

public class ComplianceRulesServiceImplTest {

    private final ComplianceRulesRepository rulesRepository = mock(ComplianceRulesRepository.class);
    private final ProcessRepository processRepository = mock(ProcessRepository.class);
    private final ControlRepository controlRepository = mock(ControlRepository.class);
    private final ModelMapper modelMapper = new ModelMapper();
    private final ComplianceRulesServiceImpl complianceRulesService = new ComplianceRulesServiceImpl(
            rulesRepository,
            processRepository,
            controlRepository,
            modelMapper);
    
    // @Mock
    // private ProcessRepository processRepository;

    // @Mock
    // private ControlRepository controlRepository;

    // @Mock
    // private ComplianceRulesRepository complianceRulesRepository;

    // @Mock
    // private ModelMapper modelMapper;

    // @InjectMocks
    // private ComplianceRulesService complianceRulesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveComplianceRules() {
        ComplianceRulesDto complianceRulesDto = new ComplianceRulesDto()
            .setProcessId(2L)
            .setControlId(3L)
            .setRules(null);
        
        ProcessModel process = new ProcessModel().setId(2L);
        ControlModel control = new ControlModel().setId(3L);
        ComplianceRulesModel complianceRulesModel = new ComplianceRulesModel();
        complianceRulesModel.setProcess(process);
        complianceRulesModel.setControl(control);
        ComplianceRulesDto expectedComplianceRulesDto = new ComplianceRulesDto();
        expectedComplianceRulesDto.setProcessId(2L);
        expectedComplianceRulesDto.setControlId(3L);
        expectedComplianceRulesDto.setRules(null);

        when(processRepository.findById(2L)).thenReturn(Optional.of(process));
        when(controlRepository.findById(3L)).thenReturn(Optional.of(control));
        when(rulesRepository.save(any(ComplianceRulesModel.class))).thenReturn(complianceRulesModel);

        ComplianceRulesDto result = complianceRulesService.saveComplianceRules(complianceRulesDto);

        assertEquals(expectedComplianceRulesDto, result);
        verify(rulesRepository).save(any(ComplianceRulesModel.class));
    }

    @Test
    public void testGetComplianceRulesByProcess() {
        Long processId = 1L;

        List<ComplianceRulesModel> complianceRulesModels = new ArrayList<>();
        ProcessModel process1 = new ProcessModel().setId(processId);
        ComplianceRulesModel complianceRulesModel1 = new ComplianceRulesModel();
        complianceRulesModel1.setProcess(process1);
        complianceRulesModels.add(complianceRulesModel1);
        ProcessModel process2 = new ProcessModel().setId(processId);
        ComplianceRulesModel complianceRulesModel2 = new ComplianceRulesModel();
        complianceRulesModel2.setProcess(process2);
        complianceRulesModels.add(complianceRulesModel2);

        List<ComplianceRulesDto> expectedDtos = new ArrayList<>();
        ComplianceRulesDto expectedDto1 = new ComplianceRulesDto();
        expectedDto1.setId(processId);
        expectedDto1.setProcessId(processId);
        expectedDtos.add(expectedDto1);
        ComplianceRulesDto expectedDto2 = new ComplianceRulesDto();
        expectedDto2.setId(2L);
        expectedDto2.setProcessId(processId);
        expectedDtos.add(expectedDto2);

        when(rulesRepository.findByProcessId(processId)).thenReturn(complianceRulesModels);

        List<ComplianceRulesDto> result = complianceRulesService.getComplianceRulesByProcess(processId);

        assertEquals(expectedDtos.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            ComplianceRulesDto expecteDto = expectedDtos.get(i);
            ComplianceRulesDto resultDto = result.get(i);
            assertEquals(expecteDto.getProcessId(), resultDto.getProcessId());
        }
    }

    @Test
    public void testGetComplianceRulesByControl() {
        Long controlId = 1L;

        List<ComplianceRulesModel> complianceRulesModels = new ArrayList<>();
        ControlModel control1 = new ControlModel().setId(controlId);
        ComplianceRulesModel complianceRulesModel1 = new ComplianceRulesModel();
        complianceRulesModel1.setControl(control1);
        complianceRulesModels.add(complianceRulesModel1);
        ControlModel control2 = new ControlModel().setId(controlId);
        ComplianceRulesModel complianceRulesModel2 = new ComplianceRulesModel();
        complianceRulesModel2.setControl(control2);
        complianceRulesModels.add(complianceRulesModel2);

        List<ComplianceRulesDto> expectedDtos = new ArrayList<>();
        ComplianceRulesDto expectedDto1 = new ComplianceRulesDto();
        expectedDto1.setId(controlId);
        expectedDto1.setControlId(controlId);
        expectedDtos.add(expectedDto1);
        ComplianceRulesDto expectedDto2 = new ComplianceRulesDto();
        expectedDto2.setId(2L);
        expectedDto2.setControlId(controlId);
        expectedDtos.add(expectedDto2);

        when(rulesRepository.findByControlId(controlId)).thenReturn(complianceRulesModels);

        List<ComplianceRulesDto> result = complianceRulesService.getComplianceRulesByControl(controlId);

        assertEquals(expectedDtos.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            ComplianceRulesDto expecteDto = expectedDtos.get(i);
            ComplianceRulesDto resultDto = result.get(i);
            assertEquals(expecteDto.getControlId(), resultDto.getControlId());
        }
    }

    
}
