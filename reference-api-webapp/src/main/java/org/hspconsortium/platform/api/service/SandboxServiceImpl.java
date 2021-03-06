/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
 *  * %%
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 */

package org.hspconsortium.platform.api.service;

import org.apache.commons.lang3.Validate;
import org.hspconsortium.platform.api.DatabaseProperties;
import org.hspconsortium.platform.api.model.DataSet;
import org.hspconsortium.platform.api.model.Sandbox;
import org.hspconsortium.platform.api.multitenant.db.SandboxPersister;
import org.hspconsortium.platform.api.multitenant.db.SchemaNotInitializedException;
import org.hspconsortium.platform.api.multitenant.TenantInfoRequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Component
public class SandboxServiceImpl implements SandboxService {
    private static final Logger logger = LoggerFactory.getLogger(SandboxServiceImpl.class);

    @Value("${hspc.platform.api.sandboxManagerApi.url}")
    private String sandboxManagerApiUrl;

    @Value("${hspc.platform.api.sandboxManagerApi.userAuthPath}")
    private String userAuthPath;

    private SandboxPersister sandboxPersister;

    private TenantInfoRequestMatcher tenantInfoRequestMatcher;

    private RestTemplate restTemplate;

    @Autowired
    public SandboxServiceImpl(SandboxPersister sandboxPersister, TenantInfoRequestMatcher tenantInfoRequestMatcher,
                              RestTemplate restTemplate) {
        this.sandboxPersister = sandboxPersister;
        this.tenantInfoRequestMatcher = tenantInfoRequestMatcher;
        this.restTemplate = restTemplate;
    }

    @Override
    public void reset() {
        tenantInfoRequestMatcher.reset();
        logger.info("Sandbox Service reset");
    }

    @Override
    public Collection<String> allTenantNames() {
        return sandboxPersister.getSandboxNames();
    }

    @Override
    public Collection<Sandbox> allSandboxes() {
        return sandboxPersister.getSandboxes();
    }

    @Override
    public Sandbox save(@NotNull Sandbox sandbox, @NotNull DataSet dataSet) {
        logger.info("Saving sandbox: " + sandbox);
        Validate.notNull(sandbox, "Sandbox must be provided");
        Validate.notNull(sandbox.getTeamId(), "Sandbox.teamId must be provided");

        sandbox.setSchemaVersion(DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION);


        Sandbox existing = checkIfTenantNameIsUnique(sandbox);

        // save the sandbox info
        Sandbox saved = sandboxPersister.saveSandbox(sandbox);
        logger.info("Saved sandbox: " + saved);

        logger.info("useStarterData: " + dataSet);
        if (existing == null) {
            sandboxPersister.loadInitialDataset(sandbox, dataSet);
        }

        // Make sure the initial data set didn't replace the sandbox info
        saved = sandboxPersister.saveSandbox(sandbox);
        logger.info("Saved sandbox: " + saved);

        // update security
        if (sandbox.isAllowOpenAccess()) {
            tenantInfoRequestMatcher.addOpenTeamId(saved.getTeamId());
        } else {
            tenantInfoRequestMatcher.removeOpenTeamId(saved.getTeamId());
        }

        return saved;
    }

    @Override
    public void clone(@NotNull Sandbox newSandbox, @NotNull Sandbox clonedSandbox) {
        logger.info("Cloning sandbox " + clonedSandbox.getTeamId() + " to sandbox: " + newSandbox.getTeamId());
        Validate.notNull(newSandbox, "New sandbox must be provided");
        Validate.notNull(newSandbox.getTeamId(), "New sandbox.teamId must be provided");
        Validate.notNull(clonedSandbox, "Cloned sandbox must be provided");
        Validate.notNull(clonedSandbox.getTeamId(), "Cloned sandbox.teamId must be provided");

        newSandbox.setSchemaVersion(DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION);
        clonedSandbox.setSchemaVersion(DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION);

        Sandbox existing = checkIfTenantNameIsUnique(newSandbox);

        if (existing == null) {
            sandboxPersister.cloneSandbox(newSandbox, clonedSandbox);
            if (newSandbox.isAllowOpenAccess()) {
                tenantInfoRequestMatcher.addOpenTeamId(newSandbox.getTeamId());
            } else {
                tenantInfoRequestMatcher.removeOpenTeamId(newSandbox.getTeamId());
            }
            return;
        }

        if (newSandbox.isAllowOpenAccess()) {
            tenantInfoRequestMatcher.addOpenTeamId(newSandbox.getTeamId());
        } else {
            tenantInfoRequestMatcher.removeOpenTeamId(newSandbox.getTeamId());
        }

        throw new IllegalArgumentException("The new sandbox already exists");
    }

    @Override
    public Sandbox get(String teamId) {
        Sandbox sandbox;
        try {
            sandbox = sandboxPersister.findSandbox(teamId);
        } catch (SchemaNotInitializedException e) {
            sandbox = save(SandboxPersister.sandboxTemplate().setTeamId(teamId), DataSet.DEFAULT);
        }

        return sandbox;
    }

    @Override
    public boolean remove(String teamId) {
        Sandbox existing = get(teamId);
        return (existing == null) || delete(existing);
    }

    private boolean delete(Sandbox existing) {
        if (existing != null) {
            boolean success = sandboxPersister.removeSandbox(existing.getSchemaVersion(), existing.getTeamId());
            // update security
            tenantInfoRequestMatcher.removeOpenTeamId(existing.getTeamId());
            return success;
        } else {
            return true;
        }
    }

    @Override
    public Sandbox reset(String teamId, DataSet dataSet) {
        Sandbox existing = get(teamId);

        if (existing == null) {
            throw new RuntimeException("Unable to reset sandbox because sandbox does not exist: [" + teamId + "]");
        } else {
            boolean deleted = delete(existing);
            if (!deleted) {
                throw new RuntimeException("Unable to reset sandbox because existing could not be deleted: [" + teamId + "]");
            }
        }

        return save(SandboxPersister.sandboxTemplate().setTeamId(teamId), dataSet);
    }

    private Sandbox checkIfTenantNameIsUnique(Sandbox sandbox) {
        try {
            Sandbox existing = sandboxPersister.findSandbox(sandbox.getTeamId());
            logger.info("Existing sandbox: " + existing);
            if (existing == null) {
                // check that the sandbox is unique across versions
                if (!sandboxPersister.isTeamIdUnique(sandbox.getTeamId())) {
                    throw new RuntimeException("TeamId [" + sandbox.getTeamId() + "] is not unique");
                }
            }
            return existing;
        } catch (SchemaNotInitializedException e) {
            logger.info("SchemaNotInitializedException ignored for now");
            // ignore, will be fixed when saving
        }
        return null;
    }

    @Override
    public boolean verifyUser(HttpServletRequest request, String sandboxId) {
        String authToken = getBearerToken(request);
        if (authToken == null) {
            return false;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "BEARER " + authToken);

        String jsonBody = "{\"sandbox\": \""+ sandboxId + "\"}";

        HttpEntity entity = new HttpEntity(jsonBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(this.sandboxManagerApiUrl + this.userAuthPath, HttpMethod.POST, entity, String.class);
            return true;
        } catch (HttpClientErrorException e) {
            return false;
        }

    }

    private String getBearerToken(HttpServletRequest request) {

        String authToken = request.getHeader("Authorization");
        if (authToken == null) {
            return null;
        }
        return authToken.substring(7);
    }


}
