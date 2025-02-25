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

package org.opengroup.osdu.schema.azure;

import org.opengroup.osdu.azure.dependencies.AzureOSDUConfig;
import org.opengroup.osdu.schema.azure.di.AzureBootstrapConfig;
import org.opengroup.osdu.schema.azure.di.CosmosContainerConfig;
import org.opengroup.osdu.schema.azure.di.EventGridConfig;
import org.opengroup.osdu.schema.azure.di.PubSubConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@ComponentScan({ "org.opengroup" })
@PropertySource("classpath:swagger.properties")
public class SchemaAzureApplication {
    public static void main(String[] args)
    {
        Class<?>[] sources = new Class<?>[]{
                SchemaAzureApplication.class,
                AzureBootstrapConfig.class,
                AzureOSDUConfig.class,
                CosmosContainerConfig.class,
                EventGridConfig.class,
                PubSubConfig.class
        };
        SpringApplication.run(sources, args);
    }
}
