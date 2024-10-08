/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.mongodb.correlation;

import java.util.Optional;

import org.kie.kogito.correlation.Correlation;
import org.kie.kogito.correlation.CorrelationEncoder;
import org.kie.kogito.correlation.CorrelationInstance;
import org.kie.kogito.correlation.CorrelationService;
import org.kie.kogito.event.correlation.MD5CorrelationEncoder;

public class MongoDBCorrelationService implements CorrelationService {

    private final MongoDBCorrelationRepository correlationRepository;
    private final CorrelationEncoder correlationEncoder;

    public MongoDBCorrelationService(MongoDBCorrelationRepository correlationRepository) {
        this.correlationRepository = correlationRepository;
        this.correlationEncoder = new MD5CorrelationEncoder();
    }

    @Override
    public CorrelationInstance create(Correlation correlation, String correlatedId) {
        String encodedCorrelationId = this.correlationEncoder.encode(correlation);
        return this.correlationRepository.insert(encodedCorrelationId, correlatedId, correlation);
    }

    @Override
    public Optional<CorrelationInstance> find(Correlation correlation) {
        String encodedCorrelationId = correlationEncoder.encode(correlation);
        return Optional.ofNullable(this.correlationRepository.findByEncodedCorrelationId(encodedCorrelationId));
    }

    @Override
    public Optional<CorrelationInstance> findByCorrelatedId(String correlatedId) {
        return Optional.ofNullable(this.correlationRepository.findByCorrelatedId(correlatedId));
    }

    @Override
    public void delete(Correlation correlation) {
        String encodedCorrelationId = correlationEncoder.encode(correlation);
        this.correlationRepository.delete(encodedCorrelationId);
    }
}
