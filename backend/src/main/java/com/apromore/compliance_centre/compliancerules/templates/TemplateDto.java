package com.apromore.compliance_centre.compliancerules.templates;

import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.stream.Collectors;
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
public class TemplateDto {

    private Long id;
    private String rule;
    private String description;
    private String example;
    private String category;
    private List<FormFieldDto> formFields;
    private Float recommendation;

    public static TemplateDto fromModel(TemplateModel model) {
        return new TemplateDto()
            .setRule(model.getRule())
            .setId(model.getId())
            .setCategory(model.getCategory().toString())
            .setDescription(model.getDescription())
            .setExample(model.getExample())
            .setFormFields(model.getFormFields().stream().map(FormFieldDto::fromModel).collect(Collectors.toList()));
    }
}
