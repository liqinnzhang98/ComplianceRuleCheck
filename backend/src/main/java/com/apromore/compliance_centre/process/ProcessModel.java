package com.apromore.compliance_centre.process;

import java.util.List;

import com.apromore.compliance_centre.eventlogs.EventLogModel;
import com.apromore.compliance_centre.process.attributes.AttributeModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "processes")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@ToString
public class ProcessModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "process", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AttributeModel> attributes;

    @OneToMany(mappedBy = "process", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EventLogModel> eventLogs;

    @OneToMany(mappedBy = "process", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EventLogModel> complianceRules;

    static public ProcessModel fromDto(ProcessDto processDto) {
        return new ProcessModel()
            .setId(processDto.getId())
            .setName(processDto.getName());
    }
}
