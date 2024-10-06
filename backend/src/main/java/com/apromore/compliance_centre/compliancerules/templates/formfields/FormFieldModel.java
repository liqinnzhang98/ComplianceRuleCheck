package com.apromore.compliance_centre.compliancerules.templates.formfields;

import com.apromore.compliance_centre.compliancerules.templates.TemplateModel;
import com.apromore.compliance_centre.process.attributes.AttributeType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "compliance_rule_template_form_fields")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@ToString
public class FormFieldModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String label;

    FormFieldType type;

    String name;

    String dependsOn;

    String text;

    AttributeType selectFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    @ToString.Exclude
    private TemplateModel template;
}
