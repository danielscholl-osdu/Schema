// Copyright © Microsoft Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.opengroup.osdu.schema.azure.impl.schemastore;

import com.azure.storage.blob.BlobContainerClient;
import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.schema.azure.utils.OSDUAzureBlobStorageImpl;
import org.opengroup.osdu.schema.constants.SchemaConstants;
import org.opengroup.osdu.schema.exceptions.ApplicationException;
import org.opengroup.osdu.schema.exceptions.NotFoundException;
import org.opengroup.osdu.schema.provider.interfaces.schemastore.ISchemaStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repository class to to register resolved Schema in Azure storage.
 *
 *
 */
@Repository
public class AzureSchemaStore implements ISchemaStore {

    @Autowired
    private DpsHeaders headers;

    @Autowired
    private BlobContainerClient blobContainerClient;

    @Autowired
    private OSDUAzureBlobStorageImpl blobStorage;

    @Autowired
    JaxRsDpsLog log;

    /**
     * Method to get schema from azure Storage given Tenant ProjectInfo
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
        filePath = dataPartitionId + ":" + filePath + SchemaConstants.JSON_EXTENSION;
        try {
            String content = blobStorage.readFromBlob(filePath);
            if (content != null)
                return content;
            else
                throw new NotFoundException(SchemaConstants.SCHEMA_NOT_PRESENT);
        }
        catch (Exception ex) {
            log.info(ex.getMessage());
            throw new NotFoundException(SchemaConstants.SCHEMA_NOT_PRESENT);
        }
    }

    /**
     * Method to write schema to azure Storage given Tenant
     * @param filePath
     * @param content
     * @return
     * @throws ApplicationException
     */

    @Override
    public String createSchema(String filePath, String content) throws ApplicationException {
        String dataPartitionId = headers.getPartitionId();
        filePath = dataPartitionId + ":" + filePath + SchemaConstants.JSON_EXTENSION;

        try {
            return blobStorage.writeToBlob(filePath, content);
        } catch (Exception ex) {
            throw new ApplicationException(SchemaConstants.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a schema from azure Storage
     * @param schemaId
     * @return
     * @throws ApplicationException
     */
    @Override
    public boolean cleanSchemaProject(String schemaId) throws ApplicationException {
        String dataPartitionId = headers.getPartitionId();
        String filePath = dataPartitionId + ":" + schemaId + SchemaConstants.JSON_EXTENSION;

        try
        {
            return blobStorage.deleteFromBlob(filePath);
        }
        catch (Exception e)
        {
            throw new ApplicationException(SchemaConstants.INTERNAL_SERVER_ERROR);
        }
    }
}
