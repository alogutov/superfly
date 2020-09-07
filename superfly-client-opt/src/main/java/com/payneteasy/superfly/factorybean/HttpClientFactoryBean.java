package com.payneteasy.superfly.factorybean;

import com.payneteasy.httpclient.contrib.ssl.AuthSSLProtocolSocketFactory;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HostParams;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.ssl.HttpSecureProtocol;
import org.springframework.beans.factory.FactoryBean;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory bean for HttpClient.
 * 
 * @author Roman Puchkovskiy
 */
public class HttpClientFactoryBean implements FactoryBean {

    private boolean authenticationPreemptive = false;
    private String username;
    private String password;
    private String authHost;
    private StoresAndSSLConfig hostConfig = null;
    private long connectionManagerTimeout = 10000; // 10 seconds by default
    private int soTimeout = 10000; // 10 seconds by default
    private int connectionTimeout = -1; // negative values mean "leave existing value"
    private HttpConnectionManager httpConnectionManager = null;
    private String[] sslEnabledProtocols = null;

    private HttpClient httpClient = null;

    public boolean isAuthenticationPreemptive() {
        return authenticationPreemptive;
    }

    public void setAuthenticationPreemptive(boolean authenticationPreemptive) {
        this.authenticationPreemptive = authenticationPreemptive;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthHost() {
        return authHost;
    }

    public void setAuthHost(String authHost) {
        this.authHost = authHost;
    }

    public void setHostConfig(StoresAndSSLConfig hostConfig) {
        this.hostConfig = hostConfig;
    }

    public void setConnectionManagerTimeout(long connectionManagerTimeout) {
        this.connectionManagerTimeout = connectionManagerTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public HttpConnectionManager getHttpConnectionManager() {
        return httpConnectionManager;
    }

    public void setHttpConnectionManager(HttpConnectionManager httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
    }

    public void setSslEnabledProtocols(String[] sslEnabledProtocols) {
        this.sslEnabledProtocols = sslEnabledProtocols;
    }

    public synchronized Object getObject() throws Exception {
        if (httpClient == null) {
            createAndConfigureHttpClient();
        }
        return httpClient;
    }

    protected void createAndConfigureHttpClient() throws IOException, GeneralSecurityException {
        httpClient = createHttpClient();
        configureHttpClient();
    }

    protected void configureHttpClient() throws IOException, GeneralSecurityException {
        httpClient.getParams().setAuthenticationPreemptive(isAuthenticationPreemptive());
        initCredentials();
        initSocketFactory();
        initProtocolIfNeeded();
        if (httpConnectionManager != null) {
            httpClient.setHttpConnectionManager(httpConnectionManager);
        }

        List<Header> headers = getDefaultHeaders();

        httpClient.getHostConfiguration().getParams().setParameter(HostParams.DEFAULT_HEADERS, headers);
        httpClient.getParams().setParameter(HttpClientParams.USER_AGENT,
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.19 (KHTML, like Gecko) Ubuntu/11.04 Chromium/18.0.1025.151 Chrome/18.0.1025.151 Safari/535.19");
        httpClient.getParams().setParameter(HttpClientParams.HTTP_CONTENT_CHARSET, "UTF-8");
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

        httpClient.getParams().setConnectionManagerTimeout(connectionManagerTimeout);
        httpClient.getParams().setSoTimeout(soTimeout);
        if (connectionTimeout >= 0) {
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
        }
    }

    protected void initProtocolIfNeeded() throws GeneralSecurityException, IOException {
        if (sslEnabledProtocols != null) {
            // have to register our own protocol
            HttpSecureProtocol psf = new HttpSecureProtocol();
            psf.setEnabledProtocols(sslEnabledProtocols);

            Protocol sslV3OnlyProtocol = new Protocol("https", (ProtocolSocketFactory) psf, 443);
            Protocol.registerProtocol("https", sslV3OnlyProtocol);
        }
    }

    protected List<Header> getDefaultHeaders() {
        List<Header> headers = new ArrayList<Header>();

        headers.add(new Header("Accept", "*/*"));
        headers.add(new Header("Connection", "close"));
        headers.add(new Header("Accept-Language", "en-US,en;q=0.8"));
        headers.add(new Header("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3"));
        return headers;
    }

    protected HttpClient createHttpClient() {
        return new HttpClient();
    }

    protected void initCredentials() {
        if (username != null && password != null) {
            AuthScope authscope = new AuthScope(
                    getAuthHost() == null ? AuthScope.ANY_HOST : getAuthHost(),
                    AuthScope.ANY_PORT);
            Credentials credentials = new UsernamePasswordCredentials(getUsername(), getPassword());
            httpClient.getState().setCredentials(authscope, credentials);
        }
    }

    protected void initSocketFactory() throws IOException {
        if (hostConfig != null) {
            URL trustKeyStoreUrl = null;
            if (hostConfig.getTrustKeyStoreResource() != null) {
                trustKeyStoreUrl = hostConfig.getTrustKeyStoreResource().getURL();
            }
            URL keyStoreUrl = null;
            if (hostConfig.getKeyStoreResource() != null) {
                keyStoreUrl = hostConfig.getKeyStoreResource().getURL();
            }
            AuthSSLProtocolSocketFactory factory = new AuthSSLProtocolSocketFactory(
                    keyStoreUrl, hostConfig.getKeyStorePassword(),
                    trustKeyStoreUrl, hostConfig.getTrustKeyStorePassword());
            if (sslEnabledProtocols != null) {
                factory.setEnabledProtocols(sslEnabledProtocols);
            }
            Protocol protocol = createProtocol(hostConfig, factory);
            httpClient.getHostConfiguration().setHost(hostConfig.getHost(),
                    hostConfig.getSecurePort(), protocol);
        }
    }

    private Protocol createProtocol(StoresAndSSLConfig config,
            ProtocolSocketFactory factory) {
        return new Protocol(config.getSecureSchema(), factory, config.getSecurePort());
    }

    public Class<?> getObjectType() {
        return HttpClient.class;
    }

    public boolean isSingleton() {
        return true;
    }

}
