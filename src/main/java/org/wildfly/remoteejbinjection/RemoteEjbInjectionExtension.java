package org.wildfly.remoteejbinjection;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

/**
 * @author Stuart Douglas
 */
public class RemoteEjbInjectionExtension implements Extension {


    public void registerEjbs(@Observes AfterBeanDiscovery event) {
        event.addBean(new Bean<Object>() {
            public Class<?> getBeanClass() {
                return null;
            }

            public Set<InjectionPoint> getInjectionPoints() {
                return Collections.emptySet();
            }

            public boolean isNullable() {
                return false;
            }

            public Object create(CreationalContext<Object> creationalContext) {
                return null;
            }

            public void destroy(Object o, CreationalContext<Object> creationalContext) {

            }

            public Set<Type> getTypes() {
                return null;
            }

            public Set<Annotation> getQualifiers() {
                return null;
            }

            public Class<? extends Annotation> getScope() {
                return null;
            }

            public String getName() {
                return null;
            }

            public Set<Class<? extends Annotation>> getStereotypes() {
                return null;
            }

            public boolean isAlternative() {
                return false;
            }
        });
    }

}
