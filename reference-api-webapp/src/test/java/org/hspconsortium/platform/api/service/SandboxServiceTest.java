package org.hspconsortium.platform.api.service;

import org.hspconsortium.platform.api.persister.SandboxPersister;
import org.hspconsortium.platform.api.security.TenantInfoRequestMatcher;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class SandboxServiceTest {

    private SandboxPersister sandboxPersister = mock(SandboxPersister.class);
    private TenantInfoRequestMatcher tenantInfoRequestMatcher = mock(TenantInfoRequestMatcher.class);
    private RestTemplate restTemplate = mock(RestTemplate.class);

    private SandboxServiceImpl sandboxService = new SandboxServiceImpl(sandboxPersister, tenantInfoRequestMatcher, restTemplate);

    @Before
    public void setup() {

    }

//    @Test
//    public void resetTest() {
//        sandboxService.reset();
//        verify(tenantInfoRequestMatcher).reset();
//    }
}