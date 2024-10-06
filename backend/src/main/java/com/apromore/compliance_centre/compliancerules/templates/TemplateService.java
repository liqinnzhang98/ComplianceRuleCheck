package com.apromore.compliance_centre.compliancerules.templates;

import java.util.HashMap;
import java.util.List;

public interface TemplateService {
    TemplateDto getTemplate(Long id);

    List<TemplateDto> getAllTemplates();

    HashMap<Long, TemplateDto> getAllTemplatesMap();

    List<TemplateDto> getTemplatesByCategory(TemplateCategory category);

    List<TemplateDto> importTemplates();
}
