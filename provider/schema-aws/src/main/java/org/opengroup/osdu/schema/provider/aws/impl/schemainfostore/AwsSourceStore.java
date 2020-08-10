package org.opengroup.osdu.schema.provider.aws.impl.schemainfostore;

import org.opengroup.osdu.core.aws.dynamodb.DynamoDBQueryHelper;
import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.schema.constants.SchemaConstants;
import org.opengroup.osdu.schema.exceptions.ApplicationException;
import org.opengroup.osdu.schema.exceptions.BadRequestException;
import org.opengroup.osdu.schema.exceptions.NotFoundException;
import org.opengroup.osdu.schema.model.Source;
import org.opengroup.osdu.schema.provider.aws.config.AwsServiceConfig;
import org.opengroup.osdu.schema.provider.aws.models.SourceDoc;
import org.opengroup.osdu.schema.provider.interfaces.schemainfostore.ISourceStore;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.text.MessageFormat;

@Repository
public class AwsSourceStore implements ISourceStore {

  public static String SOURCE_NOT_FOUND = "source not found";

  @Inject
  private DpsHeaders headers;

  @Inject
  private JaxRsDpsLog log;

  @Inject
  private AwsServiceConfig serviceConfig;

  private DynamoDBQueryHelper queryHelper;

  @PostConstruct
  public void init() {
    queryHelper = new DynamoDBQueryHelper(serviceConfig.getDynamoDbEndpoint(),
            serviceConfig.getAmazonRegion(),
            serviceConfig.getDynamoDbTablePrefix());
  }

  @Override
  public Source get(String sourceId) throws NotFoundException, ApplicationException {
    String id = headers.getPartitionId() + ":" + sourceId;
    SourceDoc result = queryHelper.loadByPrimaryKey(SourceDoc.class, id);
    if (result == null) {
      throw new NotFoundException(SOURCE_NOT_FOUND);
    }
    return result.getSource();
  }

  @Override
  public Source create(Source source) throws BadRequestException, ApplicationException {

    Source result = null;
    try {
      result = this.get(source.getSourceId());
      if (result != null) {
        throw new BadRequestException(MessageFormat.format(SchemaConstants.SOURCE_EXISTS_EXCEPTION,
                source.getSourceId()));
      }
    } catch (NotFoundException e) { }

    try {
      String id = headers.getPartitionId() + ":" + source.getSourceId();
      SourceDoc sourceDoc = new SourceDoc(id, headers.getPartitionId(), source);
      queryHelper.save(sourceDoc);

    } catch (Exception e) {
      log.error(MessageFormat.format(SchemaConstants.OBJECT_INVALID, e.getMessage()));
      throw new ApplicationException(SchemaConstants.INVALID_INPUT);
    }

    log.info(SchemaConstants.SOURCE_CREATED);
    return source;
  }
}
