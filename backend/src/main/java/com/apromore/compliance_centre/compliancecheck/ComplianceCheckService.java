package com.apromore.compliance_centre.compliancecheck;

import java.util.List;

import com.apromore.compliance_centre.compliancerules.ComplianceRulesDto;
import com.apromore.compliance_centre.compliancerules.ComplianceRulesModel;

public interface ComplianceCheckService {
    /**
     * 
     * @return
     */
    List<ComplianceCheckDto> getAllComplianceChecksForProcess(long processId);

    /**
     * 
     * @return
     */
    List<ComplianceCheckDto> getAllComplianceChecks();

    /**
     * 
     * @param complianceCheckId
     * @return
     */
    ComplianceCheckDto getComplianceCheckById(long complianceCheckId);

    /**
     * Get the aggregated results for a particular compliance check
     * 
     * @param complianceCheckId;
     * @return
     */
    ComplianceCheckReport getReportResults(long complianceCheckId);

    /**
     * @param processId
     * @param allRules
     * @return
     */
    ComplianceCheckDto checkProcessCompliance(long processId, List<ComplianceRulesDto> allRules);
}
