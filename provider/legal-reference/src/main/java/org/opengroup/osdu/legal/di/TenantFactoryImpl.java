/*
 * Copyright 2021 Google LLC
 * Copyright 2021 EPAM Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opengroup.osdu.legal.di;

import static java.util.Objects.isNull;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bson.Document;
import org.opengroup.osdu.core.common.cache.ICache;
import org.opengroup.osdu.core.common.model.tenant.TenantInfo;
import org.opengroup.osdu.core.common.provider.interfaces.ITenantFactory;
import org.opengroup.osdu.legal.tags.dataaccess.MongoClientProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class TenantFactoryImpl implements ITenantFactory {

  private static final Logger LOG = LoggerFactory.getLogger(TenantFactoryImpl.class);
  private static final String SCHEMA_DATABASE = "main";
  private static final String TENANT_INFO = "tenantinfo";

  private final MongoClientProvider clientProvider;

  @Autowired
  public TenantFactoryImpl(MongoClientProvider clientProvider) {
    this.clientProvider = clientProvider;
  }

  private Map<String, TenantInfo> tenants;

  public boolean exists(String tenantName) {
    if (this.tenants == null) {
      initTenants();
    }
    return this.tenants.containsKey(tenantName);
  }

  public TenantInfo getTenantInfo(String tenantName) {
    if (Objects.isNull(tenants) || this.tenants.isEmpty()) {
      initTenants();
    }
    return this.tenants.get(tenantName);
  }

  public Collection<TenantInfo> listTenantInfo() {
    if (this.tenants == null) {
      initTenants();
    }
    return this.tenants.values();
  }

  public <V> ICache<String, V> createCache(String tenantName, String host, int port,
      int expireTimeSeconds, Class<V> classOfV) {
    return null;
  }

  public void flushCache() {
  }

  private void initTenants() {
    this.tenants = new HashMap<>();
    MongoCollection<Document> mongoCollection = clientProvider
        .getMongoClient()
        .getDatabase(SCHEMA_DATABASE)
        .getCollection(TENANT_INFO, Document.class);

    FindIterable<Document> results = mongoCollection.find();

    if (isNull(results) || isNull(results.first())) {
      LOG.error(String.format("Collection \'%s\' is empty.", results));
    }
    for (Document document : results) {
      TenantInfo tenantInfo = new Gson().fromJson(document.toJson(), TenantInfo.class);
      this.tenants.put(tenantInfo.getName(), tenantInfo);
    }
  }

}

