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
package org.jbpm.test.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCountDownProcessEventListener extends DefaultKogitoProcessEventListener {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCountDownProcessEventListener.class);

    protected CountDownLatch latch;

    public DefaultCountDownProcessEventListener() {

    }

    public DefaultCountDownProcessEventListener(int threads) {
        this.latch = new CountDownLatch(threads);
    }

    public boolean waitTillCompleted() {
        return waitTillCompleted(10000);
    }

    public boolean await() {
        try {
            latch.await();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrputed thread while waiting for all triggers", e);
            return false;
        } catch (Exception e) {
            logger.error("Error during waiting state", e);
            return false;
        }
    }

    public boolean waitTillCompleted(long timeOut) {
        try {
            return latch.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrputed thread while waiting for all triggers", e);
            return false;
        } catch (Exception e) {
            logger.error("Error during waiting state", e);
            return false;
        }
    }

    public void reset(int threads) {
        this.latch = new CountDownLatch(threads);
    }

    protected void countDown() {
        latch.countDown();
    }
}
