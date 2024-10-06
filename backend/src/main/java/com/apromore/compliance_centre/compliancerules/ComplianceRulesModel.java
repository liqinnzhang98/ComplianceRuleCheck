package com.apromore.compliance_centre.compliancerules;

import com.apromore.compliance_centre.compliancecheck.ComplianceViolationModel;
import com.apromore.compliance_centre.compliancerules.templates.ComplianceRulesJsonConverter;
import com.apromore.compliance_centre.process.ProcessModel;
import com.apromore.compliance_centre.riskobligationregister.ControlModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "compliance_rules")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ComplianceRulesModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", referencedColumnName = "id")
    private ProcessModel process;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "control_id", referencedColumnName = "id")
    private ControlModel control;

    @Convert(converter = ComplianceRulesJsonConverter.class)
    @Column(length = 65535, columnDefinition = "TEXT")
    private List<List<ComplianceRulesItem>> rules;

    @OneToMany(mappedBy = "complianceRule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ComplianceViolationModel> violations;

    public static ComplianceRulesModel fromDto(ComplianceRulesDto complianceRulesDto) {
        return new ComplianceRulesModel()
            .setId(complianceRulesDto.getId())
            .setRules(complianceRulesDto.getRules())
            .setProcess(new ProcessModel().setId(complianceRulesDto.getProcessId()))
            .setControl(new ControlModel().setId(complianceRulesDto.getControlId()));
    }
}
