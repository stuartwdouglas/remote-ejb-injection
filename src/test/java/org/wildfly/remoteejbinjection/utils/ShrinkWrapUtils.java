package org.wildfly.remoteejbinjection.utils;

import javax.enterprise.inject.spi.Extension;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.wildfly.remoteejbinjection.RemoteEjbInjectionExtension;

public class ShrinkWrapUtils {

    public static JavaArchive createLibJar() {
        return ShrinkWrap.create(JavaArchive.class, "remote-ejb-injection.jar")
                .addPackage(RemoteEjbInjectionExtension.class.getPackage())
                .addAsResource("META-INF/beans.xml", "META-INF/beans.xml")
                .addAsServiceProvider(Extension.class, RemoteEjbInjectionExtension.class);
    }

}
