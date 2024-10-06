package com.apromore.compliance_centre.compliancecheck;

import com.apromore.compliance_centre.process.ProcessDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;

import java.sql.Timestamp;
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
public class ComplianceCheckDto {

    private long id;

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private Timestamp runAt;

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private ComplianceCheckStatus status;

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private ProcessDto process;

    @Nullable
    private ComplianceCheckReport report;

    public static ComplianceCheckDto fromModel(ComplianceCheckModel model) {
        return new ComplianceCheckDto()
            .setId(model.getId())
            .setRunAt(model.getRunAt())
            .setStatus(model.getStatus())
            .setProcess(ProcessDto.fromModel(model.getProcess()));
	}
}

