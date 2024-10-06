package com.apromore.compliance_centre.process.attributes;


import com.apromore.compliance_centre.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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
public class AttributeDto {

	private Long id;

	private String name;

	@NotNull
	private String displayName;

	private AttributeDataType dataType = AttributeDataType.TEXT;

	private AttributeType type = AttributeType.ATTRIBUTE;

	public static AttributeDto fromModel(AttributeModel model) {
		return new AttributeDto().setId(model.getId()).setName(model.getName())
				.setDisplayName(model.getDisplayName()).setType(model.getType())
				.setDataType(model.getDataType());
	}

}
