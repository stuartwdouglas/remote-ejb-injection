package org.wildfly.remoteejbinjection;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class XmlParsingTestCase {

    @Test
    public void testXmlParsing() throws Exception {

        List<RemoteEjbConfig> results =
                RemoteEjbInjectionParser.parse(getClass().getClassLoader().getResource("test-ejb-injection.xml"));

        Assert.assertEquals(2, results.size());
        RemoteEjbConfig res = results.get(0);
        Assert.assertEquals("myapp", res.getApp());
        Assert.assertEquals("mymodule", res.getModule());
        Assert.assertEquals("mydistinct", res.getDistinct());
        Assert.assertEquals("http://localhost:8080/wildfly-services", res.getProviderUri());

        res = results.get(1);
        Assert.assertEquals("otherapp", res.getApp());
        Assert.assertEquals("othermodule", res.getModule());
        Assert.assertEquals("otherdistinct", res.getDistinct());
        Assert.assertEquals("http://localhost:8080/wildfly-services", res.getProviderUri());
    }

}
