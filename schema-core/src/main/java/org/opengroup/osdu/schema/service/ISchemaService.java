package org.opengroup.osdu.schema.service;

import org.opengroup.osdu.schema.exceptions.ApplicationException;
import org.opengroup.osdu.schema.exceptions.BadRequestException;
import org.opengroup.osdu.schema.exceptions.NotFoundException;
import org.opengroup.osdu.schema.model.QueryParams;
import org.opengroup.osdu.schema.model.SchemaInfo;
import org.opengroup.osdu.schema.model.SchemaInfoResponse;
import org.opengroup.osdu.schema.model.SchemaRequest;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ISchemaService {

	public Object getSchema(String schemaId) throws BadRequestException, NotFoundException, ApplicationException;

	SchemaInfo createSchema(SchemaRequest schemaRequest)
			throws ApplicationException, BadRequestException, JsonProcessingException;

	SchemaInfo updateSchema(SchemaRequest schemaRequest)
			throws ApplicationException, BadRequestException, JsonProcessingException;

	SchemaInfoResponse getSchemaInfoList(QueryParams queryParams) throws BadRequestException, ApplicationException;

}
