package com.apromore.compliance_centre.eventlogs;


import com.apromore.compliance_centre.process.ProcessDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventLogDto {
    private long id;

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private String name;

    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());


    public static EventLogDto fromModel(EventLogModel eventLog) {
        return new EventLogDto().setId(eventLog.getId()).setName(eventLog.getName())
                .setCreatedAt(eventLog.getCreatedAt());
    }
}
