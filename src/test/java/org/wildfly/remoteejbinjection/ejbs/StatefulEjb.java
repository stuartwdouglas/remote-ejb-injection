package org.wildfly.remoteejbinjection.ejbs;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

@Stateful
@Remote(StatefulEjbInterface.class)
public class StatefulEjb implements StatefulEjbInterface {

    int count;

    @Override
    public int increment(int amount) {
        count += amount;
        return count;
    }
}
