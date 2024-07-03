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

import jakarta.inject.Named;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

@Component
public class AzureBootstrapConfig {


    @Value("${azure.keyvault.url}")
    private String keyVaultURL;

    @Bean
    @Named("KEY_VAULT_URL")
    public String keyVaultURL() {
        return keyVaultURL;
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
}