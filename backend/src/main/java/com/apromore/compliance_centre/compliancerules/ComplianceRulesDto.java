package com.apromore.compliance_centre.compliancerules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ComplianceRulesDto {

    private Long id;

    private Long processId;

    private Long controlId;

    private List<List<ComplianceRulesItem>> rules;

    public static ComplianceRulesDto fromModel(ComplianceRulesModel model) {
        return new ComplianceRulesDto()
            .setId(model.getId())
            .setProcessId(model.getProcess() != null ? model.getProcess().getId() : null)
            .setControlId(model.getControl() != null ? model.getControl().getId() : null)
            .setRules(model.getRules());
    }
}
