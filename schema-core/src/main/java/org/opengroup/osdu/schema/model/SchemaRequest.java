package org.opengroup.osdu.schema.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.opengroup.osdu.schema.validation.SchemaRequestConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchemaRequest {

	@NotNull(message = "schemaInfo must not be null")
	@Valid
	private SchemaInfo schemaInfo;

	@NotNull(message = "schema must not be null")
    @SchemaRequestConstraint
	private Object schema;

}
