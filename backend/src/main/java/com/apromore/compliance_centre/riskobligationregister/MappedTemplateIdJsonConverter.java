package com.apromore.compliance_centre.riskobligationregister;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class MappedTemplateIdJsonConverter implements AttributeConverter<List<List<Long>>, String> {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<List<Long>> attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting List<List<Long>> to JSON", e);
        }
    }

    @Override
    public List<List<Long>> convertToEntityAttribute(String dbData) {
        if (dbData == null) return new ArrayList<>(new ArrayList<>());

        try {
            return mapper.readValue(dbData, new TypeReference<List<List<Long>>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to List<List<Long>>", e);
        }
    }
}
