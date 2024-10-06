package com.apromore.compliance_centre.compliancecheck;

import java.sql.Timestamp;
import java.util.List;

import com.apromore.compliance_centre.process.ProcessModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "compliance_check")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@ToString
public class ComplianceCheckModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Timestamp runAt;

    private ComplianceCheckStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", referencedColumnName = "id")
    private ProcessModel process;

    @OneToMany(mappedBy = "complianceCheck", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ComplianceViolationModel> violations;

    @Transient
    private ComplianceCheckReport report;
}
