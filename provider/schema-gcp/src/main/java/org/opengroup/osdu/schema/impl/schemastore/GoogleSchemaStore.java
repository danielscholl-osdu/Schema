/*
  Copyright 2020 Google LLC
  Copyright 2020 EPAM Systems, Inc

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.opengroup.osdu.schema.impl.schemastore;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.provider.interfaces.ITenantFactory;
import org.opengroup.osdu.core.gcp.multitenancy.GcsMultiTenantAccess;
import org.opengroup.osdu.schema.constants.SchemaConstants;
import org.opengroup.osdu.schema.exceptions.ApplicationException;
import org.opengroup.osdu.schema.exceptions.NotFoundException;
import org.opengroup.osdu.schema.provider.interfaces.schemastore.ISchemaStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Repository class to to register resolved Schema in Google storage.
 *
 *
 */
@Repository
public class GoogleSchemaStore implements ISchemaStore {

    @Autowired
    private DpsHeaders headers;

    @Autowired
    private GcsMultiTenantAccess storageFactory;

    @Autowired
    ITenantFactory tenantFactory;
    
    @Value("${shared.tenant.name:common}")
	private String sharedTenant;

    @Autowired
    JaxRsDpsLog log;

    /**
     * Method to get schema from google Storage given Tenant ProjectInfo
     *
     * @param dataPartitionId
     * @param filePath
     * @throws NotFoundException
     * @return schema object
     * @throws ApplicationException
     * @throws NotFoundException
     */
    @Override
    public String getSchema(String dataPartitionId, String filePath) throws ApplicationException, NotFoundException {
        filePath = filePath + SchemaConstants.JSON_EXTENSION;
        String bucketname = getSchemaBucketName(dataPartitionId);
        Storage storage = storageFactory.get(tenantFactory.getTenantInfo(dataPartitionId));
        Blob blob = storage.get(bucketname, filePath);
        if (blob != null) {
            return new String(blob.getContent());
        }
        throw new NotFoundException(SchemaConstants.SCHEMA_NOT_PRESENT);
    }

    /**
     * Method to get System schema from google Storage
     * @param filePath
     * @return Schema object
     * @throws NotFoundException
     * @throws ApplicationException
     */
    @Override
    public String getSystemSchema(String filePath) throws NotFoundException, ApplicationException {
        return this.getSchema(sharedTenant, filePath);
    }

    /**
     * Method to write schema to google Storage given Tenant ProjectInfo
     *
     * @param filePath
     * @param content
     * @return schema object
     * @throws ApplicationException
     */

    @Override
    public String createSchema(String filePath, String content) throws ApplicationException {
        String dataPartitionId = headers.getPartitionId();
        filePath = filePath + SchemaConstants.JSON_EXTENSION;
        String bucketname = getSchemaBucketName(dataPartitionId);
        BlobId blobId = BlobId.of(bucketname, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Storage storage = storageFactory.get(tenantFactory.getTenantInfo(dataPartitionId));
        try {
            Blob blob = storage.create(blobInfo, content.getBytes());
            log.info(SchemaConstants.SCHEMA_CREATED);
            return blob.getName();
        } catch (StorageException ex) {
            throw new ApplicationException(SchemaConstants.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method to write System schema to google Storage
     * @param filePath
     * @param content
     * @return schema object
     * @throws ApplicationException
     */
    @Override
    public String createSystemSchema(String filePath, String content) throws ApplicationException {
        this.updateDataPartitionId();
        return this.createSchema(filePath, content);

    }

    @Override
    public boolean cleanSchemaProject(String schemaId) throws ApplicationException {
        String dataPartitionId = headers.getPartitionId();
        String fileName = schemaId + SchemaConstants.JSON_EXTENSION;
        String bucketname = getSchemaBucketName(dataPartitionId);
        BlobId blobId = BlobId.of(bucketname, fileName);
        return storageFactory.get(tenantFactory.getTenantInfo(dataPartitionId)).delete(blobId);
    }

    /**
     * Method to clean System schema from google Storage
     * @param schemaId
     * @return
     * @throws ApplicationException
     */
    @Override
    public boolean cleanSystemSchemaProject(String schemaId) throws ApplicationException {
        this.updateDataPartitionId();
        return this.cleanSchemaProject(schemaId);
    }

    private String getSchemaBucketName(String dataPartitionId) {
        return tenantFactory.getTenantInfo(dataPartitionId).getProjectId() + SchemaConstants.SCHEMA_BUCKET_EXTENSION;
    }

    private void updateDataPartitionId() {
        headers.put(SchemaConstants.DATA_PARTITION_ID, sharedTenant);
    }

}
