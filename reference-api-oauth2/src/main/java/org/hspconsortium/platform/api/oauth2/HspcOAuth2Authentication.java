package org.hspconsortium.platform.api.oauth2;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.Map;

public class HspcOAuth2Authentication extends OAuth2Authentication {

    Map<String, String> launchContextParams;

    public HspcOAuth2Authentication(OAuth2Request storedRequest, Authentication userAuthentication) {
        super(storedRequest, userAuthentication);
    }

    public Map<String, String> getLaunchContextParams() {
        return launchContextParams;
    }

    public void setLaunchContextParams(Map<String, String> launchContextParams) {
        this.launchContextParams = launchContextParams;
    }
}
