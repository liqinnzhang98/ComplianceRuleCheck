package com.apromore.compliance_centre.compliancerules.querygenerator;

import java.util.Map;

public interface QueryGenerationStrategy {
    public String getQuery();

    void processQueryParameters(Map<String, String> templateValues);
}
