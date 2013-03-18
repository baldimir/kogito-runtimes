/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.internal.task.api;

import javax.enterprise.event.Event;

import org.kie.internal.task.api.model.NotificationEvent;
import org.kie.internal.task.api.model.Task;

public interface EventService<T, V> {

    void registerTaskLifecycleEventListener(V taskLifecycleEventListener);
    
    void registerTaskNotificationEventListener(T notificationEventListener);

    Event<Task> getTaskLifecycleEventListeners();

    Event<NotificationEvent> getTaskNotificationEventListeners();

    void clearTaskLifecycleEventListeners();

    void clearTasknotificationEventListeners(); 
}
