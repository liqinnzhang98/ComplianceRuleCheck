package com.apromore.compliance_centre.compliancerules.templates;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TemplateRecommendationDto {
    private String controlDescription;
    private Map<String, Float> recommendations;
}
