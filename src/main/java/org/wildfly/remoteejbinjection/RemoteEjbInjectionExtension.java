package org.wildfly.remoteejbinjection;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.stream.XMLStreamException;

/**
 * TODO: qualifiers, steriotypes, names etc
 *
 * @author Stuart Douglas
 */
public class RemoteEjbInjectionExtension implements Extension {


    public void registerEjbs(@Observes AfterBeanDiscovery event) throws IOException, XMLStreamException, ClassNotFoundException {

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls = tccl.getResources("remote-ejb-injection.xml");
        List<RemoteEjbConfig> config = new ArrayList<>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            config.addAll(RemoteEjbInjectionParser.parse(url));
        }

        for (RemoteEjbConfig rc : config) {
            for(RemoteEjbConfig.RemoteEjb ejb : rc.getRemoteEjbs()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ejb:");
                if(rc.getApp() != null) {
                    sb.append(rc.getApp())
                    .append("/");
                } else {
                    sb.append("/");
                }
                sb.append(rc.getModule());
                if(rc.getDistinct() != null) {
                    sb.append("/")
                            .append(rc.getDistinct());
                } else {
                    sb.append("/");
                }
                sb.append("/").append(ejb.getEjbName()).append("!").append(ejb.getRemoteInterface().getName());
                String lookup = sb.toString();
                event.addBean(new Bean<Object>() {
                    public Class<?> getBeanClass() {
                        return ejb.getRemoteInterface();
                    }

                    public Set<InjectionPoint> getInjectionPoints() {
                        return Collections.emptySet();
                    }

                    public boolean isNullable() {
                        return false;
                    }

                    public Object create(CreationalContext<Object> creationalContext) {
                        try {
                            final Properties env = new Properties();
                            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
                            env.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
                            env.put(Context.PROVIDER_URL, rc.getProviderUri());
                            InitialContext namingContext = new InitialContext(env);
                            return namingContext.lookup(lookup);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    public void destroy(Object o, CreationalContext<Object> creationalContext) {

                    }

                    public Set<Type> getTypes() {
                        return Collections.singleton(ejb.getRemoteInterface());
                    }

                    public Set<Annotation> getQualifiers() {
                        Set<Annotation> qualifiers = new HashSet();
                        qualifiers.add(new AnnotationLiteral<Default>() {
                        });
                        qualifiers.add(new AnnotationLiteral<Any>() {
                        });
                        return qualifiers;
                    }

                    public Class<? extends Annotation> getScope() {
                        return Dependent.class;
                    }

                    public String getName() {
                        return null;
                    }

                    public Set<Class<? extends Annotation>> getStereotypes() {
                        return Collections.emptySet();
                    }

                    public boolean isAlternative() {
                        return false;
                    }
                });
            }
        }
    }

}
