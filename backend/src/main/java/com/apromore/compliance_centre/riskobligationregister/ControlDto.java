package com.apromore.compliance_centre.riskobligationregister;

import com.apromore.compliance_centre.compliancerules.templates.TemplateDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ControlDto {

    private long id;

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private String type;

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private String name;

    @NotEmpty(message = "{constraints.NotEmpty.message}")
    private String description;

    private List<List<TemplateDto>> templates;

    public static ControlDto fromModel(ControlModel model) {
        return new ControlDto()
            .setId(model.getId())
            .setType(model.getType())
            .setName(model.getName())
            .setDescription(model.getDescription());
    }
}
