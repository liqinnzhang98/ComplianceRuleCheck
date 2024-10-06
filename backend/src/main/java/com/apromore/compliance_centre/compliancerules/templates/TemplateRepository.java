package com.apromore.compliance_centre.compliancerules.templates;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateModel, Long> {
    List<TemplateModel> findByCategory(TemplateCategory category);
}
