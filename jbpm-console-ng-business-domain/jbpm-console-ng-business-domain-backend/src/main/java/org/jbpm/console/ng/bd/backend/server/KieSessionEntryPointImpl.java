/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.console.ng.bd.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.core.command.runtime.process.SetProcessInstanceVariablesCommand;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.backend.server.ProcessInstanceHelper;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.deployment.DeploymentService;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

@Service
@ApplicationScoped
public class KieSessionEntryPointImpl implements KieSessionEntryPoint {

    @Inject
    private DeploymentService deploymentService;

    @Inject
    private RuntimeDataService dataService;

    @Override
    public long startProcess(String domainName, String processId) {
        RuntimeManager runtimesByDomain = deploymentService.getDeployedUnit(domainName).getRuntimeManager();
        RuntimeEngine engine = runtimesByDomain.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = engine.getKieSession();
        ProcessInstance pi = null;
        try {
            pi = ksession.startProcess(processId);
        } catch (Exception e) {
            ExceptionUtilities.handleException(e);
        }  finally {
            runtimesByDomain.disposeRuntimeEngine(engine);
        }

        return pi.getId();
    }

    @Override
    public long startProcess(String domainName, String processId, Map<String, String> params) {
        RuntimeManager runtimesByDomain = deploymentService.getDeployedUnit(domainName).getRuntimeManager();
        RuntimeEngine engine = runtimesByDomain.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = engine.getKieSession();
        ProcessInstance pi = null;
        try {
            pi = ksession.startProcess(processId, new HashMap<String, Object>(params));
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        } finally {
            runtimesByDomain.disposeRuntimeEngine(engine);
        }
        return pi.getId();
    }

    @Override
    public void abortProcessInstance(long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        RuntimeManager runtimesByDomain = deploymentService.getDeployedUnit(piDesc.getDeploymentId()).getRuntimeManager();
        RuntimeEngine engine = runtimesByDomain.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        KieSession ksession = engine.getKieSession();
        try {
            ksession.abortProcessInstance(processInstanceId);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        } finally {
            runtimesByDomain.disposeRuntimeEngine(engine);
        }
    }

    @Override
    public void abortProcessInstances(List<Long> processInstanceIds) {
        for (long processInstanceId : processInstanceIds) {
            ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
            RuntimeManager runtimesByDomain = deploymentService.getDeployedUnit(piDesc.getDeploymentId()).getRuntimeManager();
            RuntimeEngine engine = runtimesByDomain.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
            KieSession ksession = engine.getKieSession();
            try {
                ksession.abortProcessInstance(processInstanceId);
            } catch (Exception e) {
               throw ExceptionUtilities.handleException(e);
            } finally {
                runtimesByDomain.disposeRuntimeEngine(engine);
            }
        }
    }

    @Override
    public void suspendProcessInstance(long processInstanceId) {
        // ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        // RuntimeManager runtimesByDomain = deploymentService.getDeployedUnit(piDesc.getDeploymentId()).getRuntimeManager();

        // KieSession ksession = runtimesByDomain.getRuntime(ProcessInstanceIdContext.get(processInstanceId)).getKieSession();
        // ksession.abortProcessInstance(processInstanceId);

    }

    @Override
    public void signalProcessInstance(long processInstanceId, String signalName, Object event) {

        if (processInstanceId != -1) {
            ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
            RuntimeManager runtimesByDomain = deploymentService.getDeployedUnit(piDesc.getDeploymentId()).getRuntimeManager();
            RuntimeEngine engine = runtimesByDomain.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
            KieSession ksession = engine.getKieSession();

            try {
                ksession.signalEvent(signalName, event, processInstanceId);
            } catch (Exception e) {
               throw ExceptionUtilities.handleException(e);
            } finally {
                runtimesByDomain.disposeRuntimeEngine(engine);
            }
        }

    }

    @Override
    public void signalProcessInstances(List<Long> processInstanceIds, String signalName, Object event) {
        for (Long processInstanceId : processInstanceIds) {
            if (processInstanceId != -1) {
                ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
                RuntimeManager runtimesByDomain = deploymentService.getDeployedUnit(piDesc.getDeploymentId()).getRuntimeManager();
                RuntimeEngine engine = runtimesByDomain.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
                KieSession ksession = engine.getKieSession();
                try {
                    ksession.signalEvent(signalName, event, processInstanceId);
                } catch (Exception e) {
                    throw ExceptionUtilities.handleException(e);
                } finally {
                    runtimesByDomain.disposeRuntimeEngine(engine);
                }

            }
        }

    }

    @Override
    public Collection<String> getAvailableSignals(long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        RuntimeManager runtimesByDomain = deploymentService.getDeployedUnit(piDesc.getDeploymentId()).getRuntimeManager();
        RuntimeEngine engine = runtimesByDomain.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        KieSession ksession = engine.getKieSession();
        try {
            ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
            Collection<String> activeSignals = new ArrayList<String>();

            if (processInstance != null) {
                ((ProcessInstanceImpl) processInstance)
                        .setProcess(ksession.getKieBase().getProcess(processInstance.getProcessId()));
                Collection<NodeInstance> activeNodes = ((WorkflowProcessInstance) processInstance).getNodeInstances();

                activeSignals.addAll(ProcessInstanceHelper.collectActiveSignals(activeNodes));
            }

            return activeSignals;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        } finally {
            runtimesByDomain.disposeRuntimeEngine(engine);
        }

    }

    @Override
    public void setProcessVariable(long processInstanceId, String variableId, Object value) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        RuntimeManager runtimesByDomain = deploymentService.getDeployedUnit(piDesc.getDeploymentId()).getRuntimeManager();
        RuntimeEngine engine = runtimesByDomain.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        KieSession ksession = engine.getKieSession();

        try {
            ksession.execute(new SetProcessInstanceVariablesCommand(processInstanceId, Collections.singletonMap(variableId, value)));
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        } finally {
            runtimesByDomain.disposeRuntimeEngine(engine);
        }

    }

}
