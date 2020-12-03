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

package org.opengroup.osdu.schema.azure.di;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.identity.DefaultAzureCredential;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

import javax.inject.Named;

@Component
public class AzureBootstrapConfig {

    @Value("${azure.storage.account-name}")
    private String storageAccount;

    @Value("${azure.storage.container-name}")
    private String storageContainer;

    @Value("${azure.keyvault.url}")
    private String keyVaultURL;

    @Value("${azure.cosmosdb.database}")
    private String cosmosDBName;

    @Bean
    @Named("STORAGE_ACCOUNT_NAME")
    public String storageAccount() {
        return storageAccount;
    }

    @Bean
    @Named("STORAGE_CONTAINER_NAME")
    public String containerName() {
        return storageContainer;
    }

    @Bean
    @Named("COSMOS_DB_NAME")
    public String cosmosDBName() {
        return cosmosDBName;
    }

    @Bean
    @Named("KEY_VAULT_URL")
    public String keyVaultURL() {
        return keyVaultURL;
    }

    @Bean
    @Named("COSMOS_ENDPOINT")
    public String cosmosEndpoint(SecretClient kv) {
        return getKeyVaultSecret(kv, "opendes-cosmos-endpoint");
    }

    @Bean
    @Named("COSMOS_KEY")
    public String cosmosKey(SecretClient kv) {
        return getKeyVaultSecret(kv, "opendes-cosmos-primary-key");
    }

    public String getKeyVaultSecret(SecretClient kv, String secretName) {
        KeyVaultSecret secret = kv.getSecret(secretName);
        if (secret == null) {
            throw new IllegalStateException(String.format("No secret found with name %s", secretName));
        }

        String secretValue = secret.getValue();
        if (secretValue == null) {
            throw new IllegalStateException(String.format(
                    "Secret unexpectedly missing from KeyVault response for secret with name %s", secretName));
        }

        return secretValue;
    }

    @Bean
    public CosmosClient buildCosmosClient(SecretClient kv)
    {
        return new CosmosClientBuilder().endpoint(cosmosEndpoint(kv)).key(cosmosKey(kv)).buildClient();
    }

    @Autowired
    private DefaultAzureCredential defaultAzureCredential;

    @Bean
    public BlobServiceClient buildBlobServiceClient()
    {
        String blobEndpoint = String.format("https://%s.blob.core.windows.net", storageAccount);
        return new BlobServiceClientBuilder().endpoint(blobEndpoint).credential(defaultAzureCredential).buildClient();

    }
}