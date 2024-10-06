package com.apromore.compliance_centre.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.persistence.AttributeConverter;

public class JsonConverter<T> implements AttributeConverter<T, String> {

    private final TypeReference<T> typeReference = new TypeReference<T>() {};

    @Resource
    private ObjectMapper mapper;

    @Override
    public String convertToDatabaseColumn(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T convertToEntityAttribute(String json) {
        if (json == null || json.isEmpty()) return null;

        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
