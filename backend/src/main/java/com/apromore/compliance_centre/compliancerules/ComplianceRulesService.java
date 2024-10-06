package com.apromore.compliance_centre.compliancerules;

import java.util.List;

public interface ComplianceRulesService {

    /**
     * Save a new empty compliance rule
     * 
     * @param dto
     * @return
     */
    ComplianceRulesDto saveComplianceRules(ComplianceRulesDto dto);

    /**
     * Update an existing compliance rule
     * 
     * @param dto
     * @return
     */
    ComplianceRulesDto updateComplianceRules(ComplianceRulesDto dto, long id);

    /**
     * Return a list of all compliance rules associated to a process
     * 
     * @return
     */
    List<ComplianceRulesDto> getComplianceRulesByProcess(long process);

    /**
     * Return a list of all compliance rules associated to a control
     * 
     * @return
     */
    List<ComplianceRulesDto> getComplianceRulesByControl(long control);

    /**
     * Deletes a compliance rule based off id
     * 
     * @return
     */
    void deleteComplianceRuleById(Long id);

    /**
     * Checks if a rule exists
     * 
     * @return
     */
    boolean existsById(Long id);
}
