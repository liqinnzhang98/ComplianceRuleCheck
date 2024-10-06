package com.apromore.compliance_centre.eventlogs;

import com.apromore.compliance_centre.process.ProcessDto;
import com.apromore.compliance_centre.process.attributes.AttributeDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventLogDataDto {

    private List<AttributeDto> attributes;

    private List<Map<String, Object>> data;
}
