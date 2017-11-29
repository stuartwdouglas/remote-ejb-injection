package org.wildfly.remoteejbinjection;

import java.util.Collections;
import java.util.List;

/**
 * @author Stuart Douglas
 */
public class RemoteEjbConfig {

    private final String providerUri;
    private final String app;
    private final String module;
    private final String distinct;

    private final List<RemoteEjb> remoteEjbs;

    public RemoteEjbConfig(String providerUri, String app, String module, String distinct, List<RemoteEjb> remoteEjbs) {
        this.providerUri = providerUri;
        this.app = app;
        this.module = module;
        this.distinct = distinct;
        this.remoteEjbs = remoteEjbs;
    }

    public String getProviderUri() {
        return providerUri;
    }

    public String getApp() {
        return app;
    }

    public String getModule() {
        return module;
    }

    public String getDistinct() {
        return distinct;
    }

    public List<RemoteEjb> getRemoteEjbs() {
        return Collections.unmodifiableList(remoteEjbs);
    }

    public static class RemoteEjb {
        private final Class<?> remoteInterface;
        private final String ejbName;


        public RemoteEjb(Class<?> remoteInterface, String ejbName) {
            this.remoteInterface = remoteInterface;
            this.ejbName = ejbName;
        }

        public Class<?> getRemoteInterface() {
            return remoteInterface;
        }

        public String getEjbName() {
            return ejbName;
        }
    }
}
