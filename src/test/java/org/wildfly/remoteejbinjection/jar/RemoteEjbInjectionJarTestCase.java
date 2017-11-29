package org.wildfly.remoteejbinjection.jar;

import java.net.URL;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.remoteejbinjection.ejbs.StatelessEjb;
import org.wildfly.remoteejbinjection.ejbs.StatelessEjbInterface;
import org.wildfly.remoteejbinjection.utils.HttpUtils;
import org.wildfly.remoteejbinjection.utils.ShrinkWrapUtils;

@RunWith(Arquillian.class)
@RunAsClient
public class RemoteEjbInjectionJarTestCase {

    @Deployment(testable = false, name = "test.jar")
    public static JavaArchive deployRemoteEjbArchive() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackage(StatelessEjb.class.getPackage());
    }

    @Deployment(name = "testApp.war", testable = false)
    public static WebArchive deployTestApp() {

        return ShrinkWrap.create(WebArchive.class, "testApp.war")
                .addClass(StatelessServlet.class)
                .addClass(StatelessEjbInterface.class)
                .addAsResource( new StringAsset(""), "WEB-INF/beans.xml")
                .addAsResource(RemoteEjbInjectionJarTestCase.class.getPackage(), "remote-ejb-injection.xml", "remote-ejb-injection.xml")
                .addAsLibrary(ShrinkWrapUtils.createLibJar());
    }


    @ArquillianResource
    @OperateOnDeployment("testApp.war")
    private URL url;

    private String performCall(String name) throws Exception {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url + "/stateless");
        httpGet.addHeader("name", name);
        HttpResponse result = client.execute(httpGet);
        return HttpUtils.getContent(result);
    }
    @Test
    public void testStuff() throws Exception {
        Assert.assertEquals("Hello stu", performCall("stu"));
    }

}
