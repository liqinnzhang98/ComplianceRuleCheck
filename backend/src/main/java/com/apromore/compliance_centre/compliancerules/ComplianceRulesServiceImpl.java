package com.apromore.compliance_centre.compliancerules;

import com.apromore.compliance_centre.process.ProcessModel;
import com.apromore.compliance_centre.process.ProcessRepository;
import com.apromore.compliance_centre.response.Response;
import com.apromore.compliance_centre.response.ResponseError;
import com.apromore.compliance_centre.riskobligationregister.ControlModel;
import com.apromore.compliance_centre.riskobligationregister.ControlRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComplianceRulesServiceImpl implements ComplianceRulesService {

    @Autowired
    private ComplianceRulesRepository rulesRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ControlRepository controlRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ComplianceRulesServiceImpl(ComplianceRulesRepository complianceRulesRepository,
            ProcessRepository processRepository,
            ControlRepository controlRepository,
            ModelMapper modelMapper) {
        this.rulesRepository = complianceRulesRepository;
        this.processRepository = processRepository;
        this.controlRepository = controlRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ComplianceRulesDto> getComplianceRulesByProcess(long process) {
        return rulesRepository
            .findByProcessId(process)
            .stream()
            .map(this::complianceRulesModelMapper)
            .collect(Collectors.toList());
    }

    @Override
    public List<ComplianceRulesDto> getComplianceRulesByControl(long control) {
        return rulesRepository
            .findByControlId(control)
            .stream()
            .map(this::complianceRulesModelMapper)
            .collect(Collectors.toList());
    }

    @Override
    public ComplianceRulesDto saveComplianceRules(ComplianceRulesDto dto) {
        ProcessModel process = processRepository.findById(dto.getProcessId()).get();
        ControlModel control = controlRepository.findById(dto.getControlId()).get();

        ComplianceRulesModel newRules = new ComplianceRulesModel();
        newRules.setProcess(process);
        newRules.setControl(control);
        newRules.setRules(dto.getRules());

        return complianceRulesModelMapper(rulesRepository.save(newRules));
    }

    @Override
    public ComplianceRulesDto updateComplianceRules(ComplianceRulesDto dto, long id) {
        ProcessModel process = processRepository.findById(dto.getProcessId()).get();
        ControlModel control = controlRepository.findById(dto.getControlId()).get();

        ComplianceRulesModel newRules = new ComplianceRulesModel();
        newRules.setId(id);
        newRules.setProcess(process);
        newRules.setControl(control);
        newRules.setRules(dto.getRules());

        return complianceRulesModelMapper(rulesRepository.save(newRules));
    }

    private ComplianceRulesDto complianceRulesModelMapper(ComplianceRulesModel model) {
        return modelMapper.map(model, ComplianceRulesDto.class);
    }

    @Override
    public void deleteComplianceRuleById(Long id) {
        rulesRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return rulesRepository.existsById(id);
    }
}
