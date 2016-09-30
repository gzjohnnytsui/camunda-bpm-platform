/*
 * Copyright 2016 camunda services GmbH.
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
package org.camunda.bpm.engine.impl.bpmn.behavior;

import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.impl.bpmn.parser.ConditionalEventDefinition;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.pvm.runtime.PvmExecutionImpl;

/**
 *
 * @author Christopher Zell <christopher.zell@camunda.com>
 */
public class BoundaryConditionalEventActivityBehavior extends BoundaryEventActivityBehavior implements ConditionalEventBehavioral {

  private final ConditionalEventDefinition conditionalEvent;

  public BoundaryConditionalEventActivityBehavior(ConditionalEventDefinition conditionalEvent) {
    this.conditionalEvent = conditionalEvent;
  }

  @Override
  public void leaveOnSatisfiedCondition(final EventSubscriptionEntity eventSubscription, final VariableScope scope, final CommandContext commandContext) {
    PvmExecutionImpl execution = eventSubscription.getExecution();
    if (!execution.isEnded() && conditionalEvent.tryEvaluate(scope, execution)) {
      if (execution.isActive() && execution.isScope()) {
        execution.executeActivity(eventSubscription.getActivity());
      }
    }
  }
}
