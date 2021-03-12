/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.opensearch.repositories.blobstore;

import org.elasticsearch.cluster.metadata.RepositoryMetadata;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.UUIDs;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.indices.recovery.RecoverySettings;
import org.opensearch.repositories.RepositoryInfo;
import org.opensearch.repositories.RepositoryStatsSnapshot;
import org.opensearch.threadpool.ThreadPool;

import java.util.Map;

public abstract class MeteredBlobStoreRepository extends BlobStoreRepository {
    private final RepositoryInfo repositoryInfo;

    public MeteredBlobStoreRepository(RepositoryMetadata metadata,
                                      boolean compress,
                                      NamedXContentRegistry namedXContentRegistry,
                                      ClusterService clusterService,
                                      RecoverySettings recoverySettings,
                                      Map<String, String> location) {
        super(metadata, compress, namedXContentRegistry, clusterService, recoverySettings);
        ThreadPool threadPool = clusterService.getClusterApplierService().threadPool();
        this.repositoryInfo = new RepositoryInfo(UUIDs.randomBase64UUID(),
            metadata.name(),
            metadata.type(),
            location,
            threadPool.absoluteTimeInMillis());
    }

    public RepositoryStatsSnapshot statsSnapshot() {
        return new RepositoryStatsSnapshot(repositoryInfo, stats(), RepositoryStatsSnapshot.UNKNOWN_CLUSTER_VERSION, false);
    }

    public RepositoryStatsSnapshot statsSnapshotForArchival(long clusterVersion) {
        RepositoryInfo stoppedRepoInfo = repositoryInfo.stopped(threadPool.absoluteTimeInMillis());
        return new RepositoryStatsSnapshot(stoppedRepoInfo, stats(), clusterVersion, true);
    }
}