package org.wildfly.remoteejbinjection;

import java.util.Collections;
import java.util.List;

/**
 * @author Stuart Douglas
 */
public class RemoteEjbConfig {

    private final String providerUri;
    private final List<RemoteEjb> remoteEjbs;

    public RemoteEjbConfig(String providerUri, List<RemoteEjb> remoteEjbs) {
        this.providerUri = providerUri;
        this.remoteEjbs = remoteEjbs;
    }

    public String getProviderUri() {
        return providerUri;
    }

    public List<RemoteEjb> getRemoteEjbs() {
        return Collections.unmodifiableList(remoteEjbs);
    }

    public static class RemoteEjb {
        private final Class<?> remoteInterface;
        private final String lookup;

        public RemoteEjb(Class<?> remoteInterface, String lookup) {
            this.remoteInterface = remoteInterface;
            this.lookup = lookup;
        }

        public Class<?> getRemoteInterface() {
            return remoteInterface;
        }

        public String getLookup() {
            return lookup;
        }
    }
}
