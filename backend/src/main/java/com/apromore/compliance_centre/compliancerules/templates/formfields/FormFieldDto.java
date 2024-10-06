package com.apromore.compliance_centre.compliancerules.templates.formfields;

import com.apromore.compliance_centre.process.attributes.AttributeType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class FormFieldDto {

    private String name;

    private String label;

    private String text;

    private FormFieldType type;

    private AttributeType selectFrom;

    private String dependsOn;

    public static FormFieldDto fromModel(FormFieldModel model) {
        return new FormFieldDto()
            .setName(model.getName())
            .setLabel(model.getLabel())
            .setText(model.getText())
            .setType(model.getType())
            .setDependsOn(model.getDependsOn())
            .setSelectFrom(model.getSelectFrom());
    }
}
