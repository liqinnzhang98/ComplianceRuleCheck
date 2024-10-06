package com.apromore.compliance_centre.process.attributes;

import com.apromore.compliance_centre.process.ProcessModel;
import com.apromore.compliance_centre.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@Table(name = "attributes")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String displayName;

	private AttributeType type;

	private AttributeDataType dataType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "process_id", referencedColumnName = "id")
	@ToString.Exclude
	ProcessModel process;

	public String getDatabaseColumnName() {
		if (name == null)
			return null;

		return "`" + name + "`";
	}

	public AttributeType getAttributeType() {
		return type;
	}

	public static AttributeModel fromDto(AttributeDto dto) {
		return new AttributeModel()
				.setName(dto.getName() == null ? Utils.toValidColumnName(dto.getDisplayName())
						: dto.getName())

				.setDisplayName(dto.getDisplayName()).setType(dto.getType())
				.setDataType(dto.getDataType());
	}
}
