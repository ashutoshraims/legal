//  Copyright © Microsoft Corporation
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.opengroup.osdu.legal.azure.di;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.security.keyvault.secrets.SecretClient;
import org.opengroup.osdu.azure.KeyVaultFacade;
import org.opengroup.osdu.common.Validators;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.inject.Named;

@Component
public class AzureBootstrapConfig {

    @Value("${azure.storage.account-name}")
    private String storageAccount;

    @Value("${azure.storage.container-name}")
    private String storageContainer;

    @Value("${azure.servicebus.topic-name}")
    private String serviceBusTopic;

    @Value("${azure.servicebus.namespace-name}")
    private String serviceBusNamespace;

    @Value("${azure.cosmosdb.tenant.collection}")
    private String tenantCollectionName;

    @Value("${azure.cosmosdb.legal.collection}")
    private String legalCollectionName;

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
    @Named("SERVICE_BUS_NAMESPACE")
    public String serviceBusNamespace() {
        return serviceBusNamespace;
    }

    @Bean
    @Named("SERVICE_BUS_TOPIC")
    public String serviceBusTopic() {
        return serviceBusTopic;
    }

    @Bean
    @Named("KEY_VAULT_URL")
    public String keyVaultURL() {
        return keyVaultURL;
    }

    @Bean
    @Named("COSMOS_ENDPOINT")
    public String cosmosEndpoint(SecretClient kv) {
        return KeyVaultFacade.getSecretWithValidation(kv, "cosmos-endpoint");
    }

    @Bean
    @Named("COSMOS_KEY")
    public String cosmosKey(SecretClient kv) {
        return KeyVaultFacade.getSecretWithValidation(kv, "cosmos-primary-key");
    }

    @Bean
    @Named("LEGAL_TAGS_CONTAINER")
    CosmosContainer legalTagsContainer(final CosmosClient cosmosClient) {
        Validators.checkNotNull(cosmosClient, "Cosmos client cannot be null");
        return cosmosClient.getDatabase(cosmosDBName).getContainer(legalCollectionName);
    }

    @Bean
    @Named("TENANT_INFO_CONTAINER")
    CosmosContainer tenantInfoContainer(final CosmosClient cosmosClient) {
        Validators.checkNotNull(cosmosClient, "Cosmos client cannot be null");
        return cosmosClient.getDatabase(cosmosDBName).getContainer(tenantCollectionName);
    }
}