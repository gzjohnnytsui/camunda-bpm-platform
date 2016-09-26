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
package org.camunda.bpm.engine.test.bpmn.event.conditional;

import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;

/**
 *
 * @author Christopher Zell <christopher.zell@camunda.com>
 */
public class BoundaryConditionalEventTest extends AbstractConditionalEventTestCase {

  protected static final String TASK_WITH_CONDITION = "Task with condition";
  protected static final String TASK_WITH_CONDITION_ID = "taskWithCondition";

  @Test
  @Deployment
  public void testTrueCondition() {
    //given process with boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInst.getId());
    Task task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_WITH_CONDITION, task.getName());

    //when variable is set on task with condition
    taskService.setVariable(task.getId(), VARIABLE_NAME, 1);

    //then execution is at user task after boundary event
    task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_AFTER_CONDITION, task.getName());
  }

  @Test
  @Deployment
  public void testNonInterruptingTrueCondition() {
    //given process with boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInst.getId());
    Task task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_WITH_CONDITION, task.getName());

    //when variable is set on task with condition
    taskService.setVariable(task.getId(), VARIABLE_NAME, 1);

    //then execution is at user task after boundary event and in the task with the boundary event
    List<Task> tasklist = taskQuery.list();
    assertEquals(2, tasklist.size());
  }

  @Test
  @Deployment
  public void testFalseCondition() {
    //given process with boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery();
    Task task = taskQuery.processInstanceId(procInst.getId()).singleResult();
    assertNotNull(task);
    assertEquals(TASK_WITH_CONDITION, task.getName());

    //when variable is set on task with condition
    taskService.setVariable(task.getId(), VARIABLE_NAME, 1);

    //then execution stays in task with boundary condition
    Execution execution = runtimeService.createExecutionQuery()
             .processInstanceId(procInst.getId())
             .activityId(TASK_WITH_CONDITION_ID)
             .singleResult();
    assertNotNull(execution);
  }

  @Test
  @Deployment
  public void testVariableCondition() {
    //given process with boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInst.getId());
    Task task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_WITH_CONDITION, task.getName());

    //when local variable is set on task with condition
    taskService.setVariableLocal(task.getId(), VARIABLE_NAME, 1);

    //then execution is at user task after boundary event
    task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_AFTER_CONDITION, task.getName());
  }

  @Test
  @Deployment
  public void testNonInterruptingVariableCondition() {
    //given process with boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInst.getId());
    Task task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_WITH_CONDITION, task.getName());

    //when local variable is set on task with condition
    taskService.setVariableLocal(task.getId(), VARIABLE_NAME, 1);

    //then execution is at user task after boundary event and in the task with the boundary event
    List<Task> tasklist = taskQuery.list();
    assertEquals(2, tasklist.size());
  }


  @Test
  @Deployment(resources ={ "org/camunda/bpm/engine/test/bpmn/event/conditional/BoundaryConditionalEventTest.testVariableCondition.bpmn20.xml"})
  public void testWrongVariableCondition() {
    //given process with boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInst.getId());
    Task task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_WITH_CONDITION, task.getName());

    //when wrong variable is set on task with condition
    taskService.setVariableLocal(task.getId(), VARIABLE_NAME + 1, 1);

    //then execution stays at user task with condition
    task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_WITH_CONDITION, task.getName());

    //when correct variable is set
    taskService.setVariableLocal(task.getId(), VARIABLE_NAME, 1);

    //then execution is on user task after condition
    task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_AFTER_CONDITION, task.getName());
  }


  @Test
  @Deployment(resources ={ "org/camunda/bpm/engine/test/bpmn/event/conditional/BoundaryConditionalEventTest.testVariableCondition.bpmn20.xml"})
  public void testSetVariableOnTaskCondition() {
    //given process with boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInst.getId());
    Task task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_WITH_CONDITION, task.getName());

    //when variable is set on task with condition
    taskService.setVariable(task.getId(), VARIABLE_NAME, 1);

    //then execution is at user task after boundary event
    task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_AFTER_CONDITION, task.getName());
  }

  @Test
  @Deployment(resources ={ "org/camunda/bpm/engine/test/bpmn/event/conditional/BoundaryConditionalEventTest.testNonInterruptingVariableCondition.bpmn20.xml"})
  public void testNonInterruptingSetVariableOnTaskCondition() {
    //given process with boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInst.getId());
    Task task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_WITH_CONDITION, task.getName());

    //when variable is set on task with condition
    taskService.setVariable(task.getId(), VARIABLE_NAME, 1);

    //then execution is at user task after boundary event and in the task with the boundary event
    List<Task> tasklist = taskQuery.list();
    assertEquals(2, tasklist.size());
  }


  @Test
  @Deployment(resources ={ "org/camunda/bpm/engine/test/bpmn/event/conditional/BoundaryConditionalEventTest.testVariableCondition.bpmn20.xml"})
  public void testSetVariableOnExecutionCondition() {
    //given process with boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInst.getId());
    Task task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_WITH_CONDITION, task.getName());

    //when variable is set on execution with condition
    runtimeService.setVariable(procInst.getId(), VARIABLE_NAME, 1);

    //then execution is at user task after boundary event
    task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_AFTER_CONDITION, task.getName());
  }

  @Test
  @Deployment(resources ={ "org/camunda/bpm/engine/test/bpmn/event/conditional/BoundaryConditionalEventTest.testNonInterruptingVariableCondition.bpmn20.xml"})
  public void testNonInterruptingSetVariableOnExecutionCondition() {
    //given process with boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInst.getId());
    Task task = taskQuery.singleResult();
    assertNotNull(task);
    assertEquals(TASK_WITH_CONDITION, task.getName());

    //when variable is set on execution with condition
    runtimeService.setVariable(procInst.getId(), VARIABLE_NAME, 1);

    //then execution is at user task after boundary event and in the task with the boundary event
    List<Task> tasklist = taskQuery.list();
    assertEquals(2, tasklist.size());
  }

  @Test
  @Deployment
  public void testParallelVariableCondition() {
    //given process with parallel user tasks and boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInst.getId());
    List<Task> tasks = taskQuery.list();
    assertEquals(2, tasks.size());

    Task task = tasks.get(0);

    //when local variable is set on task with condition
    taskService.setVariableLocal(task.getId(), VARIABLE_NAME, 1);

    //then boundary event is triggered of this task and task ends
    //other execution stays in other task
    List<Execution> executions = runtimeService.createExecutionQuery()
             .processInstanceId(procInst.getId())
             .list();
    assertEquals(2, executions.size());

    tasks = taskQuery.list();
    assertEquals(1, tasks.size());

    Execution execution = runtimeService.createExecutionQuery()
             .processInstanceId(procInst.getId())
             .activityId(task.getId())
             .singleResult();
    assertNull(execution);
  }

  @Test
  @Deployment(resources ={ "org/camunda/bpm/engine/test/bpmn/event/conditional/BoundaryConditionalEventTest.testParallelVariableCondition.bpmn20.xml"})
  public void testParallelSetVariableOnTaskCondition() {
    //given process with parallel user tasks and boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInst.getId());
    List<Task> tasks = taskQuery.list();
    assertEquals(2, tasks.size());

    Task task = tasks.get(0);

    //when variable is set on execution
    taskService.setVariable(task.getId(), VARIABLE_NAME, 1);

    //then both boundary event are triggered and process instance ends
    List<Execution> executions = runtimeService.createExecutionQuery()
             .processInstanceId(procInst.getId())
             .list();
    assertEquals(0, executions.size());

    tasks = taskQuery.list();
    assertEquals(0, tasks.size());
  }

  @Test
  @Deployment(resources ={ "org/camunda/bpm/engine/test/bpmn/event/conditional/BoundaryConditionalEventTest.testParallelVariableCondition.bpmn20.xml"})
  public void testParallelSetVariableOnExecutionCondition() {
    //given process with parallel user tasks and boundary conditional event
    ProcessInstance procInst = runtimeService.startProcessInstanceByKey(CONDITIONAL_EVENT_PROCESS_KEY);

    TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInst.getId());
    List<Task> tasks = taskQuery.list();
    assertEquals(2, tasks.size());

    //when variable is set on execution
    //taskService.setVariable(task.getId(), VARIABLE_NAME, 1);
    runtimeService.setVariable(procInst.getId(), VARIABLE_NAME, 1);

    //then both boundary events are triggered and process instance ends
    List<Execution> executions = runtimeService.createExecutionQuery()
             .processInstanceId(procInst.getId())
             .list();
    assertEquals(0, executions.size());

    tasks = taskQuery.list();
    assertEquals(0, tasks.size());
  }
}
