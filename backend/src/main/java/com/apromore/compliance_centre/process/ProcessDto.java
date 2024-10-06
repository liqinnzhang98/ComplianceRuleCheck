package com.apromore.compliance_centre.process;

import java.util.List;

import com.apromore.compliance_centre.eventlogs.EventLogDto;
import com.apromore.compliance_centre.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessDto {
    private long id;

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private String name;

    public List<EventLogDto> eventLogs;

    public static ProcessDto fromModel(ProcessModel process) {
        ProcessDto processDto = new ProcessDto().setId(process.getId()).setName(process.getName());
        var eventLogs = process.getEventLogs();

        if (eventLogs != null) {
            processDto.setEventLogs(Utils.mapList(eventLogs, EventLogDto::fromModel));
        }
        
        return processDto;
    }
}
