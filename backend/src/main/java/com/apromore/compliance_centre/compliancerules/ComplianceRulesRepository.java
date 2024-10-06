package com.apromore.compliance_centre.compliancerules;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ComplianceRulesRepository extends CrudRepository<ComplianceRulesModel, Long> {
    List<ComplianceRulesModel> findByProcessId(Long processId);
    List<ComplianceRulesModel> findByControlId(Long controlId);
}