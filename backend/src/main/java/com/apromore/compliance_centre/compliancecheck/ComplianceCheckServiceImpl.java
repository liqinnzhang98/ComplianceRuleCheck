package com.apromore.compliance_centre.compliancecheck;

import com.apromore.compliance_centre.compliancerules.ComplianceRulesDto;
import com.apromore.compliance_centre.compliancerules.ComplianceRulesItem;
import com.apromore.compliance_centre.compliancerules.ComplianceRulesModel;
import com.apromore.compliance_centre.compliancerules.ComplianceRulesService;
import com.apromore.compliance_centre.compliancerules.queryexecutor.QueryExecutor;
import com.apromore.compliance_centre.compliancerules.querygenerator.QueryGenerationStrategy;
import com.apromore.compliance_centre.compliancerules.querygenerator.QueryGeneratorProvider;
import com.apromore.compliance_centre.compliancerules.templates.TemplateModel;
import com.apromore.compliance_centre.compliancerules.templates.TemplateRepository;
import com.apromore.compliance_centre.eventlogs.EventLogDataRepository;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import com.apromore.compliance_centre.process.ProcessModel;
import com.apromore.compliance_centre.process.ProcessRepository;
import com.apromore.compliance_centre.process.ProcessService;
import com.apromore.compliance_centre.process.attributes.AttributeType;
import com.apromore.compliance_centre.riskobligationregister.ControlDto;
import com.apromore.compliance_centre.riskobligationregister.ControlModel;
import com.apromore.compliance_centre.utils.Utils;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Service;

@Service
public class ComplianceCheckServiceImpl implements ComplianceCheckService {

    @Autowired
    private ComplianceCheckRepository complianceCheckRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private ProcessService processService;

    @Autowired
    private ComplianceRulesService complianceRulesService;

    @Autowired
    private QueryGeneratorProvider queryGeneratorProvider;

    @Autowired
    private QueryExecutor queryExecutor;

    public ComplianceCheckServiceImpl(
        ComplianceCheckRepository complianceCheckRepository,
        ProcessRepository processRepository,
        TemplateRepository templateRepository,
        ProcessService processService,
        ComplianceRulesService complianceRulesService,
        QueryGeneratorProvider queryGeneratorProvider,
        QueryExecutor queryExecutor
    ) {
        this.complianceCheckRepository = complianceCheckRepository;
        this.processRepository = processRepository;
        this.templateRepository = templateRepository;
        this.processService = processService;
        this.complianceRulesService = complianceRulesService;
        this.queryGeneratorProvider = queryGeneratorProvider;
        this.queryExecutor = queryExecutor;
    }

    @Override
    public ComplianceCheckReport getReportResults(long reportId) {
        // fetch compliance check (return null if doesn't exist)
        Optional<ComplianceCheckModel> check = complianceCheckRepository.findById(reportId);
        if (!check.isPresent()) return null;

        // fetch number of cases (return null if no cases)
        int totalCases = processService.getAllCases(check.get().getProcess().getId()).size();
        if (totalCases == 0) return null;

        List<ComplianceViolationModel> violations = check.get().getViolations();
        Map<Long, List<String>> breachedCaseIdsByRule = new HashMap<>();
        Map<Long, ControlModel> controlsByRule = new HashMap<>();
        Map<String, Integer> numBreachesPerCase = new HashMap<>();
        Map<Long, ComplianceRulesModel> rulesMap = new HashMap<>();

        // generate list of breachs by rule
        for (ComplianceViolationModel violation : violations) {
            long violationRuleId = violation.getComplianceRule().getId();

            // add rule to rules map
            if (!rulesMap.containsKey(violationRuleId)) {
                rulesMap.put(violationRuleId, violation.getComplianceRule());
            }

            // add control to controls by rule list
            if (!controlsByRule.containsKey(violationRuleId)) {
                controlsByRule.put(violationRuleId, violation.getComplianceRule().getControl());
            }

            // add case id to rule's violation list
            if (!breachedCaseIdsByRule.containsKey(violationRuleId)) {
                breachedCaseIdsByRule.put(violationRuleId, new ArrayList<>());
            }
            breachedCaseIdsByRule.get(violationRuleId).add(violation.getCaseId());

            // increment case id's violation count
            int numBreaches = 1;
            if (numBreachesPerCase.containsKey(violation.getCaseId())) {
                numBreaches += numBreachesPerCase.get(violation.getCaseId());
            }
            numBreachesPerCase.put(violation.getCaseId(), numBreaches);
        }

        // extract maximum and minimum breaches per case
        Entry<String, Integer> max = null;
        Entry<String, Integer> min = null;
        if (numBreachesPerCase.size() > 0) {
            max = Collections.max(numBreachesPerCase.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
            min = Collections.min(numBreachesPerCase.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
        }

        List<ComplianceCheckReport.ReportBreaches> breachesByRule = new ArrayList<>();
        for (Entry<Long, List<String>> entry : breachedCaseIdsByRule.entrySet()) {
            breachesByRule.add(
                new ComplianceCheckReport.ReportBreaches(
                    ComplianceRulesDto.fromModel(rulesMap.get(entry.getKey())),
                    ControlDto.fromModel(controlsByRule.get(entry.getKey())),
                    entry.getValue()
                )
            );
        }

        // return full report object
        return new ComplianceCheckReport(
            totalCases,
            numBreachesPerCase.size(),
            max == null ? "NONE" : max.getKey(),
            max == null ? 0 : max.getValue(),
            min == null ? "NONE" : min.getKey(),
            min == null ? 0 : min.getValue(),
            numBreachesPerCase.size() == 0 ? 0 : (double) violations.size() / numBreachesPerCase.size(),
            breachesByRule
        );
    }

    @Override
    public List<ComplianceCheckDto> getAllComplianceChecksForProcess(long processId) {
        return Utils.mapList(complianceCheckRepository.findByProcessId(processId), ComplianceCheckDto::fromModel);
    }

    @Override
    public List<ComplianceCheckDto> getAllComplianceChecks() {
        return StreamSupport
            .stream(complianceCheckRepository.findAll().spliterator(), false)
            .map(check -> ComplianceCheckDto.fromModel(check))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ComplianceCheckDto getComplianceCheckById(long complianceCheckId) {
        Optional<ComplianceCheckModel> complianceCheck = complianceCheckRepository.findById(complianceCheckId);
        return complianceCheck.isPresent() ? ComplianceCheckDto.fromModel(complianceCheck.get()) : null;
    }

    @Override
    public ComplianceCheckDto checkProcessCompliance(long processId, List<ComplianceRulesDto> allRulesDto) {
        ProcessModel process = processRepository.findById(processId).get();

        List<ComplianceRulesModel> allRules = new ArrayList<>();
        for (ComplianceRulesDto dto : allRulesDto) {
            ProcessModel procModel = new ProcessModel().setId(dto.getProcessId());
            ControlModel contModel = new ControlModel().setId(dto.getControlId());

            ComplianceRulesModel model = new ComplianceRulesModel()
                .setProcess(procModel)
                .setControl(contModel)
                .setId(dto.getId())
                .setRules(dto.getRules());
            allRules.add(model);
        }

        ComplianceCheckModel check = new ComplianceCheckModel()
            .setProcess(process)
            .setRunAt(new Timestamp(System.currentTimeMillis()))
            .setStatus(ComplianceCheckStatus.COMPLETED);

        List<ComplianceViolationModel> violations = new ArrayList<>();
        for (EventLogModel eventLog : process.getEventLogs()) {
            for (ComplianceRulesModel rules : allRules) {
                Set<String> violatedCaseIds = processComplianceCheck(eventLog, rules);

                for (String caseId : violatedCaseIds) {
                    ComplianceViolationModel violation = new ComplianceViolationModel()
                        .setCaseId(caseId)
                        .setComplianceCheck(check)
                        .setComplianceRule(rules);

                    violations.add(violation);
                }
            }
        }

        check.setViolations(violations);
        complianceCheckRepository.save(check);

        return ComplianceCheckDto.fromModel(check);
    }

    /**
     * Processes the given event log against a set of compliance rules to determine any violations.
     *
     * <p>
     * Each rule is defined as a logical composition of sub-rules: (A1 AND B1 AND C1 AND...) OR (A2 AND B2 AND C2 AND ...) OR ...
     * A violation for these rules can be determined using their negated form:
     * (A1' OR B1' OR C1' OR...) AND (A2' OR B2' OR C2' OR ...) AND ...
     * </p>
     *
     * <p>
     * Here:
     * <ul>
     *   <li>(A1', A2'... An'), (B1', B2'... Bn'), (...) are the sub-rules for each rule.</li>
     *   <li>AND represents the logical AND operation, meaning both sub-rules must be violated.</li>
     *   <li>OR represents the logical OR operation, meaning at least one of the sub-rules must be violated.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This method processes each rule by executing an SQL query to determine the violating case IDs for each sub-rule and
     * then combines these results according to the logical composition of the rules.
     * </p>
     *
     * @param eventLog The event log to be processed.
     * @param rules The compliance rules against which the event log is to be checked.
     * @return A set of case IDs that violate the provided rules. If no violations are found, returns an empty set.
     */
    private Set<String> processComplianceCheck(EventLogModel eventLog, ComplianceRulesModel rules) {
        // (A1' OR B1' OR C1'...) AND (A2' OR B2' OR C2'...) AND (A3' OR B3' OR C3'...) AND ...
        Set<String> violatingCaseIds = null;

        for (List<ComplianceRulesItem> andCompositedRules : rules.getRules()) {
            // A1' OR B1' OR C1' OR ...
            Set<String> violatingCaseIdsOfAndCompositedTemplates = new HashSet<>();

            for (ComplianceRulesItem rule : andCompositedRules) {
                QueryGenerationStrategy strategy = getQueryGenerator(rule.getTemplateId(), eventLog, rule.getValues());
                String sqlQuery = strategy.getQuery();

                // Assuming the query is written for violating cases
                // i.e If rule is for set A, then the query is written for A'
                List<String> violatingCaseIdsForTemplate = queryExecutor.executeQuery(
                    sqlQuery,
                    // TODO: use params if needed
                    new HashMap<>(),
                    SingleColumnRowMapper.newInstance(String.class)
                );

                violatingCaseIdsOfAndCompositedTemplates.addAll(violatingCaseIdsForTemplate);
            }

            if (violatingCaseIds == null) {
                violatingCaseIds = violatingCaseIdsOfAndCompositedTemplates;
            } else {
                violatingCaseIds.retainAll(violatingCaseIdsOfAndCompositedTemplates);
            }
        }

        return violatingCaseIds;
    }

    private QueryGenerationStrategy getQueryGenerator(
        Long templateId,
        EventLogModel eventLogModel,
        Map<String, String> templateValues
    ) {
        TemplateModel template = templateRepository.findById(templateId).get();
        return queryGeneratorProvider.getQueryGenerator(template, eventLogModel, templateValues);
    }
}
