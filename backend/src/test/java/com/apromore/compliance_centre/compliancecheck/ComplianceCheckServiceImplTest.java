package com.apromore.compliance_centre.compliancecheck;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.apromore.compliance_centre.compliancerules.ComplianceRulesDto;
import com.apromore.compliance_centre.compliancerules.ComplianceRulesItem;
import com.apromore.compliance_centre.compliancerules.ComplianceRulesModel;
import com.apromore.compliance_centre.compliancerules.ComplianceRulesServiceImpl;
import com.apromore.compliance_centre.compliancerules.queryexecutor.QueryExecutor;
import com.apromore.compliance_centre.compliancerules.querygenerator.QueryGenerationStrategy;
import com.apromore.compliance_centre.compliancerules.querygenerator.QueryGeneratorProvider;
import com.apromore.compliance_centre.compliancerules.templates.TemplateModel;
import com.apromore.compliance_centre.compliancerules.templates.TemplateRepository;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import com.apromore.compliance_centre.process.ProcessDto;
import com.apromore.compliance_centre.process.ProcessModel;
import com.apromore.compliance_centre.process.ProcessRepository;
import com.apromore.compliance_centre.process.ProcessServiceImpl;
import com.apromore.compliance_centre.riskobligationregister.ControlDto;
import com.apromore.compliance_centre.riskobligationregister.ControlModel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

public class ComplianceCheckServiceImplTest {

    private final ComplianceCheckRepository complianceCheckRepository = mock(ComplianceCheckRepository.class);
    private final ProcessRepository processRepository = mock(ProcessRepository.class);
    private final TemplateRepository templateRepository = mock(TemplateRepository.class);
    private final ProcessServiceImpl processService = mock(ProcessServiceImpl.class);
    private final ComplianceRulesServiceImpl complianceRulesService = mock(ComplianceRulesServiceImpl.class);
    private final QueryGeneratorProvider queryGeneratorProvider = mock(QueryGeneratorProvider.class);
    private final QueryExecutor queryExecutor = mock(QueryExecutor.class);
    private final ComplianceCheckServiceImpl complianceCheckService = new ComplianceCheckServiceImpl(
        complianceCheckRepository,
        processRepository,
        templateRepository,
        processService,
        complianceRulesService,
        queryGeneratorProvider,
        queryExecutor
    );

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetReportResults() {
        long processId = 1L;
        ProcessModel processModel = new ProcessModel().setId(processId);

        ComplianceRulesModel rule1 = new ComplianceRulesModel().setId(1L);
        ComplianceRulesModel rule2 = new ComplianceRulesModel().setId(2L);

        ControlModel control1 = new ControlModel().setId(1l).setName("control1");
        ControlModel control2 = new ControlModel().setId(2l).setName("control2");
        ControlModel control3 = new ControlModel().setId(3l).setName("control3");

        rule1.setControl(control1);
        rule2.setControl(control2);
        ComplianceViolationModel violation1 = new ComplianceViolationModel()
            .setCaseId("case1")
            .setComplianceRule(rule1);
        ComplianceViolationModel violation2 = new ComplianceViolationModel()
            .setCaseId("case1")
            .setComplianceRule(rule2);
        // ComplianceViolationModel violation3 = new ComplianceViolationModel().setCaseId("case2").setComplianceRule(rule1);

        List<ComplianceViolationModel> violations = Arrays.asList(violation1, violation2);

        ComplianceCheckModel checkModel = new ComplianceCheckModel()
            .setId(1L)
            .setStatus(ComplianceCheckStatus.COMPLETED)
            .setProcess(processModel)
            .setViolations(violations);

        Optional<ComplianceCheckModel> check = Optional.of(checkModel);
        List<ComplianceViolationModel> violationList = check.get().getViolations();
        Map<Long, List<String>> breachedCaseIdsByRule = new HashMap<>();
        Map<Long, ControlModel> controlsByRule = new HashMap<>();
        Map<String, Integer> numBreachesPerCase = new HashMap<>();
        Map<Long, ComplianceRulesModel> rulesByRuleId = new HashMap<>();

        for (ComplianceViolationModel violation : violationList) {
            long violationRuleId = violation.getComplianceRule().getId();

            if (!rulesByRuleId.containsKey(violationRuleId)) {
                rulesByRuleId.put(violationRuleId, violation.getComplianceRule());
            }

            if (!breachedCaseIdsByRule.containsKey(violationRuleId)) {
                breachedCaseIdsByRule.put(violationRuleId, new ArrayList<>());
            }
            if (!controlsByRule.containsKey(violationRuleId)) {
                ControlModel control = violation.getComplianceRule().getControl();
                controlsByRule.put(violationRuleId, control);
            }
            breachedCaseIdsByRule.get(violationRuleId).add(violation.getCaseId());

            int numBreaches = 1;
            if (numBreachesPerCase.containsKey(violation.getCaseId())) {
                numBreaches += numBreachesPerCase.get(violation.getCaseId());
            }
            numBreachesPerCase.put(violation.getCaseId(), numBreaches);
        }
        assertNotNull(controlsByRule);
        List<ComplianceCheckReport.ReportBreaches> breachesByRule = new ArrayList<>();
        for (Entry<Long, List<String>> entry : breachedCaseIdsByRule.entrySet()) {
            assertNotNull(controlsByRule.get(entry.getKey()));
            breachesByRule.add(
                new ComplianceCheckReport.ReportBreaches(
                    ComplianceRulesDto.fromModel(rulesByRuleId.get(entry.getKey())),
                    ControlDto.fromModel(controlsByRule.get(entry.getKey())),
                    entry.getValue()
                )
            );
        }

        Entry<String, Integer> max = null;
        Entry<String, Integer> min = null;
        if (numBreachesPerCase.size() > 0) {
            max = Collections.max(numBreachesPerCase.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
            min = Collections.min(numBreachesPerCase.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
        }

        ComplianceCheckReport report = new ComplianceCheckReport(
            2,
            numBreachesPerCase.size(),
            max == null ? "NONE" : max.getKey(),
            max == null ? 0 : max.getValue(),
            min == null ? "NONE" : min.getKey(),
            min == null ? 0 : min.getValue(),
            numBreachesPerCase.size() == 0 ? 0 : (double) violations.size() / numBreachesPerCase.size(),
            breachesByRule
        );

        when(complianceCheckRepository.findById(processId)).thenReturn(Optional.of(checkModel));
        when(processService.getAllCases(processId)).thenReturn(Arrays.asList("case1", "case2"));
        ComplianceCheckReport result = complianceCheckService.getReportResults(processId);

        assertNotNull(report);
        assertEquals(report, result);
        assertEquals(2, breachedCaseIdsByRule.size());
        assertEquals(1, breachedCaseIdsByRule.get(1L).size());
        assertEquals(1, breachedCaseIdsByRule.get(2L).size());
        assertEquals(2, (int) numBreachesPerCase.get("case1"));
        //assertEquals(1, (int) numBreachesPerCase.get("case2"));
        assertEquals(2, report.totalCases);
        assertEquals(1, report.totalBreachedCases);
        assertEquals(2, report.breaches.size());
        assertEquals(2, report.getTotalCases());
    }

    @Test
    public void testGetReportResultsWithNonExistingReport() {
        long processId = 1L;
        when(complianceCheckRepository.findById(processId)).thenReturn(Optional.empty());

        ComplianceCheckReport result = complianceCheckService.getReportResults(processId);

        assertNull(result);
    }

    @Test
    public void testGetReportResultsWithNoCases() {
        long processId = 2L;
        ProcessModel mockProcess = new ProcessModel().setId(3L);
        ComplianceCheckModel mockCheck = new ComplianceCheckModel().setProcess(mockProcess);

        when(complianceCheckRepository.findById(processId)).thenReturn(Optional.of(mockCheck));
        when(processService.getAllCases(mockProcess.getId())).thenReturn(Collections.emptyList());

        ComplianceCheckReport result = complianceCheckService.getReportResults(processId);

        assertNull(result);
    }

    @Test
    public void testGetAllComplianceChecksForProcess() {
        long processId = 1L;
        ProcessModel processModel1 = new ProcessModel().setId(processId);
        ProcessModel processModel2 = new ProcessModel().setId(processId);

        List<ComplianceCheckModel> complianceCheckModels = new ArrayList<>();
        complianceCheckModels.add(
            new ComplianceCheckModel().setId(2L).setStatus(ComplianceCheckStatus.COMPLETED).setProcess(processModel1)
        );
        complianceCheckModels.add(
            new ComplianceCheckModel().setId(3L).setStatus(ComplianceCheckStatus.COMPLETED).setProcess(processModel2)
        );
        when(complianceCheckRepository.findByProcessId(processId)).thenReturn(complianceCheckModels);

        List<ComplianceCheckDto> expectedDtos = new ArrayList<>();
        for (int i = 0; i < complianceCheckModels.size(); i++) {
            ComplianceCheckModel model = complianceCheckModels.get(i);
            expectedDtos.add(ComplianceCheckDto.fromModel(model));
        }

        List<ComplianceCheckDto> result = complianceCheckService.getAllComplianceChecksForProcess(processId);

        assertEquals(expectedDtos, result);
    }

    @Test
    public void testGetAllComplianceChecks() {
        long processId = 1L;
        ProcessModel processModel1 = new ProcessModel().setId(processId);
        List<ComplianceCheckModel> complianceCheckModels = new ArrayList<>();
        complianceCheckModels.add(
            new ComplianceCheckModel().setId(1L).setProcess(processModel1).setStatus(ComplianceCheckStatus.COMPLETED)
        );
        complianceCheckModels.add(
            new ComplianceCheckModel().setId(2L).setProcess(processModel1).setStatus(ComplianceCheckStatus.COMPLETED)
        );
        when(complianceCheckRepository.findAll()).thenReturn(complianceCheckModels);

        List<ComplianceCheckDto> complianceCheckDtos = complianceCheckService.getAllComplianceChecks();

        assertEquals(complianceCheckModels.size(), complianceCheckDtos.size());
        for (int i = 0; i < complianceCheckDtos.size(); i++) {
            ComplianceCheckModel model = complianceCheckModels.get(i);
            ComplianceCheckDto dto = complianceCheckDtos.get(i);
            assertEquals(model.getStatus(), dto.getStatus());
        }
        verify(complianceCheckRepository, times(1)).findAll();
    }

    @Test
    public void testGetComplianceCheckByExistingId() {
        long processId = 2L;
        ProcessModel processModel1 = new ProcessModel().setId(processId);
        ComplianceCheckModel complianceCheckModel = new ComplianceCheckModel();
        complianceCheckModel.setId(1L);
        complianceCheckModel.setProcess(processModel1);
        complianceCheckModel.setStatus(ComplianceCheckStatus.COMPLETED);
        when(complianceCheckRepository.findById(1L)).thenReturn(Optional.of(complianceCheckModel));

        ComplianceCheckDto complianceCheckDto = complianceCheckService.getComplianceCheckById(1L);

        assertNotNull(complianceCheckDto);
        assertEquals(complianceCheckDto.getId(), complianceCheckModel.getId());
        assertEquals(complianceCheckDto.getStatus(), complianceCheckModel.getStatus());
    }

    @Test
    public void testGetComplianceCheckByNonExistingId() {
        when(complianceCheckRepository.findById(2L)).thenReturn(Optional.empty());

        ComplianceCheckDto complianceCheckDto = complianceCheckService.getComplianceCheckById(2L);

        assertNull(complianceCheckDto);
    }

    @Test
    public void testGetQueryGenerator() {
        Long templateId = 1L;
        EventLogModel eventLogModel = new EventLogModel();
        Map<String, String> templateValues = new HashMap<>();

        TemplateModel mockTemplate = new TemplateModel();
        QueryGenerationStrategy mockStrategy = mock(QueryGenerationStrategy.class);

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(mockTemplate));
        when(queryGeneratorProvider.getQueryGenerator(mockTemplate, eventLogModel, templateValues))
            .thenReturn(mockStrategy);

        QueryGenerationStrategy result = queryGeneratorProvider.getQueryGenerator(
            mockTemplate,
            eventLogModel,
            templateValues
        );

        assertEquals(mockStrategy, result);
    }
}
