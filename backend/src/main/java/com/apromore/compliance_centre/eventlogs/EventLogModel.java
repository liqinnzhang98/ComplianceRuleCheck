package com.apromore.compliance_centre.eventlogs;

import com.apromore.compliance_centre.process.ProcessModel;
import com.apromore.compliance_centre.process.attributes.AttributeModel;
import com.apromore.compliance_centre.process.attributes.AttributeType;
import com.apromore.compliance_centre.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "event_logs")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@ToString
public class EventLogModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String displayName;

    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private ProcessModel process;

    public List<AttributeModel> getAttributes() {
        return getProcess().getAttributes();
    }

    public AttributeModel getAttributeByType(AttributeType type) {
        return getAttributes().stream().filter(attr -> attr.getType() == type).findFirst().orElse(null);
    }

    @Transient
    private String tableName = null;

    public String getEventLogTableName() {
        if (tableName != null) {
            return tableName;
        }

        // remove extension if any
        String processedName = name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : name;

        // sanitize table name
        processedName = Utils.toValidTableName(processedName);

        // add prefix and suffix
        tableName = "`log_" + processedName + "_" + id + "`";

        return tableName;
    }

    public static EventLogModel fromDto(EventLogDto eventLogDto) {
        return new EventLogModel()
            .setName(Utils.toValidTableName(eventLogDto.getName()))
            .setDisplayName(eventLogDto.getName())
            .setCreatedAt(eventLogDto.getCreatedAt());
    }
}
