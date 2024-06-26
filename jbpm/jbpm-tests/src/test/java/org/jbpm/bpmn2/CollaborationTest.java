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
package org.jbpm.bpmn2;

import java.util.Collections;

import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CollaborationTest extends JbpmBpmn2TestCase {

    @Test
    public void testBoundaryMessageCollaboration() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/collaboration/BPMN2-CollaborationBoundaryMessage.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        KogitoProcessInstance pid = kruntime.startProcess("CollaborationBoundaryMessage", Collections.singletonMap("MessageId", "2"));
        kruntime.signalEvent("Message-collaboration", new Message("1", "example"), pid.getStringId());
        assertProcessInstanceActive(pid);
        kruntime.signalEvent("Message-collaboration", new Message("2", "example"), pid.getStringId());
        assertProcessInstanceCompleted(pid);
    }

    @Test
    public void testStartMessageCollaboration() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/collaboration/BPMN2-CollaborationStartMessage.bpmn2");
        kruntime.signalEvent("Message-collaboration", new Message("1", "example"));
        assertThat(getNumberOfProcessInstances("CollaborationStartMessage")).isEqualTo(1);
    }

    @Test
    public void testStartMessageCollaborationNoMatch() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/collaboration/BPMN2-CollaborationStartMessage.bpmn2");

        kruntime.signalEvent("Message-collaboration", new Message("2", "example"));
        assertThat(getNumberOfProcessInstances("CollaborationStartMessage")).isZero();
    }

    @Test
    public void testIntermediateMessageCollaboration() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/collaboration/BPMN2-CollaborationIntermediateMessage.bpmn2");
        KogitoProcessInstance pid = kruntime.startProcess("CollaborationIntermediateMessage", Collections.singletonMap("MessageId", "2"));
        kruntime.signalEvent("Message-collaboration", new Message("1", "example"), pid.getStringId());
        assertProcessInstanceActive(pid);
        kruntime.signalEvent("Message-collaboration", new Message("2", "example"), pid.getStringId());
        assertProcessInstanceCompleted(pid);
    }

    @Test
    public void testInvalidIntermediateMessageCollaboration() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/collaboration/BPMN2-CollaborationIntermediateMessage.bpmn2");

        KogitoProcessInstance pid = kruntime.startProcess("CollaborationIntermediateMessage", Collections.singletonMap("MessageId", "2"));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            kruntime.signalEvent("Message-collaboration", new Message(null, "example"), pid.getStringId());
        });

        assertProcessInstanceActive(pid);
    }
}
