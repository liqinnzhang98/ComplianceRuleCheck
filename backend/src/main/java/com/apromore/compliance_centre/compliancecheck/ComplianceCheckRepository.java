package com.apromore.compliance_centre.compliancecheck;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ComplianceCheckRepository extends CrudRepository<ComplianceCheckModel, Long> {
    List<ComplianceCheckModel> findByProcessId(long processId);
}
