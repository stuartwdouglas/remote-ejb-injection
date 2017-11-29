package org.wildfly.remoteejbinjection.ejbs;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Remote(StatelessEjbInterface.class)
public class StatelessEjb implements StatelessEjbInterface {
    @Override
    public String sayHello(String message) {
        return "Hello " + message;
    }
}
