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
package org.jbpm.ruleflow.core.factory;

import java.util.List;

import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DataAssociation;
import org.jbpm.workflow.core.node.EventTrigger;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.api.definition.process.WorkflowElementIdentifier;

public class StartNodeFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends NodeFactory<StartNodeFactory<T>, T> {

    public static final String METHOD_INTERRUPTING = "interrupting";
    public static final String METHOD_TRIGGER = "trigger";
    public static final String METHOD_TIMER = "timer";

    public StartNodeFactory(T nodeContainerFactory, NodeContainer nodeContainer, WorkflowElementIdentifier id) {
        super(nodeContainerFactory, nodeContainer, new StartNode(), id);
    }

    protected StartNode getStartNode() {
        return (StartNode) getNode();
    }

    public StartNodeFactory<T> interrupting(boolean interrupting) {
        getStartNode().setInterrupting(interrupting);
        return this;
    }

    public StartNodeFactory<T> trigger(String triggerEventType, List<DataAssociation> dataAssociations) {
        EventTrigger trigger = new EventTrigger();
        EventTypeFilter eventFilter = new EventTypeFilter();
        eventFilter.setType(triggerEventType);
        trigger.addEventFilter(eventFilter);
        dataAssociations.forEach(trigger::addInAssociation);
        getStartNode().addTrigger(trigger);
        return this;
    }

    public StartNodeFactory<T> trigger(String triggerEventType, String target) {
        return trigger(triggerEventType, target, target);
    }

    public StartNodeFactory<T> trigger(String triggerEventType, String source, String target) {
        EventTrigger trigger = new EventTrigger();
        EventTypeFilter eventFilter = new EventTypeFilter();
        eventFilter.setType(triggerEventType);
        trigger.addEventFilter(eventFilter);
        if (source != null) {
            trigger.addInMapping(source, target);
        }
        getStartNode().addTrigger(trigger);
        return this;
    }

    public StartNodeFactory<T> timer(String delay, String period, String date, int timeType) {
        Timer timer = new Timer();
        timer.setDate(date);
        timer.setDelay(delay);
        timer.setPeriod(period);
        timer.setTimeType(timeType);

        getStartNode().setTimer(timer);
        return this;
    }
}
