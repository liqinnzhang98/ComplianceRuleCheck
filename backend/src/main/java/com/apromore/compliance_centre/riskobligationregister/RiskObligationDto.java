package com.apromore.compliance_centre.riskobligationregister;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
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
public class RiskObligationDto {

    private long id;

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private String type;

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private String name = "";

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private String description;

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private String category;

    @Nullable
    private String subCategory;

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private List<ControlDto> controls;
}
