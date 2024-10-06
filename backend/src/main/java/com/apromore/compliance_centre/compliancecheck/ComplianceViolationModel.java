package com.apromore.compliance_centre.compliancecheck;

import com.apromore.compliance_centre.compliancerules.ComplianceRulesModel;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "compliance_violations")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@ToString
public class ComplianceViolationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String caseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compliance_check_id", referencedColumnName = "id")
    private ComplianceCheckModel complianceCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compliance_rule_id", referencedColumnName = "id")
    private ComplianceRulesModel complianceRule;
}
