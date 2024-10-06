package com.apromore.compliance_centre.riskobligationregister;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@Entity
@Table(name = "riskObligations")
@Accessors(chain = true)
public class RiskObligationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // the type of "risk" or "obligation"
    private String type;

    private String name;

    private String description;

    private String category;

    private String subCategory;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "risk_obligation_control", joinColumns = @JoinColumn(name = "riskObligations_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "controls_id", referencedColumnName = "id"))
    private List<ControlModel> controls;

    public RiskObligationModel(String type, String name, String description, String category, String subCategory) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
    }
}
