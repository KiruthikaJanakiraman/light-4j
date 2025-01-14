/*
 * Copyright (c) 2016 Network New Technologies Inc.
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

package com.networknt.client.oauth;

import com.networknt.client.ClientConfig;
import com.networknt.status.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DerefRequest {
    private static final Logger logger = LoggerFactory.getLogger(DerefRequest.class);
    private static final String CONFIG_PROPERTY_MISSING = "ERR10057";

    private String serverUrl;
    private String proxyHost;
    private int proxyPort;
    private String serviceId;
    private String uri;
    private String clientId;
    private String clientSecret;
    private boolean enableHttp2;

    public DerefRequest(String token) {
        Map<String, Object> derefConfig = ClientConfig.get().getDerefConfig();
        if(derefConfig != null) {
            setServerUrl((String)derefConfig.get(ClientConfig.SERVER_URL));
            setProxyHost((String)derefConfig.get(ClientConfig.PROXY_HOST));
            int port = derefConfig.get(ClientConfig.PROXY_PORT) == null ? 443 : (Integer)derefConfig.get(ClientConfig.PROXY_PORT);
            setProxyPort(port);
            setServiceId((String)derefConfig.get(ClientConfig.SERVICE_ID));
            Object object = derefConfig.get(ClientConfig.ENABLE_HTTP2);
            setEnableHttp2(object != null && (Boolean) object);
            setUri(derefConfig.get(ClientConfig.URI) + "/" + token);
            setClientId((String)derefConfig.get(ClientConfig.CLIENT_ID));
            if(derefConfig.get(ClientConfig.CLIENT_SECRET) != null) {
                setClientSecret((String)derefConfig.get(ClientConfig.CLIENT_SECRET));
            } else {
                logger.error(new Status(CONFIG_PROPERTY_MISSING, "deref client_secret", "client.yml").toString());
            }
        }
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public boolean isEnableHttp2() { return enableHttp2; }

    public void setEnableHttp2(boolean enableHttp2) { this.enableHttp2 = enableHttp2; }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
}
