package com.apromore.compliance_centre.compliancerules.templates;

import com.apromore.compliance_centre.compliancerules.ComplianceRulesItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class ComplianceRulesJsonConverter implements AttributeConverter<List<List<ComplianceRulesItem>>, String> {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<List<ComplianceRulesItem>> rules) {
        try {
            return mapper.writeValueAsString(rules);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting List<List<ComplianceRuleModel>> to JSON", e);
        }
    }

    @Override
    public List<List<ComplianceRulesItem>> convertToEntityAttribute(String dbData) {
        if (dbData == null) return new ArrayList<>(new ArrayList<>());

        try {
            return mapper.readValue(dbData, new TypeReference<List<List<ComplianceRulesItem>>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to List<List<ComplianceRuleModel>>", e);
        }
    }
}
