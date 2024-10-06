package com.apromore.compliance_centre.compliancerules.templates;

import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashMap;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "compliance_rule_templates")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateModel {

    @Id
    Long id;

    String rule;

    String description;

    String example;

    TemplateCategory category;

    @Convert(converter = JsonListConverter.class)
    List<String> formFieldsOrder;

    @OneToMany(mappedBy = "template", orphanRemoval = true, cascade = CascadeType.ALL)
    List<FormFieldModel> formFields;

    public TemplateType getType() {
        return TemplateType.fromString(rule);
    }

    HashMap<String, FormFieldModel> getFormFieldsMap() {
        HashMap<String, FormFieldModel> map = new HashMap<>();
        for (FormFieldModel formField : formFields) {
            map.put(formField.getName(), formField);
        }
        return map;
    }
}
