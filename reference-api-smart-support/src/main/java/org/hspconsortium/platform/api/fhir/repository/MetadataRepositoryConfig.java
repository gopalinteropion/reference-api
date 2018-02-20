package org.hspconsortium.platform.api.fhir.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class MetadataRepositoryConfig {
    static protected String SECURE_MODE = "secured";

    static protected String SECURE_MODE_MOCK = "mock";

    @Value("${hspc.platform.api.security.mode}")
    private String securityMode;

    @Value("${hspc.platform.authorization.url}")
    private String authorizationUrl;

    @Value("${hspc.platform.manifest.override}")
    private boolean manifestOverride;

    @Value("${hspc.platform.manifest.url}")
    private String manifestAuthorizationUrl;

    @Value("${hspc.platform.authorization.authorizeUrlPath}")
    private String authorizeUrlPath;

    @Value("${hspc.platform.authorization.tokenUrlPath}")
    private String tokenUrlPath;

    @Value("${hspc.platform.authorization.tokenCheckUrlPath}")
    private String tokenCheckUrlPath;

    @Value("${hspc.platform.authorization.smart.launchUrlPath}")
    private String launchUrlPath;

    @Value("${hspc.platform.authorization.smart.registrationEndpointUrlPath}")
    private String registrationEndpointUrlPath;

    @Value("${hspc.platform.authorization.smart.urisEndpointExtensionUrl}")
    private String urisEndpointExtensionUrl;

    @Value("${hspc.platform.authorization.smart.launchRegistrationUrl}")
    private String launchRegistrationUrl;

    public String getSecurityMode() {
        return securityMode;
    }

    public String getBaseUrl() {
        return authorizationUrl;
    }

    public boolean isManifestOverride() {
        return manifestOverride;
    }

    public String getAuthorizeUrlPath() {
        return authorizeUrlPath;
    }

    public String getTokenUrlPath() {
        return tokenUrlPath;
    }

    public String getTokenCheckUrlPath() {
        return tokenCheckUrlPath;
    }

    public String getLaunchUrlPath() {
        return launchUrlPath;
    }

    public String getRegistrationEndpointUrlPath() {
        return registrationEndpointUrlPath;
    }

    public String getUrisEndpointExtensionUrl() {
        return urisEndpointExtensionUrl;
    }

    public String getLaunchRegistrationUrl() {
        return launchRegistrationUrl;
    }

    public boolean isSecured() {
        return SECURE_MODE.equalsIgnoreCase(getSecurityMode()) || SECURE_MODE_MOCK.equalsIgnoreCase(getSecurityMode());
    }

    public String getBasePath() {
        return (isManifestOverride() ? manifestAuthorizationUrl : authorizationUrl);
    }

    public String getAuthorizeUrl() {
        return getBasePath() + getAuthorizeUrlPath();
    }

    public String getTokenUrl() {
        return getBasePath() + getTokenUrlPath();
    }

    public String getTokenCheckUrl() {
        // always use the authorization url
        return authorizationUrl + getTokenCheckUrlPath();
    }

    public String getRegistrationEndpointUrl() {
        return getBasePath() + getRegistrationEndpointUrlPath();
    }

    public String getLaunchUrl() {
        // always use the authorization url
        return authorizationUrl + getLaunchUrlPath();
    }
}