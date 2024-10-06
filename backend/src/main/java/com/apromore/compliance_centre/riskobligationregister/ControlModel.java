package com.apromore.compliance_centre.riskobligationregister;

import com.apromore.compliance_centre.compliancerules.ComplianceRulesModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "controls")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@ToString
public class ControlModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String type;

    private String name;

    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "controls")
    private List<RiskObligationModel> riskObligations;

    @OneToMany(mappedBy = "control", fetch = FetchType.LAZY)
    private List<ComplianceRulesModel> complianceRules;

    @Convert(converter = MappedTemplateIdJsonConverter.class)
    List<List<Long>> mappedTemplateIds;

    public ControlModel(String type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }
}
